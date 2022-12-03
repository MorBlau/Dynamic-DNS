package com.mblau.ddns.exception.cloudflare;

public class ZoneNotFoundException extends Exception {
    public ZoneNotFoundException(String message) {
        super(message);
    }
}
