package org.sysRestaurante.dao;

import java.time.LocalDateTime;

public class KitchenOrderDao extends ComandaDao {
    public enum KitchenOrderStatus {
        WAITING(1), LATE(2), COOKING(3), DELIVERED(4), CANCELLED(5), RETURNED(6), READY(7);

        final private int value;
        KitchenOrderStatus(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        public static KitchenOrderStatus getByValue(int value) {
            for (KitchenOrderStatus status : KitchenOrderStatus.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant with value " + value);
        }
    }
    private int idKitchenOrder;
    private LocalDateTime kitchenOrderDateTime;
    private LocalDateTime finalKitchenOrderDateTime;
    private KitchenOrderStatus kitchenOrderStatus;
    private String kitchenOrderDetails;

    public KitchenOrderDao() {
        this.kitchenOrderDateTime = LocalDateTime.now();
        this.kitchenOrderStatus = KitchenOrderStatus.WAITING;
    }

    public int getIdKitchenOrder() {
        return idKitchenOrder;
    }

    public void setIdKitchenOrder(int idKitchenOrder) {
        this.idKitchenOrder = idKitchenOrder;
    }

    public LocalDateTime getKitchenOrderDateTime() {
        return kitchenOrderDateTime;
    }

    public void setKitchenOrderDateTime(LocalDateTime kitchenOrderDateTime) {
        this.kitchenOrderDateTime = kitchenOrderDateTime;
    }

    public LocalDateTime getFinalKitchenOrderDateTime() {
        return finalKitchenOrderDateTime;
    }

    public void setFinalKitchenOrderDateTime(LocalDateTime finalKitchenOrderDateTime) {
        this.finalKitchenOrderDateTime = finalKitchenOrderDateTime;
    }

    public KitchenOrderStatus getKitchenOrderStatus() {
        return kitchenOrderStatus;
    }

    public void setKitchenOrderStatus(KitchenOrderStatus kitchenOrderStatus) {
        this.kitchenOrderStatus = kitchenOrderStatus;
    }

    public String getKitchenOrderDetails() {
        return kitchenOrderDetails;
    }

    public void setKitchenOrderDetails(String kitchenOrderDetails) {
        this.kitchenOrderDetails = kitchenOrderDetails;
    }
}
