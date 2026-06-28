package com.eventrouting.upstream.dto;

import java.util.Map;

import com.eventrouting.upstream.enums.Currency;
import com.eventrouting.upstream.enums.EventType;
import com.eventrouting.upstream.enums.PaymentMode;
import com.eventrouting.upstream.enums.PaymentStatus;

import lombok.Data;

@Data
public class EventRequest {
    private String eventId;
    private EventType eventType;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String merchantId;
    private String customerId;
    private String transactionId;
    private String amount;
    private Currency currency;
    private Map<String, String> metadata;
}
