package org.example.service;

import lombok.AllArgsConstructor;
import org.example.entity.ExchangeRateEntity;
import org.example.model.request.RateRequest;
import org.example.model.response.ExchangeRateResponse;
import org.example.repository.ExchangeRatesHistoryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class ExchangeRatesService {
    private static final String CURRENT_BASE_CURRENCY = "EUR";

    private ExchangeRatesHistoryRepository historyRepository;

    @Transactional
    public void saveExchangeRates(Map<String, Double> exchangeRates, boolean isDataUpdate) {
        historyRepository.saveAll(convertToEntity(exchangeRates, isDataUpdate));
    }

    @Transactional
    public void removeOlderEntries(){
        historyRepository.deleteAllByDate(LocalDate.now().minusDays(8));
    }

    public ExchangeRateResponse getExchangeRates(RateRequest request) {
        List<ExchangeRateEntity> entityList;
        boolean success = false;
        LocalDate date = null;
        String error = null;

        if (request.getCurrency() == null && request.getDate() == null) {

            entityList = getAllRates();

        } else if (request.getDate() != null && request.getCurrency() != null &&
        !request.getCurrency().isEmpty()) {

            date = request.getDate();
            entityList = getRatesByDateAndCurrencies(request.getDate(), request.getCurrency());

            if(entityList.isEmpty()) {
                error = "No data for this date and currencies";
            }

        } else if (request.getDate() != null) {

            date = request.getDate();
            entityList = getAllRatesByDate(request.getDate());

            if (entityList.isEmpty()) {
                error = "No data for this date";
            }

        } else {

            entityList = getAllRatesByCurrencies(request.getCurrency());

            if(entityList.isEmpty()) {
                error = "No such currency";
            }
        }

        if(!entityList.isEmpty()) {
            success = true;
        }

        return ExchangeRateResponse.builder()
                .success(success)
                .error(error)
                .baseCurrency(success ? CURRENT_BASE_CURRENCY : null)
                .date(date)
                .currencies(entityList.stream()
                        .map(entity -> Map.of(entity.getCurrency(), entity.getRate()))
                        .collect(toList()))
                .build();
    }

    private List<ExchangeRateEntity> convertToEntity(Map<String, Double> exchangeRates, boolean isDataUpdate) {
        List<ExchangeRateEntity> entityList = new ArrayList<>();
        LocalDate today = LocalDate.parse(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (isDataUpdate) {

            for (var entry : exchangeRates.entrySet()) {
                entityList.add(ExchangeRateEntity.builder()
                        .date(today)
                        .currency(entry.getKey())
                        .rate(entry.getValue().toString())
                        .build());
            }

        } else {
            LocalDate daysBefore = today.minusDays(8);

            while (!daysBefore.equals(today.plusDays(1))) {
                for (var entry : exchangeRates.entrySet()) {
                    entityList.add(ExchangeRateEntity.builder()
                            .date(daysBefore)
                            .currency(entry.getKey())
                            .rate(entry.getValue().toString())
                            .build());
                }
                daysBefore = daysBefore.plusDays(1);
            }
        }

        return entityList;
    }

    private List<ExchangeRateEntity> getRatesByDateAndCurrencies(LocalDate date, String currency) {
        return historyRepository.getExchangeRateEntityByDateAndCurrencies(date,
                Stream.of(currency
                                .replaceAll("\\s", "")
                                .split(","))
                        .collect(toList())
        );
    }
    private List<ExchangeRateEntity> getAllRatesByDate(LocalDate date) {
        return historyRepository.getExchangeRateEntitiesByDate(date);
    }

    private List<ExchangeRateEntity> getAllRatesByCurrencies(String currencies) {
        return historyRepository.getExchangeRateEntitiesByCurrency(
                Stream.of(currencies
                                .replaceAll("\\s", "")
                                .split(","))
                        .collect(toList()));
    }

    private List<ExchangeRateEntity> getAllRates() {
        return historyRepository.findAll();
    }
}
