package org.example.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateResponse {

    private boolean success;

    private String error;

    private String baseCurrency;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;

    private List<Map<String, String>> currencies;
}
