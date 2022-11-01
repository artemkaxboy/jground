package com.artemkaxboy.concurrent.parallelrunner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelRunner<T> implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ParallelRunner.class);

    private final ExecutorService executorService;
    private final int threadCount;
    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;
    private final CountDownLatch trigger;
    private final Thread shutdownHookThread;
    private List<Future<T>> tasks;

    private ParallelRunner(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.threadCount = threadCount;
        this.startLatch = new CountDownLatch(threadCount);
        this.finishLatch = new CountDownLatch(threadCount);
        this.trigger = new CountDownLatch(1);

        this.shutdownHookThread = new Thread(() -> {
            try {
                this.finishLatch.await();
            } catch (InterruptedException ignored) {
            } finally {
                this.executorService.shutdown();
            }
        });
        this.shutdownHookThread.start();
    }

    public static ParallelRunner<Void> forRunnable(int threadCount, Runnable runnable) {

        ParallelRunner<Void> runner = new ParallelRunner<>(threadCount);
        List<Future<Void>> tasks = IntStream.range(0, threadCount).mapToObj(i -> {
            TaskWorkerRunnable task =
                    new TaskWorkerRunnable(runnable, runner.startLatch, runner.finishLatch, runner.trigger);
            //noinspection unchecked
            return (Future<Void>) runner.executorService.submit(task);
        }).toList();

        runner.setTasks(tasks);
        return runner;
    }

    public static <T> ParallelRunner<T> forSupplier(int threadCount, Supplier<T> supplier) {

        ParallelRunner<T> runner = new ParallelRunner<>(threadCount);
        List<Future<T>> tasks = IntStream.range(0, threadCount).mapToObj(i -> {
            TaskWorkerSupplier<T> task =
                    new TaskWorkerSupplier<>(supplier, runner.startLatch, runner.finishLatch, runner.trigger);
            return runner.executorService.submit(task);
        }).toList();

        runner.setTasks(tasks);
        return runner;
    }

    public static <T, R> ParallelRunner<R> forFunctionStatic(int threadCount, Function<T, R> function, T argument) {
        return forFunction(threadCount, function, (Supplier<T>) () -> argument);
    }

    public static <T, R> ParallelRunner<R> forFunction(int threadCount,
                                                       Function<T, R> function,
                                                       Supplier<T> argumentGetter) {

        ParallelRunner<R> runner = new ParallelRunner<>(threadCount);
        List<Future<R>> tasks = IntStream.range(0, threadCount).mapToObj(i -> {
            TaskWorkerFunction<T, R> task = new TaskWorkerFunction<>(
                    function, argumentGetter, runner.startLatch, runner.finishLatch, runner.trigger);
            return runner.executorService.submit(task);
        }).toList();

        runner.setTasks(tasks);
        return runner;
    }

    public void awaitReadiness() throws InterruptedException {
        long count;
        boolean interrupted = false;
        while ((count = startLatch.getCount()) > 0 && !interrupted) {
            log.debug("Waiting for {} threads to be ready...", count);
            interrupted = startLatch.await(100, TimeUnit.MILLISECONDS);
        }
    }

    public void await() throws InterruptedException {
        long count;
        boolean interrupted = false;
        while ((count = finishLatch.getCount()) > 0 && !interrupted) {
            log.debug("Waiting for {} threads to finish...", count);
            interrupted = finishLatch.await(100, TimeUnit.MILLISECONDS);
        }
    }

    public Collection<Result<T>> getResults() {
        return tasks.stream().map(f -> Result.of(f::get)).collect(Collectors.toList());
    }

    public Collection<Result<T>> getResultValues() {
        return tasks.stream().map(f -> Result.of(f::get)).filter(Result::isValue).collect(Collectors.toList());
    }

    public Collection<Result<T>> getResultExceptions() {
        return tasks.stream().map(f -> Result.of(f::get)).filter(Result::isException).collect(Collectors.toList());
    }

    public void start() {
        trigger.countDown();
    }

    public void interrupt() {
        close();
    }

    public boolean isDown() {
        return executorService.isShutdown();
    }

    public int getFinishedThreadCount() {
        return threadCount - (int) finishLatch.getCount();
    }

    public int getPreparedTaskCount() {
        return threadCount - (int) startLatch.getCount();
    }

    public int getRunningTaskCount() {
        return getPreparedTaskCount() - getFinishedThreadCount();
    }

    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public void close() {
        executorService.shutdownNow();
        shutdownHookThread.interrupt();
    }

    private void setTasks(List<Future<T>> tasks) {
        this.tasks = tasks;
    }

    @SuppressWarnings("ClassCanBeRecord") // must be java 8 compatible
    private static class TaskWorkerRunnable implements Runnable {

        private final Runnable runnable;
        private final CountDownLatch startLatch;
        private final CountDownLatch finishLatch;
        private final CountDownLatch trigger;

        public TaskWorkerRunnable(
                Runnable runnable,
                CountDownLatch startLatch,
                CountDownLatch finishLatch,
                CountDownLatch trigger) {
            this.runnable = runnable;
            this.startLatch = startLatch;
            this.finishLatch = finishLatch;
            this.trigger = trigger;
        }

        @Override
        public void run() {
            startLatch.countDown();
            try {
                trigger.await();
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                finishLatch.countDown();
            }
        }
    }

    private static class TaskWorkerSupplier<T> implements Callable<T> {

        private final Callable<T> callable;
        private final CountDownLatch startLatch;
        private final CountDownLatch finishLatch;
        private final CountDownLatch trigger;

        public TaskWorkerSupplier(
                Supplier<T> task,
                CountDownLatch startLatch,
                CountDownLatch finishLatch,
                CountDownLatch trigger) {
            this.callable = task::get;
            this.startLatch = startLatch;
            this.finishLatch = finishLatch;
            this.trigger = trigger;
        }

        @Override
        public T call() throws Exception {
            startLatch.countDown();
            try {
                trigger.await();
                return callable.call();
            } finally {
                finishLatch.countDown();
            }
        }
    }

    private static class TaskWorkerFunction<T, R> implements Callable<R> {

        private final Function<T, R> function;
        private final Supplier<T> argumentGetter;
        private final CountDownLatch startLatch;
        private final CountDownLatch finishLatch;
        private final CountDownLatch trigger;

        public TaskWorkerFunction(
                Function<T, R> function,
                Supplier<T> argumentGetter,
                CountDownLatch startLatch,
                CountDownLatch finishLatch,
                CountDownLatch trigger) {
            this.function = function;
            this.argumentGetter = argumentGetter;
            this.startLatch = startLatch;
            this.finishLatch = finishLatch;
            this.trigger = trigger;
        }

        public TaskWorkerFunction(
                Function<T, R> function,
                T argument,
                CountDownLatch startLatch,
                CountDownLatch finishLatch,
                CountDownLatch trigger) {
            this(function, () -> argument, startLatch, finishLatch, trigger);
        }

        @Override
        public R call() throws Exception {
            startLatch.countDown();
            try {
                trigger.await();
                return function.apply(argumentGetter.get());
            } finally {
                finishLatch.countDown();
            }
        }
    }

    static class Result<T> {

        private final T value;
        private final Exception exception;

        private Result(T value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        static <T> Result<T> of(Callable<T> callable) {
            try {
                return new Result<>(callable.call(), null);
            } catch (Exception e) {
                return new Result<>(null, e);
            }
        }

        public boolean isValue() {
            return exception == null;
        }

        public boolean isException() {
            return !isValue();
        }

        public T getValue() {
            return value;
        }

        public Exception getException() {
            return exception;
        }
    }
}
