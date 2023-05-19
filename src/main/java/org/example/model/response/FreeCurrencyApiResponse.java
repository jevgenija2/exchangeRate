package org.example.model.response;

import lombok.Data;
import java.util.Map;

@Data
public class FreeCurrencyApiResponse {
    private Map<String, Double> data;
}
