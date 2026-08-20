package com.lumibooks.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lumibooks.backend.dto.order.request.OrderCreateRequest;
import com.lumibooks.backend.dto.order.request.OrderManagerUpdateRequest;
import com.lumibooks.backend.dto.order.request.OrderStatusUpdateRequest;
import com.lumibooks.backend.dto.order.response.CheckoutPreviewResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderAdminSummaryResponse;
import com.lumibooks.backend.dto.order.response.OrderClientDetailResponse;
import com.lumibooks.backend.dto.order.response.OrderClientResponse;
import com.lumibooks.backend.enums.OrderStatus;

/**
 * Interfaz para la gestión de órdenes.
 */
public interface OrderService {

    // =============== CLIENTE ================================

    /**
     * Retorna el preview del checkout con items del carrito, dirección y totales.
     * @param addressId identificador de la dirección (null usa la default)
     * @return preview del checkout
     */
    CheckoutPreviewResponse getCheckoutPreview(Long addressId);

    /**
     * Crea una nueva orden desde el carrito del usuario autenticado.
     * @param request datos del destinatario, dirección y pago
     * @return detalle de la orden creada
     * @throws BadRequestException si el carrito está vacío o el distrito no tiene envío disponible
     * @throws ResourceNotFoundException si la dirección no existe
     */
    OrderClientResponse createOrder(OrderCreateRequest request);

    /**
     * Retorna las órdenes del usuario autenticado.
     * @param status filtro opcional por estado
     * @param pageable paginación y ordenamiento
     * @return página de órdenes del cliente
     */
    Page<OrderClientResponse> getMyOrders(OrderStatus status, Pageable pageable);

    /**
     * Retorna el detalle de una orden del usuario autenticado.
     * @param orderId identificador de la orden
     * @return detalle de la orden
     * @throws ResourceNotFoundException si la orden no existe o no pertenece al usuario
     */
    OrderClientDetailResponse getMyOrderDetail(Long orderId);

    // ============ GESTOR ============

    /**
     * Retorna las órdenes asignadas al gestor autenticado con filtros dinámicos.
     * @param search búsqueda genérica por número de orden, DNI o nombre del cliente
     * @param status filtro por estado
     * @param dateFrom filtro por fecha desde
     * @param dateTo filtro por fecha hasta
     * @param pageable paginación y ordenamiento
     * @return página de órdenes del gestor
     */
    Page<OrderAdminSummaryResponse> getManagerOrders(
            String search,
            OrderStatus status,
            java.time.LocalDate dateFrom,
            java.time.LocalDate dateTo,
            Pageable pageable);

    /**
     * Retorna el detalle de una orden asignada al gestor autenticado.
     * @param orderId identificador de la orden
     * @return detalle de la orden
     * @throws ResourceNotFoundException si la orden no existe o no está asignada al gestor
     */
    OrderAdminDetailResponse getManagerOrderDetail(Long orderId);

    /**
     * Cambia el estado de una orden asignada al gestor autenticado.
     * @param orderId identificador de la orden
     * @param request nuevo estado
     * @throws ResourceNotFoundException si la orden no existe o no está asignada al gestor
     * @throws BadRequestException si el cambio de estado no es válido
     */
    void updateOrderStatus(Long orderId, OrderStatusUpdateRequest request);

    // ============ ADMIN ============

    /**
     * Retorna todas las órdenes con filtros dinámicos para el panel de administración.
     * @param orderNumber búsqueda por número de orden
     * @param dni búsqueda por DNI
     * @param clientName búsqueda por nombre del cliente
     * @param status filtro por estado
     * @param managerId filtro por gestor
     * @param dateFrom filtro por fecha desde
     * @param dateTo filtro por fecha hasta
     * @param pageable paginación y ordenamiento
     * @return página de órdenes
     */
    Page<OrderAdminSummaryResponse> getOrdersAdmin(
            String orderNumber,
            String dni,
            String clientName,
            OrderStatus status,
            Long managerId,
            java.time.LocalDate dateFrom,
            java.time.LocalDate dateTo,
            Pageable pageable);

    /**
     * Retorna el detalle completo de una orden para el panel de administración.
     * @param orderId identificador de la orden
     * @return detalle de la orden
     * @throws ResourceNotFoundException si la orden no existe
     */
    OrderAdminDetailResponse getOrderDetailAdmin(Long orderId);

    /**
     * Reasigna el gestor de una orden.
     * @param orderId identificador de la orden
     * @param request nuevo gestor
     * @throws ResourceNotFoundException si la orden o el gestor no existen
     * @throws BadRequestException si el usuario no tiene rol de gestor o está inactivo
     */
    void updateOrderManager(Long orderId, OrderManagerUpdateRequest request);

}