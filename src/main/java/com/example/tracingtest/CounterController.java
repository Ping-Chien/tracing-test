package com.example.tracingtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;


@RestController
public class CounterController {
    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${other.pod.url}")
    private String otherPodUrl;

    private static final String EXCHANGE_RATE_API_URL = "https://tw.rter.info/capi.php";
    
    @GetMapping("/click")
    public String click() {
        try {
            // Fetch exchange rate data from the API as a string
            ResponseEntity<String> response = restTemplate.getForEntity(EXCHANGE_RATE_API_URL, String.class);
            String jsonResponse = response.getBody();
            
            if (jsonResponse == null) {
                throw new RuntimeException("Empty response from exchange rate API");
            }
            
            // Parse the JSON string manually using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> rateData = objectMapper.readValue(jsonResponse, Map.class);
            
            // Extract the TWD to USD exchange rate
            if (!rateData.containsKey("USDTWD")) {
                throw new RuntimeException("Invalid or missing exchange rate data");
            }
            
            Map<String, Object> usdTwdData = rateData.get("USDTWD");
            if (!usdTwdData.containsKey("Exrate")) {
                throw new RuntimeException("Missing Exrate data");
            }
            
            // The API returns USD to TWD rate, we're taking the reciprocal to get TWD to USD
            Double usdToTwd = ((Number) usdTwdData.get("Exrate")).doubleValue();
            Double twdToUsd = 1 / usdToTwd;
            
            // Create and save counter with the exchange rate
            Counter counter = new Counter();
            // No need to set ID as it's now auto-incrementing
            counter.setNumber(Math.round(twdToUsd * 1000000)); // Store as long with 6 decimal precision
            counter = counterRepository.save(counter);
            
            return String.format("Exchange rate saved! 1 TWD = %.6f USD (ID: %d)", twdToUsd, counter.getId());  
        } catch (Exception e) {
            return "Error fetching exchange rate: " + e.getMessage();
        }
    }

    @GetMapping("/call-other")
    public String callOtherPodClick() {
        String url = otherPodUrl + "/click";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return "Other pod response: " + response;
        } catch (Exception e) {
            return "Failed to call other pod: " + e.getMessage();
        }
    }
}
