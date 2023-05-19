package org.example.repository;

import org.example.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRatesHistoryRepository extends JpaRepository<ExchangeRateEntity, Long> {
    @Query("SELECT entity FROM ExchangeRateEntity entity WHERE entity.currency IN :reqCurrencies")
    List<ExchangeRateEntity> getExchangeRateEntitiesByCurrency(@Param("reqCurrencies") List<String> reqCurrencies);

    List<ExchangeRateEntity> getExchangeRateEntitiesByDate(LocalDate date);

    @Query("SELECT entity FROM ExchangeRateEntity entity WHERE entity.currency IN :reqCurrencies AND entity.date=:reqDate")
    List<ExchangeRateEntity> getExchangeRateEntityByDateAndCurrencies(@Param("reqDate") LocalDate reqDate,
                                                                    @Param("reqCurrencies") List<String> reqCurrencies);

    void deleteAllByDate(LocalDate date);

}
