package com.mblau.ddns.exception.cloudflare;

public class DnsRecordNotFoundException extends Exception {
    public DnsRecordNotFoundException(String message) {
        super(message);
    }
}
