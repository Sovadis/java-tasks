package ua.kpi.java.task8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Експерименти для відповіді на три питання лабораторної роботи №8. Це окрема програма
 * (не обов'язкова {@link ParallelMonteCarloPi}), аргументів не потребує.
 *
 * <ol>
 *     <li>Як кількість ітерацій впливає на точність оцінки π.</li>
 *     <li>Як кількість потоків впливає на результат при тих самих кількості точок і зерні.</li>
 *     <li>Як кількість потоків впливає на час обчислення; додатково вимірюється час роботи
 *     кожного потоку окремо.</li>
 * </ol>
 */
public final class PiExperiments {

    private static final long SEED = ParallelMonteCarloPi.BASE_SEED;
    private static final int ACCURACY_THREADS = 8;
    private static final int ACCURACY_SEEDS = 10;
    private static final long RESULT_ITERATIONS = 100_000_000L;
    private static final int[] RESULT_THREADS = {1, 2, 4, 8, 16};
    private static final int[] SPEED_THREADS = {1, 2, 4, 8, 16, 32};
    private static final int SPEED_RUNS = 5;
    private static final int[] WORKER_THREADS = {8, 16};

    private PiExperiments() {
    }

    /**
     * Запускає всі три експерименти й виводить таблиці результатів.
     *
     * @param args не використовуються
     */
    public static void main(String[] args) {
        System.out.println("Логічних процесорів: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Зерно: " + SEED);
        accuracy();
        threadsAndResult();
        performance();
        workerTimes();
    }

    /**
     * Питання 1: похибка оцінки π при 10^3 … 10^9 точок і фіксованій кількості потоків.
     * Окрім похибки для основного зерна, рахується середньоквадратична похибка по
     * {@link #ACCURACY_SEEDS} зернах, яку можна порівняти з теоретичною σ ≈ 1.64/√N.
     */
    private static void accuracy() {
        System.out.println();
        System.out.println("1. Вплив кількості ітерацій на точність (потоків: " + ACCURACY_THREADS + ")");
        System.out.printf(Locale.US, "%15s %10s %12s %14s %12s%n",
                "ITERATIONS", "PI", "|PI - π|", "СКП (" + ACCURACY_SEEDS + " зерен)", "σ = 1.64/√N");
        for (long n = 1_000L; n <= ParallelMonteCarloPi.TOTAL_ITERATIONS; n *= 10) {
            ParallelMonteCarloPi.Result r = ParallelMonteCarloPi.estimate(n, ACCURACY_THREADS, SEED);
            double sumSquares = 0;
            for (int k = 0; k < ACCURACY_SEEDS; k++) {
                double error = ParallelMonteCarloPi.estimate(n, ACCURACY_THREADS, SEED + k).pi() - Math.PI;
                sumSquares += error * error;
            }
            double rms = Math.sqrt(sumSquares / ACCURACY_SEEDS);
            // теоретичне стандартне відхилення оцінки: 4·√(p(1−p)/N), p = π/4, тобто ≈ 1.642/√N
            double sigma = Math.sqrt(Math.PI * (4 - Math.PI) / n);
            System.out.printf(Locale.US, "%,15d %10.6f %12.6f %14.6f %12.6f%n",
                    n, r.pi(), Math.abs(r.pi() - Math.PI), rms, sigma);
        }
    }

    /** Питання 2: результат при тих самих кількості точок і зерні, але різній кількості потоків. */
    private static void threadsAndResult() {
        System.out.println();
        System.out.printf(Locale.US, "2. Вплив кількості потоків на результат (ітерацій: %,d, зерно %d)%n",
                RESULT_ITERATIONS, SEED);
        System.out.printf(Locale.US, "%8s %12s %10s %12s %10s%n", "THREADS", "INSIDE", "PI", "|PI - π|", "ПОВТОР");
        for (int threads : RESULT_THREADS) {
            ParallelMonteCarloPi.Result first = ParallelMonteCarloPi.estimate(RESULT_ITERATIONS, threads, SEED);
            ParallelMonteCarloPi.Result second = ParallelMonteCarloPi.estimate(RESULT_ITERATIONS, threads, SEED);
            String same = first.inside() == second.inside() ? "збігається" : "ІНШИЙ";
            System.out.printf(Locale.US, "%8d %,12d %10.6f %12.6f %10s%n", threads, first.inside(), first.pi(),
                    Math.abs(first.pi() - Math.PI), same);
        }
    }

    /**
     * Питання 3: час обчислення 10^9 точок (п'ять запусків після прогріву) і прискорення.
     * Сторонні процеси можуть лише збільшити час, тому прискорення рахується за мінімальним часом.
     */
    private static void performance() {
        long n = ParallelMonteCarloPi.TOTAL_ITERATIONS;
        System.out.println();
        System.out.printf(Locale.US,
                "3. Вплив кількості потоків на продуктивність (ітерацій: %,d, запусків: %d)%n", n, SPEED_RUNS);
        // прогрів: JIT-компілятор встигає скомпілювати цикл до початку вимірювань
        for (int i = 0; i < 3; i++) {
            ParallelMonteCarloPi.estimate(100_000_000L, 4, SEED);
        }
        System.out.printf(Locale.US, "%8s %10s %9s %9s %7s %7s   %s%n",
                "THREADS", "PI", "MIN, ms", "MED, ms", "S", "E", "ЗАПУСКИ, ms");
        double base = 0;
        for (int threads : SPEED_THREADS) {
            double[] times = new double[SPEED_RUNS];
            double pi = 0;
            for (int k = 0; k < SPEED_RUNS; k++) {
                ParallelMonteCarloPi.Result r = ParallelMonteCarloPi.estimate(n, threads, SEED);
                times[k] = r.timeMs();
                pi = r.pi();
            }
            double min = Arrays.stream(times).min().orElseThrow();
            if (threads == 1) {
                base = min;
            }
            double speedup = base / min;
            System.out.printf(Locale.US, "%8d %10.5f %9.1f %9.1f %7.2f %7.2f   %s%n",
                    threads, pi, min, median(times), speedup, speedup / threads, runs(times));
        }
    }

    /**
     * Доповнення до питання 3: час роботи кожного потоку окремо при рівному поділі 10^9 точок.
     * Загальний час визначає найповільніший потік, тому розкид показує, чи однаково швидко
     * працюють ядра процесора.
     */
    private static void workerTimes() {
        long n = ParallelMonteCarloPi.TOTAL_ITERATIONS;
        System.out.println();
        System.out.printf(Locale.US, "4. Час роботи окремих потоків (ітерацій: %,d, один запуск)%n", n);
        System.out.printf(Locale.US, "%8s %8s %8s %8s   %s%n", "THREADS", "MIN, ms", "MED, ms", "MAX, ms",
                "ЧАС КОЖНОГО ПОТОКУ, ms (за зростанням)");
        for (int threads : WORKER_THREADS) {
            long[] parts = ParallelMonteCarloPi.splitIterations(n, threads);
            SplittableRandom root = new SplittableRandom(SEED);
            List<Callable<Double>> tasks = new ArrayList<>();
            for (long part : parts) {
                SplittableRandom random = root.split();
                tasks.add(() -> {
                    long start = System.nanoTime();
                    ParallelMonteCarloPi.countInside(part, random);
                    return (System.nanoTime() - start) / 1_000_000.0;
                });
            }
            double[] times = new double[threads];
            try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
                List<Future<Double>> futures = pool.invokeAll(tasks);
                for (int i = 0; i < threads; i++) {
                    times[i] = futures.get(i).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException("Помилка вимірювання", e);
            }
            Arrays.sort(times);
            System.out.printf(Locale.US, "%8d %8.0f %8.0f %8.0f   %s%n", threads, times[0], median(times),
                    times[threads - 1], runs(times).replace(" / ", " "));
        }
    }

    /**
     * Медіана масиву значень.
     *
     * @param values значення (масив не змінюється)
     * @return медіана
     */
    static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 1 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2;
    }

    private static String runs(double[] times) {
        StringBuilder sb = new StringBuilder();
        for (double t : times) {
            if (!sb.isEmpty()) {
                sb.append(" / ");
            }
            sb.append(String.format(Locale.US, "%.0f", t));
        }
        return sb.toString();
    }
}
