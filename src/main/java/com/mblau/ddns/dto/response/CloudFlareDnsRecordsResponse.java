package com.mblau.ddns.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CloudFlareDnsRecordsResponse(@JsonProperty("result")
                                           List<DnsRecordResponse> dnsRecordList) {}
