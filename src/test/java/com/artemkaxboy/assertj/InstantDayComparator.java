package com.artemkaxboy.assertj;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantDayComparator {

    public static int compare(Instant i1, Instant i2) {
        return i1.truncatedTo(ChronoUnit.DAYS).compareTo(i2.truncatedTo(ChronoUnit.DAYS));
    }
}
