package com.lumibooks.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.order.request.OrderCreateRequest;
import com.lumibooks.backend.dto.order.request.OrderManagerUpdateRequest;
import com.lumibooks.backend.dto.order.request.OrderStatusUpdateRequest;
import com.lumibooks.backend.dto.order.response.CheckoutPreviewResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminSummaryResponse;
import com.lumibooks.backend.dto.order.response.OrderClientDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderClientResponse;
import com.lumibooks.backend.entity.Address;
import com.lumibooks.backend.entity.Book;
import com.lumibooks.backend.entity.Cart;
import com.lumibooks.backend.entity.CartItem;
import com.lumibooks.backend.entity.Order;
import com.lumibooks.backend.entity.OrderItem;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.ActionType;
import com.lumibooks.backend.enums.EntityType;
import com.lumibooks.backend.enums.OrderStatus;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.mapper.OrderMapper;
import com.lumibooks.backend.repository.AddressRepository;
import com.lumibooks.backend.repository.BookRepository;
import com.lumibooks.backend.repository.CartItemRepository;
import com.lumibooks.backend.repository.CartRepository;
import com.lumibooks.backend.repository.OrderItemRepository;
import com.lumibooks.backend.repository.OrderRepository;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.security.AuthenticatedUserProvider;
import com.lumibooks.backend.service.ActionLogService;
import com.lumibooks.backend.service.NotificationService;
import com.lumibooks.backend.service.OrderService;
import com.lumibooks.backend.specification.OrderSpecification;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gestión de órdenes.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final NotificationService notificationService;
    private final ActionLogService actionLogService;

    // ===================== CLIENTE ===========================================

    // ============ Método para obtener el preview del checkout ============
    @Override
    public CheckoutPreviewResponse getCheckoutPreview(Long addressId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = getCartOrThrow(user);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        Address address = getAddressOrThrow(addressId, user);

        return orderMapper.toCheckoutPreviewResponse(cart, address);
    }

    // ====== Método para crear una orden desde el carrito del usuario =====
    @Override
    @Transactional
    public OrderClientResponse createOrder(OrderCreateRequest request) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Cart cart = getCartOrThrow(user);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        Address address = getAddressOrThrow(request.getAddressId(), user);
        validateShippingAvailable(address);

        for (CartItem cartItem : cart.getItems()) {
            validateStock(cartItem.getBook(), cartItem.getQuantity());
        }

        BigDecimal shippingCost = address.getDistrict().getShippingCost();
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal total = subtotal.add(shippingCost);

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .assignedManager(findAvailableManager())
                .address(address)
                .recipientName(request.getRecipientName())
                .dni(request.getDni())
                .phone(request.getPhone())
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .total(total)
                .status(OrderStatus.PENDIENTE)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            Book book = cartItem.getBook();
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookRepository.save(book);

            orderItemRepository.save(OrderItem.builder()
                    .order(savedOrder)
                    .book(book)
                    .title(book.getTitle())
                    .coverImageUrl(book.getCoverImageUrl())
                    .unitPrice(book.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build());
        }

        cartItemRepository.deleteByCartId(cart.getId());

        return orderMapper.toClientResponse(savedOrder);
    }

    // ============= Método para obteenr órdenes del usuario ===============
    @Override
    public Page<OrderClientResponse> getMyOrders(OrderStatus status, Pageable pageable) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        // Si se especifica un estado, filtrar por ese estado, sino retornar todas las
        // órdenes del usuario
        if (status != null) {
            return orderRepository.findByUserIdAndStatus(user.getId(), status, pageable)
                    .map(orderMapper::toClientResponse);
        }
        // Retornar todas las órdenes del usuario sin filtrar por estado
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(orderMapper::toClientResponse);
    }

    // ===== Método para obtener el detalle de una orden del usuario =======
    @Override
    public OrderClientDetailResponse getMyOrderDetail(Long orderId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        // Cargar la orden con todos los detalles para evitar problemas de lazy loading
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + orderId));
        // Verificar que la orden pertenece al usuario autenticado
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Orden no encontrada con id: " + orderId);
        }
        return orderMapper.toClientDetailResponse(order);
    }

    // ===================== GESTOR ===========================================

    // ==== Método para obtener órdenes asignadas al gestor con filtros =====
    @Override
    public Page<OrderAdminSummaryResponse> getManagerOrders(
            String search,
            OrderStatus status, LocalDate dateFrom, LocalDate dateTo,
            Pageable pageable) {

        User manager = authenticatedUserProvider.getAuthenticatedUser();
        Specification<Order> spec = OrderSpecification.assignedToManager(manager.getId());

        if (search != null && !search.isBlank()) {
            spec = spec.and(OrderSpecification.hasSearch(search));
        }
        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }
        if (dateFrom != null) {
            spec = spec.and(OrderSpecification.createdAfter(dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and(OrderSpecification.createdBefore(dateTo));
        }
        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toAdminSummaryResponse);
    }

    // ====== Método para obtener el detalle de una orden asignada al gestor =====
    @Override
    public OrderAdminDetailResponse getManagerOrderDetail(Long orderId) {
        User manager = authenticatedUserProvider.getAuthenticatedUser();
        // Cargar la orden con todos los detalles para evitar problemas de lazy loading
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + orderId));
        // Verificar que la orden está asignada al gestor autenticado
        if (order.getAssignedManager() == null ||
                !order.getAssignedManager().getId().equals(manager.getId())) {
            throw new ResourceNotFoundException("Orden no encontrada con id: " + orderId);
        }
        return orderMapper.toAdminDetailResponse(order);
    }

    // ====== Método para cambiar el estado de una orden asignada al gestor =====
    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        User manager = authenticatedUserProvider.getAuthenticatedUser();
        // Cargar la orden para verificar que existe
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + orderId));
        // Verificar que la orden está asignada al gestor autenticado
        if (order.getAssignedManager() == null ||
                !order.getAssignedManager().getId().equals(manager.getId())) {
            throw new ResourceNotFoundException("Orden no encontrada con id: " + orderId);
        }
        // Validar que la transición de estado sea válida según el flujo definido
        validateStatusTransition(order.getStatus(), request.getStatus());
        // Actualizar el estado de la orden
        order.setStatus(request.getStatus());
        orderRepository.save(order);

        actionLogService.log(
                ActionType.CAMBIAR_ESTADO,
                EntityType.ORDER,
                order.getId(),
                "Cambió el estado de la orden '" + order.getOrderNumber() + "' a "
                        + request.getStatus().getDisplayName());

        if (request.getStatus() == OrderStatus.ENTREGADO) {
            notificationService.sendNotification(
                    order.getUser(),
                    "¡Tu orden fue entregada!",
                    "Tu orden " + order.getOrderNumber()
                            + " ha sido entregada. Ya puedes dejar una reseña de tus libros.");
        } else {
            notificationService.sendNotification(
                    order.getUser(),
                    "Estado de tu orden actualizado",
                    "Tu orden " + order.getOrderNumber() + " ahora está en estado: "
                            + request.getStatus().getDisplayName());
        }
    }

    // ====================== ADMIN ===========================================

    // ====== Método para obtener todas las órdenes con filtros dinámicos para admin
    // =====
    @Override
    public Page<OrderAdminSummaryResponse> getOrdersAdmin(
            String search, OrderStatus status, Long managerId,
            LocalDate dateFrom, LocalDate dateTo,
            Pageable pageable) {
        // Construir la especificación dinámica para filtrar las órdenes según los
        // parámetros recibidos
        Specification<Order> spec = Specification.unrestricted();

        if (search != null && !search.isBlank()) {
            spec = spec.and(OrderSpecification.hasSearch(search));
        }
        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }
        if (managerId != null) {
            spec = spec.and(OrderSpecification.hasManager(managerId));
        }
        if (dateFrom != null) {
            spec = spec.and(OrderSpecification.createdAfter(dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and(OrderSpecification.createdBefore(dateTo));
        }

        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toAdminSummaryResponse);
    }

    // ====== Método para obtener el detalle de una orden para admin =====
    @Override
    public OrderAdminDetailResponse getOrderDetailAdmin(Long orderId) {
        // Cargar la orden con todos los detalles para evitar problemas de lazy loading
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + orderId));
        return orderMapper.toAdminDetailResponse(order);
    }

    // ====== Método para actualizar el gestor asignado a una orden ======
    @Override
    @Transactional
    public void updateOrderManager(Long orderId, OrderManagerUpdateRequest request) {
        // Cargar la orden para verificar que existe
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden no encontrada con id: " + orderId));
        // Cargar el nuevo gestor para verificar que existe y tiene rol de gestor
        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gestor no encontrado con id: " + request.getManagerId()));
        // Validar que el usuario tiene rol de gestor
        if (manager.getRole() != RoleUser.GESTOR) {
            throw new BadRequestException("El usuario no tiene rol de gestor");
        }
        // Validar que el gestor está activo
        if (!manager.isActive()) {
            throw new BadRequestException("El gestor está inactivo");
        }
        // Actualizar el gestor asignado a la orden
        order.setAssignedManager(manager);
        orderRepository.save(order);

        actionLogService.log(
                ActionType.ASIGNAR,
                EntityType.ORDER,
                order.getId(),
                "Reasignó la orden '" + order.getOrderNumber() + "' al gestor '"
                        + manager.getFullName() + "'");

        notificationService.sendNotification(
                manager,
                "Nuevo pedido asignado",
                "El administrador te ha asignado el pedido " + order.getOrderNumber());
    }

    // ============ HELPERS PRIVADOS ===========================================

    // Obtiene el carrito del usuario o lanza excepción si no existe
    private Cart getCartOrThrow(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("No tienes un carrito activo"));
    }

    // Obtiene la dirección del usuario o lanza excepción si no existe
    private Address getAddressOrThrow(Long addressId, User user) {
        return addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dirección no encontrada con id: " + addressId));
    }

    // Valida que el distrito de la dirección tenga envío disponible
    private void validateShippingAvailable(Address address) {
        if (!address.getDistrict().isShippingAvailable()) {
            throw new BadRequestException(
                    "No hay envío disponible para la dirección seleccionada");
        }
    }

    // Calcula el subtotal de todo el carrito (sin incluir el costo de envío)
    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Valida que el libro tenga stock suficiente para la cantidad solicitada
    private void validateStock(Book book, Integer quantity) {
        if (book.getStock() < quantity) {
            throw new BadRequestException(
                    "Stock insuficiente para el libro: " + book.getTitle() +
                            ". Stock disponible: " + book.getStock());
        }
    }

    // Asigna automáticamente el gestor con menos órdenes activas
    private User findAvailableManager() {
        Long managerId = orderRepository.findManagerIdWithLeastActiveOrders(
                OrderStatus.getActiveStatuses(), RoleUser.GESTOR);

        return userRepository.findById(managerId).orElseThrow();
    }

    // Genera un número de orden único con formato: LB-YYYYMMDD-UNIQUEID
    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String unique = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "LB-" + date + "-" + unique;
    }

    // Valida que la transición de estado sea válida según el flujo definido
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDIENTE -> next == OrderStatus.EN_PREPARACION;
            case EN_PREPARACION -> next == OrderStatus.ENVIADO;
            case ENVIADO -> next == OrderStatus.ENTREGADO;
            case ENTREGADO -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    "No se puede cambiar el estado de " + current + " a " + next);
        }
    }

}