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
    public void getDailyExchangeRatesUpdate(){
        // Handle the response
        webClient.get()
                .uri("/latest")
                .retrieve()
                .bodyToMono(FreeCurrencyApiResponse.class)
                .subscribe( rates -> {
                    service.saveExchangeRates(rates.getData(), true);
                    System.out.println("Rates updated.");
                        }
                );
    }

    @Scheduled(cron = "0 15 1 * * *")
    public void removeOlderEntries() {
        service.removeOlderEntries();
        System.out.println("Entries that are older than 7 days deleted.");
    }

    public void populateData() {
        webClient.get()
                .uri("/latest")
                .retrieve()
                .bodyToMono(FreeCurrencyApiResponse.class)
                .subscribe( rates -> {
                            service.saveExchangeRates(rates.getData(), false);
                            System.out.println("Data populated.");
                        }
                );
    }

}
