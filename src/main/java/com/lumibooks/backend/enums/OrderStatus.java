package com.lumibooks.backend.enums;

import java.util.List;

public enum OrderStatus {

    PENDIENTE,
    EN_PREPARACION,
    ENVIADO,
    ENTREGADO;

    // Método para obtener los estados que se consideran "activos" en el proceso de una orden
    public static List<OrderStatus> getActiveStatuses() {
        return List.of(PENDIENTE, EN_PREPARACION, ENVIADO);
    }
    
}