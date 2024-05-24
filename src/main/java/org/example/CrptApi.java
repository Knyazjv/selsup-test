package org.example;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CrptApi {
    private static final String API_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final long timeInterval;
    private final int requestLimit;
    private long lastRequestTime;
    private final AtomicInteger currentRequests;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeInterval = timeUnit.toMillis(1);
        this.httpClient = HttpClient.newBuilder().build();
        this.objectMapper = new ObjectMapper();
        this.requestLimit = requestLimit;
        this.lastRequestTime = System.currentTimeMillis();
        this.currentRequests = new AtomicInteger(0);
    }

    public HttpResponse<String> createDocument(DocLpIntroduceGoods document, String signature)
            throws IOException, InterruptedException {

        while (currentRequests.get() >= requestLimit) {
            long remainingTime = timeInterval - (System.currentTimeMillis() - lastRequestTime);
            if (remainingTime > 0) {
                Thread.sleep(remainingTime);
            } else {
                lastRequestTime = System.currentTimeMillis();
                currentRequests.set(0);
            }
        }

        try {
            String requestBody = objectMapper.writeValueAsString(document);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + signature)
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } finally {
            currentRequests.incrementAndGet();
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class DocLpIntroduceGoods {
        private Description description;
        private String docId;
        private String docStatus;
        private String docType;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private String productionDate;
        private String productionType;
        private List<Product> products;
        private String regDate;
        private String regNumber;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Description {

        private String participantInn;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Product {

        private String certificateDocument;
        private String certificateDocumentDate;
        private String certificateDocumentNumber;
        private String ownerInn;
        private String producerInn;
        private String productionDate;
        private String tnvedCode;
        private String uitCode;
        private String uituCode;
    }
}
