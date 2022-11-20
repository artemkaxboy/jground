package com.artemkaxboy.assertj;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class PersonTest {

    private static final Person PERSON_1 = new Person("John", Instant.now(), 1.8);
    private static final Person PERSON_2 = new Person("John", Instant.now(), 1.8);

    @Test
    void fails() {

        assertThat(PERSON_1)
                .isEqualTo(PERSON_2);
    }

    @Test
    void passesIgnoringType() {

        assertThat(PERSON_1)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)                    // ignore field of type Instant solves the problem
                .isEqualTo(PERSON_2);
    }

    @Test
    void passesIgnoringFieldByName() {

        String birthDateFieldName = "birthDate"; // CONS - field name is hardcoded!!!
        assertThat(PERSON_1)
                .usingRecursiveComparison()
                .ignoringFields(birthDateFieldName)                      // ignore field of name birthDate solves the problem
                .isEqualTo(PERSON_2);
    }

    /**
     * {@link InstantDayComparator}
     */
    @Test
    void passesWithCustomComparatorForType() {

        assertThat(PERSON_1)
                .usingRecursiveComparison()
                .withComparatorForType(InstantDayComparator::compare, Instant.class)
                .isEqualTo(PERSON_2);
    }

    @Test
    void allPasses() {
        assertThat(PERSON_1)
                .hasNoNullFieldsOrProperties()
                .isInstanceOf(Object.class)
                .isExactlyInstanceOf(Person.class)
                .isNotNull()
                .doesNotHaveSameHashCodeAs(PERSON_2);
    }

    @Test
    void instants() {
        assertThat(PERSON_1.birthDate())
                .isBefore(Instant.now())
                .isCloseTo(Instant.now(), within(1, ChronoUnit.MINUTES));
    }

    @Test
    void doubles() {
        assertThat(PERSON_1.height())
                .isNotNaN()
                .isNotZero()
                .isNotNegative()
                .isPositive()
                .isCloseTo(PERSON_2.height(), within(0.1))
                .isCloseTo(PERSON_2.height(), Percentage.withPercentage(1))
        ;
    }

    @Test
    void strings() {
        assertThat(PERSON_1.name())
//                .containsOnlyDigits()
//                .containsOnlyWhitespaces()

                .isNotBlank()
                .isMixedCase()
                .isEqualToIgnoringCase(PERSON_1.name().toLowerCase())

                .isEqualToIgnoringNewLines(PERSON_1.name())
                .isEqualToIgnoringWhitespace(PERSON_1.name())
                .isEqualToNormalizingWhitespace(PERSON_1.name())
                .isEqualToNormalizingPunctuationAndWhitespace(PERSON_1.name())

                .matches("\\w+")
                .startsWith("J")
                .endsWith("n")
                .contains("oh")
                .hasSizeBetween(3, 5)
                .hasSameSizeAs(PERSON_2.name())
        ;
    }

    @Test
    void multiFail() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(1)
                .isEven()
                .isNegative();
        softly.assertAll();
    }

    @Test
    void exceptions() {
        Throwable throwable = new IllegalArgumentException("wrong amount 123");
        Throwable runtime = new RuntimeException(throwable);

        assertThat(runtime).hasMessageMatching(".*amount.*")
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .isInstanceOf(RuntimeException.class);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    new Person("John", Instant.now(), -1);
                })
                .withMessageMatching(".*must be positive.*");

        assertThatThrownBy(() -> {
            throw runtime;
        })
                .hasMessageMatching(".*amount.*")
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .isInstanceOf(RuntimeException.class);

    }
}
