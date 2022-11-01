package com.artemkaxboy.concurrent.parallelrunner;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParallelRunnerTest {

    private static final int MANY_THREADS = 1000;
    private static final int FEW_THREADS = 100;

    private static final Runnable NOOP = () -> {
    };

    private static <T> void verifyHalfThrownHalfExpected(T expectedValue,
                                                         Collection<ParallelRunner.Result<T>> values,
                                                         Collection<ParallelRunner.Result<T>> exceptions) {
        Assertions.assertEquals(exceptions.size(), values.size());
        Assertions.assertTrue(values.stream().allMatch(it -> Objects.equals(expectedValue, it.getValue())));
    }

    private static void interruptRunnerInAWhile(ParallelRunner<?> runner) {
        new Thread(() -> {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runner.interrupt();
        }).start();
    }

    @Test
    void forRunnable_ManyThread() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ParallelRunner<Void> runner = ParallelRunner.forRunnable(MANY_THREADS, counter::incrementAndGet);

        runner.start();
        runner.await();

        Assertions.assertEquals(MANY_THREADS, counter.get());
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forSupplier_ManyThread() {
        AtomicInteger counter = new AtomicInteger();
        ParallelRunner<Integer> runner = ParallelRunner.forSupplier(MANY_THREADS, counter::incrementAndGet);

        runner.start();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();
        long distinctResults = results.stream().map(ParallelRunner.Result::getValue).distinct().count();

        Assertions.assertEquals(MANY_THREADS, counter.get());
        Assertions.assertEquals(MANY_THREADS, distinctResults);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunction_ManyThread() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ParallelRunner<Integer> runner = ParallelRunner.forFunction(MANY_THREADS, it -> it, counter::incrementAndGet);

        runner.start();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();
        long distinctResults = results.stream().map(ParallelRunner.Result::getValue).distinct().count();

        Assertions.assertEquals(MANY_THREADS, counter.get());
        Assertions.assertEquals(MANY_THREADS, distinctResults);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunctionStatic_ManyThread() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ParallelRunner<Integer> runner =
                ParallelRunner.forFunctionStatic(MANY_THREADS, it -> counter.incrementAndGet(), 0);

        runner.start();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();
        long distinctResults = results.stream().map(ParallelRunner.Result::getValue).distinct().count();

        Assertions.assertEquals(MANY_THREADS, counter.get());
        Assertions.assertEquals(MANY_THREADS, distinctResults);
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forRunnable_HalfResultException() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();

        ParallelRunner<Void> runner = ParallelRunner.forRunnable(FEW_THREADS, () -> {
            int value = counter.incrementAndGet();
            if (value % 2 == 0) {
                throw new RuntimeException(String.valueOf(value));
            }
        });
        runner.start();

        verifyHalfThrownHalfExpected(null, runner.getResultValues(), runner.getResultExceptions());
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forSupplier_HalfResultException() {
        AtomicInteger counter = new AtomicInteger();
        int expectedValue = 123;

        ParallelRunner<Integer> runner = ParallelRunner.forSupplier(FEW_THREADS, () -> {
            int value = counter.incrementAndGet();
            if (value % 2 == 0) {
                throw new RuntimeException(String.valueOf(value));
            }
            return expectedValue;
        });
        runner.start();

        verifyHalfThrownHalfExpected(expectedValue, runner.getResultValues(), runner.getResultExceptions());
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunction_HalfResultException() {
        AtomicInteger counter = new AtomicInteger();
        int expectedValue = 123;

        ParallelRunner<Integer> runner = ParallelRunner.forFunctionStatic(FEW_THREADS, arg -> {
            int value = counter.incrementAndGet();
            if (value % 2 == 0) {
                throw new RuntimeException(String.valueOf(value));
            }
            return arg;
        }, expectedValue);
        runner.start();

        verifyHalfThrownHalfExpected(expectedValue, runner.getResultValues(), runner.getResultExceptions());
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forRunnable_Interruption() throws InterruptedException {
        int sleepTime = 1000;

        ParallelRunner<Void> runner = ParallelRunner.forRunnable(FEW_THREADS, () -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        runner.awaitReadiness();
        long start = System.currentTimeMillis();
        interruptRunnerInAWhile(runner);
        runner.start();
        Collection<ParallelRunner.Result<Void>> results = runner.getResults();
        long duration = System.currentTimeMillis() - start;

        Assertions.assertTrue(duration < sleepTime);
        Assertions.assertTrue(results.stream().allMatch(ParallelRunner.Result::isException));
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forSupplier_Interruption() throws InterruptedException {
        int sleepTime = 1000;

        ParallelRunner<Integer> runner = ParallelRunner.forSupplier(FEW_THREADS, () -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        });
        runner.awaitReadiness();
        long start = System.currentTimeMillis();
        interruptRunnerInAWhile(runner);
        runner.start();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();
        long duration = System.currentTimeMillis() - start;

        Assertions.assertTrue(duration < sleepTime);
        Assertions.assertTrue(results.stream().allMatch(ParallelRunner.Result::isException));
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forFunction_Interruption() throws InterruptedException {
        int sleepTime = 1000;

        ParallelRunner<Integer> runner = ParallelRunner.forFunctionStatic(FEW_THREADS, arg -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return arg;
        }, 1);
        runner.awaitReadiness();
        long start = System.currentTimeMillis();
        interruptRunnerInAWhile(runner);
        runner.start();
        Collection<ParallelRunner.Result<Integer>> results = runner.getResults();
        long duration = System.currentTimeMillis() - start;

        Assertions.assertTrue(duration < sleepTime);
        Assertions.assertTrue(results.stream().allMatch(ParallelRunner.Result::isException));
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void forRunnable_WorksParallel() throws InterruptedException {
        int sleepTime = 1000;

        long start = System.currentTimeMillis();
        ParallelRunner<Void> runner = ParallelRunner.forRunnable(MANY_THREADS, () -> {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        runner.start();
        runner.await();
        long duration = System.currentTimeMillis() - start;

        Assertions.assertTrue(duration < sleepTime * 10);
        Assertions.assertTrue(runner.getResults().stream().allMatch(ParallelRunner.Result::isValue));
        Assertions.assertTrue(runner.isDown());
    }

    @Test
    void getFinishedThreadCount_WhenNotStarted() {
        try (ParallelRunner<Void> runner = ParallelRunner.forRunnable(FEW_THREADS, NOOP)) {
            Assertions.assertEquals(0, runner.getFinishedThreadCount());
        }
    }
}
