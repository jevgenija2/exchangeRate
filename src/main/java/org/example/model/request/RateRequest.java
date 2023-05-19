package org.example.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
@Data
public class RateRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;
    String currency;
}
