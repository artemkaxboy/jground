package com.artemkaxboy.concurrent.parallelrunner;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParallelRunnerTest {

    private static final int THREAD_COUNT = 10000;

    private String string;
    private Long number;

    @Test
    void forRunnable_AppendString() throws InterruptedException {
        string = "";

        ParallelRunner<Void> runner = ParallelRunner.forRunnable(THREAD_COUNT, () -> string += "a");
        runner.run();
        runner.await();

        Assertions.assertNotEquals(THREAD_COUNT, string.length());
        System.out.printf("%s/%s\n", string.length(), THREAD_COUNT);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forRunnable_IncrementNumber() throws InterruptedException {
        number = 0L;

        ParallelRunner<Void> runner = ParallelRunner.forRunnable(THREAD_COUNT, () -> number++);
        runner.run();
        runner.await();

        Assertions.assertNotEquals(THREAD_COUNT, number);
        System.out.printf("%s/%s\n", number, THREAD_COUNT);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forSupplier_IncrementNumber() throws InterruptedException {
        number = 0L;

        ParallelRunner<Long> runner = ParallelRunner.forSupplier(THREAD_COUNT, () -> number++);
        runner.run();
        Collection<ParallelRunner.Result<Long>> results = runner.getResults();
        int differentResultCount = results.stream()
                .collect(Collectors.groupingBy(ParallelRunner.Result::getValue))
                .size();

        Assertions.assertNotEquals(THREAD_COUNT, number);
        Assertions.assertEquals(number, differentResultCount);
        System.out.printf("%s/%s\n", number, THREAD_COUNT);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forSupplier_HalfResultException() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);

        ParallelRunner<Void> runner = ParallelRunner.forRunnable(THREAD_COUNT, () -> {
            int value = counter.incrementAndGet();
            if (value % 2 == 0) {
                throw new RuntimeException(String.valueOf(value));
            }
        });
        runner.run();
        Collection<ParallelRunner.Result<Void>> results = runner.getResults();

        Iterator<List<ParallelRunner.Result<Void>>> groupedResults = results.stream()
                .collect(Collectors.groupingBy(ParallelRunner.Result::isValue))
                .values().iterator();
        List<ParallelRunner.Result<Void>> first = groupedResults.next();
        List<ParallelRunner.Result<Void>> second = groupedResults.next();
        Assertions.assertEquals(first.size(), second.size());
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunction_ConstantResult() throws InterruptedException {
        Integer expectedResult = 1;

        ParallelRunner<Integer> runner = ParallelRunner.forFunction(THREAD_COUNT, integer -> integer, expectedResult);
        runner.run();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();

        Assertions.assertTrue(results.stream().allMatch(it -> Objects.equals(it.getValue(), expectedResult)));
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunction_AllExceptions() throws InterruptedException {

        ParallelRunner<Integer> runner = ParallelRunner.forFunction(THREAD_COUNT, integer -> {
            throw new RuntimeException();
        }, 1);
        runner.run();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();

        Assertions.assertTrue(results.stream().allMatch(ParallelRunner.Result::isException));
        Assertions.assertTrue(runner.isDown());
    }
}
