package ua.kpi.java.task8;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Лабораторна робота №8 (Simple Task): паралельне обчислення наближеного значення числа π
 * методом Монте-Карло.
 *
 * <p>У одиничний квадрат «кидаються» випадкові точки (x, y), і рахуються ті, для яких
 * x² + y² ≤ 1. Частка таких точок наближено дорівнює π/4, тому π ≈ 4 · (влучання / усі точки).
 *
 * <p>Точки розподіляються між потоками порівну. Потоки запускаються через {@link ExecutorService}:
 * кожен потік виконує задачу {@link Callable}{@code <Long>}, яка повертає власну кількість влучань,
 * а головний потік збирає результати через {@link Future#get()} і додає їх. Такий підхід обрано
 * замість {@code Thread} + {@code join()}, бо {@code Callable} повертає значення напряму (не треба
 * спільного масиву для результатів), пул сам керує потоками, а виняток у потоці не губиться –
 * він повертається з {@code get()} як {@link ExecutionException}.
 *
 * <p>Кожен потік має власний лічильник (локальну змінну) і власний генератор
 * {@link SplittableRandom}. Генератори створюються в головному потоці методом
 * {@link SplittableRandom#split()} від одного кореневого генератора із заданим зерном, тому
 * результат відтворюється при тих самих зерні та кількості потоків, а потоки не мають
 * спільного змінного стану і не потребують синхронізації в циклі.
 *
 * <p>Єдиний аргумент програми – кількість потоків.
 */
public final class ParallelMonteCarloPi {

    /** Загальна кількість точок (ітерацій). */
    public static final long TOTAL_ITERATIONS = 1_000_000_000L;

    /** Зерно кореневого генератора випадкових чисел. */
    public static final long BASE_SEED = 85L;

    /** Найбільша дозволена кількість потоків (захист від помилкового введення). */
    public static final int MAX_THREADS = 1024;

    private static final String USAGE =
            "Використання: java ua.kpi.java.task8.ParallelMonteCarloPi <кількість потоків від 1 до "
                    + MAX_THREADS + ">";

    /**
     * Результат обчислення.
     *
     * @param pi         наближене значення π
     * @param inside     кількість точок, що потрапили в чверть кола
     * @param iterations загальна кількість точок
     * @param threads    кількість потоків
     * @param timeMs     час обчислення в мілісекундах
     */
    public record Result(double pi, long inside, long iterations, int threads, double timeMs) {
    }

    private ParallelMonteCarloPi() {
    }

    /**
     * Точка входу: читає кількість потоків, обчислює π і виводить результат.
     *
     * @param args єдиний аргумент – кількість потоків
     */
    public static void main(String[] args) {
        int threads;
        try {
            threads = parseThreads(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка: " + e.getMessage());
            System.err.println(USAGE);
            System.exit(1);
            return;
        }
        Result result = estimate(TOTAL_ITERATIONS, threads, BASE_SEED);
        System.out.println(format(result));
    }

    /**
     * Перевіряє аргументи командного рядка й повертає кількість потоків.
     *
     * @param args аргументи програми
     * @return кількість потоків від 1 до {@link #MAX_THREADS}
     * @throws IllegalArgumentException якщо аргумент відсутній, їх більше одного, аргумент не є цілим
     *                                  числом або виходить за межі
     */
    public static int parseThreads(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("не задано кількість потоків.");
        }
        if (args.length > 1) {
            throw new IllegalArgumentException(
                    "програма приймає лише один аргумент, отримано " + args.length + ".");
        }
        int threads;
        try {
            threads = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "кількість потоків має бути цілим числом, отримано «" + args[0] + "».");
        }
        if (threads < 1 || threads > MAX_THREADS) {
            throw new IllegalArgumentException("кількість потоків має бути від 1 до " + MAX_THREADS
                    + ", отримано " + threads + ".");
        }
        return threads;
    }

    /**
     * Ділить ітерації між потоками: кожен отримує {@code total / threads}, а остачу
     * по одній ітерації отримують перші потоки.
     *
     * @param total   загальна кількість ітерацій
     * @param threads кількість потоків
     * @return масив довжини {@code threads} з кількістю ітерацій для кожного потоку
     */
    public static long[] splitIterations(long total, int threads) {
        if (total < 0 || threads < 1) {
            throw new IllegalArgumentException(
                    "Неправильні дані: ітерацій " + total + ", потоків " + threads);
        }
        long[] parts = new long[threads];
        long base = total / threads;
        long remainder = total % threads;
        for (int i = 0; i < threads; i++) {
            parts[i] = base + (i < remainder ? 1 : 0);
        }
        return parts;
    }

    /**
     * Послідовно «кидає» {@code points} точок і рахує влучання в чверть одиничного кола.
     * Цей метод виконує кожен потік зі своїм генератором.
     *
     * @param points кількість точок
     * @param random генератор, яким користується лише один потік
     * @return кількість точок з x² + y² ≤ 1
     */
    public static long countInside(long points, SplittableRandom random) {
        long inside = 0;
        for (long i = 0; i < points; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }
        return inside;
    }

    /**
     * Паралельно обчислює наближене значення π.
     *
     * @param iterations загальна кількість точок (не менше 1)
     * @param threads    кількість потоків (не менше 1)
     * @param seed       зерно кореневого генератора
     * @return результат з оцінкою π і часом обчислення
     */
    public static Result estimate(long iterations, int threads, long seed) {
        if (iterations < 1 || threads < 1) {
            throw new IllegalArgumentException(
                    "Неправильні дані: ітерацій " + iterations + ", потоків " + threads);
        }
        long[] parts = splitIterations(iterations, threads);
        // генератори створюються заздалегідь у головному потоці,
        // тому порядок split() не залежить від планувальника потоків
        SplittableRandom root = new SplittableRandom(seed);
        List<Callable<Long>> tasks = new ArrayList<>();
        for (long part : parts) {
            SplittableRandom random = root.split();
            tasks.add(() -> countInside(part, random));
        }

        long start = System.nanoTime();
        long inside = 0;
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            List<Future<Long>> futures = new ArrayList<>();
            for (Callable<Long> task : tasks) {
                futures.add(pool.submit(task));
            }
            for (Future<Long> future : futures) {
                inside += future.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Обчислення перервано", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Помилка в потоці обчислення", e.getCause());
        }
        double timeMs = (System.nanoTime() - start) / 1_000_000.0;

        double pi = 4.0 * inside / iterations;
        return new Result(pi, inside, iterations, threads, timeMs);
    }

    /**
     * Форматує результат так, як вимагає завдання.
     *
     * @param result результат обчислення
     * @return чотири рядки: PI, THREADS, ITERATIONS, TIME
     */
    public static String format(Result result) {
        return String.format(Locale.US, "PI is %.5f\nTHREADS %d\nITERATIONS %,d\nTIME %.2fms",
                result.pi(), result.threads(), result.iterations(), result.timeMs());
    }
}
