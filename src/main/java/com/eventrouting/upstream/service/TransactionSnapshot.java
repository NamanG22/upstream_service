package com.eventrouting.upstream.service;

import java.util.Map;

import com.eventrouting.upstream.enums.Currency;
import com.eventrouting.upstream.enums.EventType;
import com.eventrouting.upstream.enums.PaymentMode;

public record TransactionSnapshot(
        String transactionId,
        EventType eventType,
        PaymentMode paymentMode,
        String merchantId,
        String customerId,
        String amount,
        Currency currency,
        Map<String, String> metadata) {
}
