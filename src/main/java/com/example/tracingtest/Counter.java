package com.example.tracingtest;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Counter {
    @Id
    private Long id;

    private Long number;

    // You can add more fields as needed
    public Counter() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
