package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.cron.ExchangeRatesCronJob;
import org.example.model.request.RateRequest;
import org.example.model.response.ExchangeRateResponse;
import org.example.service.ExchangeRatesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(("/api/v1"))
public class ExchangeRatesController {
    private static final String ERROR_MESSAGE = "Something went wrong: ";

    private ExchangeRatesService service;
    private ExchangeRatesCronJob cron;

    @GetMapping(value = "/currencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExchangeRateResponse getRate(@RequestBody RateRequest request) {
        return service.getExchangeRates(request);
    }

    //Populates data for the last 8 days (same rates, but different date)
    @PostMapping(value = "/populate")
    public ResponseEntity<String> populateRates() {
        try {
            cron.populateData();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Data successfully populated.");

        } catch (Exception e){

            return ResponseEntity
                    .internalServerError()
                    .body(ERROR_MESSAGE + e.getMessage());
        }
    }

    //Remove entries that are older than 7 days. This cron is also scheduled to remove data on daily basis, 15 minutes after data is updated.
    @DeleteMapping(value = "/clean")
    public ResponseEntity<String> cleanDb() {
        try {
            cron.removeOlderEntries();

            return ResponseEntity
                    .ok("Successfully deleted old entries.");
        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError().body(ERROR_MESSAGE + e.getMessage());
        }
    }
}
