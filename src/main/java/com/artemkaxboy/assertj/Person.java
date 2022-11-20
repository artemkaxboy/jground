package com.artemkaxboy.assertj;

import java.time.Instant;

public record Person(String name, Instant birthDate, double height) {

    public Person {
        if (height < 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
    }
}
