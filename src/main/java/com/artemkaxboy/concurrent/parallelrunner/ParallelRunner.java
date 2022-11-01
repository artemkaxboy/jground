package com.artemkaxboy.concurrent.parallelrunner;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelRunner<T> {

    private static final Logger log = LoggerFactory.getLogger(ParallelRunner.class);

    private final ExecutorService executorService;
    private final int threadCount;
    private final CountDownLatch startLatch;
    private final CountDownLatch finishLatch;
    private final CountDownLatch trigger;
    private List<Future<T>> tasks;

    private ParallelRunner(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.threadCount = threadCount;
        this.startLatch = new CountDownLatch(threadCount);
        this.finishLatch = new CountDownLatch(threadCount);
        this.trigger = new CountDownLatch(1);

        new Thread(() -> {
            try {
                this.finishLatch.await();
            } catch (InterruptedException ignored) {
            } finally {
                this.executorService.shutdown();
            }
        }).start();
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

    public static <T, R> ParallelRunner<R> forFunction(int threadCount, Function<T, R> function, T argument) {
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

    private void setTasks(List<Future<T>> tasks) {
        this.tasks = tasks;
    }

    public void await() throws InterruptedException {
        long count = finishLatch.getCount();
        if (count > 0) {
            log.debug("Waiting for {} threads to finish...", count);
            finishLatch.await();
        } else {
            log.debug("All threads are ready");
        }
    }

    public Collection<Result<T>> getResults() throws InterruptedException {
        await();
        return tasks.stream().parallel().map(f -> Result.of(f::get)).collect(Collectors.toList());
    }

    public void run() {
        trigger.countDown();
    }

    public boolean isDown() {
        return executorService.isShutdown();
    }

    public int getFinishedThreadCount() {
        return threadCount - (int) finishLatch.getCount();
    }

    public int getStartedTaskCount() {
        return threadCount - (int) startLatch.getCount();
    }

    public int getRunningTaskCount() {
        return getStartedTaskCount() - getFinishedThreadCount();
    }

    public int getThreadCount() {
        return threadCount;
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
            } catch (InterruptedException e) {
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
        public T call() {
            startLatch.countDown();
            try {
                trigger.await();
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
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
            } catch (Exception e) {
                throw new RuntimeException(e);
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
