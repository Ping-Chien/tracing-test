package com.example.tracingtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class CounterController {
    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${other.pod.url}")
    private String otherPodUrl;

    @GetMapping("/click")
    public String click() {
        Counter counter = new Counter();
        counter.setNumber(counterRepository.count() + 1); // 或依需求設定number
        counterRepository.save(counter);
        return "Clicked!";
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
