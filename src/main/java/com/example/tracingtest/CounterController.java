package com.example.tracingtest;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CounterController {
    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/click")
    public String click() {
        Counter counter = new Counter();
        counter.setNumber(new Random().nextLong());
        counterRepository.save(counter);
        return "Clicked!";
    }

    @GetMapping("/call-other")
    public String callOtherPodClick(@RequestParam String podUrl) {
        String url = podUrl + "/click";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return "Other pod response: " + response;
        } catch (Exception e) {
            return "Failed to call other pod: " + e.getMessage();
        }
    }
}
