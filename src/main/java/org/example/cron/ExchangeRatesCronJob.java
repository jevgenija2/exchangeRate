package org.example.cron;

import lombok.AllArgsConstructor;
import org.example.model.response.FreeCurrencyApiResponse;
import org.example.service.ExchangeRatesService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@AllArgsConstructor
public class ExchangeRatesCronJob {
    private WebClient webClient;
    private ExchangeRatesService service;

    @Scheduled(cron = "0 0 1 * * *") //Executes at 01:00 every day
    public void getDailyExchangeRatesUpdate() {
        // Handle the response
        webClient.get()
                .uri("/latest")
                .retrieve()
                .bodyToMono(FreeCurrencyApiResponse.class)
                .subscribe(rates -> {
                    service.saveExchangeRates(rates.getData(), true);
                    System.out.println("Rates updated.");
                }, error -> {
                    System.out.println("Something went wrong during request to freecurrencyapi: " + error.getMessage());
                });
    }

    @Scheduled(cron = "0 15 1 * * *")
    public void removeOlderEntries() {
        try {
            service.removeOlderEntries();
        } catch (Exception e) {
            System.out.println("Something went wrong, during older entries removal: " + e.getMessage());
        }
    }

    public void populateData() {
        webClient.get()
                .uri("/latest")
                .retrieve()
                .bodyToMono(FreeCurrencyApiResponse.class)
                .subscribe(rates -> {
                            service.saveExchangeRates(rates.getData(), false);
                        }, error -> {
                            System.out.println("Something went wrong during request to freecurrencyapi: " + error.getMessage());
                        }
                );
    }
}
