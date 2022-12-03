package com.mblau.ddns.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DnsRecordResponse(String id, @JsonProperty("zone_id") String zoneId,
                                String name, @JsonProperty("content") String ip) {
}
