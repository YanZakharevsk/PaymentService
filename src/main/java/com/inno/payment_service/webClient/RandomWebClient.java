package com.inno.payment_service.webClient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RandomWebClient {

    private final WebClient webClient;

    @Value(value = "${external.random.number.api.url}")
    private String randomApiURL;

    @Value(value = "${external.random.number.api.param.min}")
    private String numberMin;

    @Value(value = "${external.random.number.api.param.max}")
    private String numberMax;

    @Value(value = "${external.random.number.api.param.count}")
    private String numberCount;

    public Integer getRandomNumber() {
        try {
            String responseBody = webClient
                    .get()
                    .uri(randomApiURL + "?num={count}&min={min}&max={max}&col=1&base=10&format=plain&rnd=new",
                            numberCount, numberMin, numberMax)
                    .accept(MediaType.TEXT_PLAIN)
                    .retrieve()

                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ResponseStatusException(
                                            HttpStatusCode.valueOf(400), "Random API request failed: " + msg)))
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, response ->
                            response.bodyToMono(String.class)
                                    .flatMap(msg -> Mono.error(new ResponseStatusException(
                                            HttpStatusCode.valueOf(500), "Random API error: " + msg)))
                    )
                    .bodyToMono(String.class)
                    .block();

            if (responseBody == null || responseBody.isBlank()) {
                throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Random API returned empty result");
            }

            return Integer.parseInt(responseBody.trim());

        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(502), "External API error", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(503), "Random API service is unavailable", e);
        }
    }
}