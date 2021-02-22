package com.example.api.event;

import com.example.api.core.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@NoArgsConstructor @Getter
public class Event<K ,P> {

    public enum Type{ CREATE, DELETE }

    private Event.Type eventType;
    private K key;
    private P data;
    private LocalDateTime eventCreatedAt;

    public Event(Type eventType, K key, P data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = now();
    }
}
