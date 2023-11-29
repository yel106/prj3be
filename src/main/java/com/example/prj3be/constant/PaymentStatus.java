package com.example.prj3be.constant;

public enum PaymentStatus {
    CARD("카드"),CASH("현금"),POINT("포인트");
    private String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
