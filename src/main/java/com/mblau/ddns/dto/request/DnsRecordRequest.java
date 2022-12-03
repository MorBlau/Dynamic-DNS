package com.mblau.ddns.dto.request;

public record DnsRecordRequest(String name, String type, String content, int ttl, boolean proxied) {}
