package com.mblau.ddns.service;

import com.mblau.ddns.dto.request.DnsRecordRequest;
import com.mblau.ddns.dto.response.DnsRecordResponse;
import com.mblau.ddns.dto.Result;
import com.mblau.ddns.dto.response.CloudFlareDnsRecordsResponse;
import com.mblau.ddns.dto.response.CloudFlareZonesResponse;
import com.mblau.ddns.exception.cloudflare.DnsRecordNotFoundException;
import com.mblau.ddns.exception.cloudflare.ZoneNotFoundException;
import com.mblau.ddns.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class CloudflareService implements IDnsService {

    private static final String ZONES = "zones";
    private static final String DNS_RECORDS = "dns_records";

    @Value("${cloudflare.api.token}")
    private String token;
    @Value("${cloudflare.api.baseUrl}")
    private String baseUrl;
    @Value("${cloudflare.api.domainName}")
    private String domainName;
    @Autowired
    private HttpService httpService;

    public void notifyIpChanged(String ip) throws Exception {
        String zoneId = resolveZoneId(domainName);
        DnsRecordResponse dnsRecord = resolveDnsRecord(zoneId);
        if (!Objects.equals(dnsRecord.ip(), ip))
            updateDnsRecordIp(dnsRecord, zoneId, ip);
        else
            log.info("DNS record already contains correct IP");
    }

    private DnsRecordResponse resolveDnsRecord(String zoneId) throws URISyntaxException, IOException, InterruptedException, DnsRecordNotFoundException {
        String endpoint = ZONES + "/" + zoneId + "/" + DNS_RECORDS;
        Map<String, String> params = new HashMap<>();
        params.put("match", "all");
        params.put("name", domainName);
        params.put("type", "A");
        String result = getRequest(endpoint, params);
        CloudFlareDnsRecordsResponse recordsResponse = JsonUtil.toModel(result, CloudFlareDnsRecordsResponse.class);
        return recordsResponse.dnsRecordList().stream()
                .filter(dnsRecord -> dnsRecord.zoneId().equals(zoneId))
                .filter(dnsRecord -> dnsRecord.name().equals(domainName))
                .findFirst()
                .orElseThrow(() -> new DnsRecordNotFoundException("DNS record not found for zone " + zoneId + ". Total records found: " + recordsResponse.dnsRecordList().size()));
    }

    private void updateDnsRecordIp(DnsRecordResponse dnsRecord, String zoneId, String ip) throws IOException, URISyntaxException, InterruptedException {
        String endpoint = ZONES + "/" + zoneId + "/" + DNS_RECORDS + "/" + dnsRecord.id();
        DnsRecordRequest dnsRecordRequest = new DnsRecordRequest(domainName, "A", ip, 1, true);
        String body = JsonUtil.toJson(dnsRecordRequest);
        String result = putRequest(endpoint, body);
    }

    private String resolveZoneId(String domainName) throws ZoneNotFoundException, URISyntaxException, IOException, InterruptedException {
        String result = getRequest(ZONES, Collections.singletonMap("name", domainName));
        CloudFlareZonesResponse zonesResponse = JsonUtil.toModel(result, CloudFlareZonesResponse.class);
        return zonesResponse.getResult().stream()
                .filter(domainInfo -> domainInfo.getName().equals(domainName))
                .findFirst()
                .map(CloudFlareZonesResponse.DomainInfo::getZoneId)
                .orElseThrow(() -> new ZoneNotFoundException("Zone not found for domain " + domainName));
    }

    private String getRequest(@NonNull String endpoint) throws URISyntaxException, IOException, InterruptedException {
        return getRequest(endpoint, null);
    }

    private String getRequest(@NonNull String endpoint, Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        String url = buildUrl(endpoint);
        Map<String, String> headers = getAuthHeader();
        Result result = httpService.getCall(url, headers, params);
        return result.error() != null ? result.error() : result.result();
    }

    private String putRequest(@NonNull String endpoint, String body) throws URISyntaxException, IOException, InterruptedException {
        return putRequest(endpoint, body, null);
    }

    private String putRequest(@NonNull String endpoint, String body, Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        String url = buildUrl(endpoint);
        Map<String, String> headers = getAuthHeader();
        Result result = httpService.putCall(url, body, headers, params);
        return result.error() != null ? result.error() : result.result();
    }

    private String buildUrl(String endpoint) {
        return baseUrl + "/" + endpoint;
    }

    private Map<String, String> getAuthHeader() {
        Map<String, String> headers = new HashMap<>();
        return addAuthHeader(headers);
    }

    private Map<String, String> addAuthHeader(@NonNull Map<String, String> headers) {
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

}
