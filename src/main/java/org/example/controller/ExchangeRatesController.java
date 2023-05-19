package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.cron.ExchangeRatesCronJob;
import org.example.model.request.RateRequest;
import org.example.model.response.ExchangeRateResponse;
import org.example.service.ExchangeRatesService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(("/api/v1"))
public class ExchangeRatesController {

    private ExchangeRatesService service;
    private ExchangeRatesCronJob cron;

    @GetMapping(value = "/currencies", produces = "application/json")
    public ExchangeRateResponse getRate(@RequestBody RateRequest request) {
        return service.getExchangeRates(request);
    }

    @PostMapping(value = "/populate", produces = MediaType.APPLICATION_JSON_VALUE)
    public void populateRates() {
        cron.populateData();
    }

    @PostMapping(value = "/clean", produces = MediaType.APPLICATION_JSON_VALUE)
    public void cleanDb() {
        cron.removeOlderEntries();
    }

}
