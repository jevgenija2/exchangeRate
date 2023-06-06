package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class WebClientConfig {
    @Value("${free-currency.key}")
    private String apiKey;

    @Value("${free-currency.url}")
    private String baseUrl;

    @Value("${free-currency.currencies}")
    private String currencies; //Can be null

    @Bean
    public WebClient webclient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .filter(addQueryParameters(apiKey, currencies)) //will add all the required query params to request
                .build();
    }

    private ExchangeFilterFunction addQueryParameters(String apiKey, String currencies) {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(request.url());

            uriBuilder.queryParam("apikey", apiKey);

            if(currencies != null && !currencies.isEmpty()) {
                uriBuilder.queryParam("currencies", currencies);
            }

            ClientRequest updatedRequest = ClientRequest.from(request)
                    .url(uriBuilder.build().toUri())
                    .build();

            return Mono.just(updatedRequest);
        });
    }

}
