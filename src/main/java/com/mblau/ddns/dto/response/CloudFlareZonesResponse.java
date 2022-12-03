package com.mblau.ddns.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudFlareZonesResponse {
    @JsonProperty("result")
    private List<DomainInfo> result;

    @Setter
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DomainInfo {
        private String name;
        @JsonProperty("id")
        private String zoneId;
        private String status;
    }
}
