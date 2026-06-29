package com.eventrouting.upstream.dto;

import java.util.Map;

import com.eventrouting.upstream.enums.Currency;
import com.eventrouting.upstream.enums.EventType;
import com.eventrouting.upstream.enums.PaymentMode;

import lombok.Data;

@Data
public class TransactionRequest {
    private EventType eventType;
    private PaymentMode paymentMode;
    private Currency currency;
    private Map<String, String> metadata;
}
