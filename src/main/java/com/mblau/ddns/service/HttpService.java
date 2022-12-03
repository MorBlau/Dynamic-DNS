package com.mblau.ddns.service;

import com.mblau.ddns.dto.Result;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Log4j2
@Service
public class HttpService {

    private HttpClient client;

    private HttpService() {
        init();
    }

    @PostConstruct
    private void init() {
        client = HttpClient.newBuilder()
                .build();
    }

    public Result getCall(String url) throws URISyntaxException, IOException, InterruptedException {
        return getCall(url, null);
    }

    public Result getCall(String url, Map<String, String> headers) throws URISyntaxException, IOException, InterruptedException {
        return getCall(url, headers, null);
    }

    public Result getCall(String url, Map<String, String> headers, Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        return performCall(url, headers, params, null, this::setGetMethod);
    }

    public Result performCall(String url, Map<String, String> headers, Map<String, String> params,
                              String body, BiConsumer<HttpRequest.Builder, String> methodSetter) throws URISyntaxException, IOException, InterruptedException {
        String urlWithParams = url + buildParams(params);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams));
        setHeaders(builder, headers);
        methodSetter.accept(builder, body);
        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new Result(response.body(), null, response.statusCode());
    }

    private void setGetMethod(HttpRequest.Builder builder, String body) {
        builder.GET();
    }

    private void setPutMethod(HttpRequest.Builder builder, String body) {
        builder.PUT(HttpRequest.BodyPublishers.ofString(body));
    }

    private void setPostMethod(HttpRequest.Builder builder, String body) {
        builder.POST(HttpRequest.BodyPublishers.ofString(body));
    }

    @NonNull
    private String buildParams(@Nullable Map<String, String> params) {
        if (CollectionUtils.isEmpty(params))
            return "";

        return "?" +
                params.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
    }

    private void setHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
        if (headers == null)
            return;

        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }
    }

    public Result putCall(String url, String body, Map<String, String> headers,
                              Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        return performCall(url, headers, params, body, this::setPutMethod);
    }

    public Result postCall(String url, String body, Map<String, String> headers,
                              Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        return performCall(url, headers, params, body, this::setPostMethod);
    }
}
