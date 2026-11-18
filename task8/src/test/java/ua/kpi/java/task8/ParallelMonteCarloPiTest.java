package ua.kpi.java.task8;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParallelMonteCarloPiTest {

    private static final long SEED = 85L;
    private static final long MILLION = 1_000_000L;
    /** Стандартне відхилення оцінки при 10^6 точок ≈ 0.0016, допуск – понад 6σ. */
    private static final double TOLERANCE = 0.01;

    @Test
    void estimateIsCloseToPiForMillionPoints() {
        ParallelMonteCarloPi.Result r = ParallelMonteCarloPi.estimate(MILLION, 4, SEED);
        assertEquals(Math.PI, r.pi(), TOLERANCE);
        assertEquals(MILLION, r.iterations());
        assertEquals(4, r.threads());
        assertEquals(4.0 * r.inside() / MILLION, r.pi());
        assertTrue(r.timeMs() >= 0);
    }

    @ParameterizedTest(name = "[{index}] threads = {0}")
    @ValueSource(ints = {1, 2, 3, 8, 16})
    void oneAndManyThreadsGiveSaneEstimate(int threads) {
        ParallelMonteCarloPi.Result r = ParallelMonteCarloPi.estimate(MILLION, threads, SEED);
        assertEquals(Math.PI, r.pi(), TOLERANCE);
        assertTrue(r.inside() > 0 && r.inside() <= MILLION);
    }

    @Test
    void sameSeedAndThreadsGiveIdenticalResult() {
        ParallelMonteCarloPi.Result a = ParallelMonteCarloPi.estimate(MILLION, 8, SEED);
        ParallelMonteCarloPi.Result b = ParallelMonteCarloPi.estimate(MILLION, 8, SEED);
        assertEquals(a.inside(), b.inside());
        assertEquals(a.pi(), b.pi());
    }

    @Test
    void differentSeedGivesDifferentResult() {
        long a = ParallelMonteCarloPi.estimate(MILLION, 4, SEED).inside();
        long b = ParallelMonteCarloPi.estimate(MILLION, 4, SEED + 1).inside();
        assertNotEquals(a, b);
    }

    @Test
    void oneThreadEqualsSequentialCount() {
        // один потік отримує перший генератор split() і рахує всі точки сам
        long expected = ParallelMonteCarloPi.countInside(MILLION, new SplittableRandom(SEED).split());
        assertEquals(expected, ParallelMonteCarloPi.estimate(MILLION, 1, SEED).inside());
    }

    @Test
    void partsSumToTotalAndRemainderGoesToFirstThreads() {
        assertArrayEquals(new long[]{4, 3, 3}, ParallelMonteCarloPi.splitIterations(10, 3));
        assertArrayEquals(new long[]{5, 5}, ParallelMonteCarloPi.splitIterations(10, 2));
        assertArrayEquals(new long[]{1, 1, 0, 0}, ParallelMonteCarloPi.splitIterations(2, 4));
        for (int threads : new int[]{1, 3, 7, 16, 32}) {
            long[] parts = ParallelMonteCarloPi.splitIterations(ParallelMonteCarloPi.TOTAL_ITERATIONS, threads);
            assertEquals(threads, parts.length);
            assertEquals(ParallelMonteCarloPi.TOTAL_ITERATIONS, Arrays.stream(parts).sum());
            assertTrue(parts[0] - parts[threads - 1] <= 1);
        }
    }

    @Test
    void countInsideOfZeroPointsIsZero() {
        assertEquals(0, ParallelMonteCarloPi.countInside(0, new SplittableRandom(SEED)));
    }

    @Test
    void validThreadArgumentIsParsed() {
        assertEquals(1, ParallelMonteCarloPi.parseThreads(new String[]{"1"}));
        assertEquals(8, ParallelMonteCarloPi.parseThreads(new String[]{"8"}));
        assertEquals(ParallelMonteCarloPi.MAX_THREADS,
                ParallelMonteCarloPi.parseThreads(new String[]{String.valueOf(ParallelMonteCarloPi.MAX_THREADS)}));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\"")
    @ValueSource(strings = {"abc", "0", "-3", "2.5", "", "1025", "99999999999"})
    void invalidThreadArgumentIsRejected(String arg) {
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.parseThreads(new String[]{arg}));
    }

    @Test
    void missingOrExtraArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.parseThreads(new String[0]));
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.parseThreads(null));
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.parseThreads(new String[]{"4", "8"}));
    }

    @Test
    void estimateRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.estimate(0, 4, SEED));
        assertThrows(IllegalArgumentException.class, () -> ParallelMonteCarloPi.estimate(MILLION, 0, SEED));
    }

    @Test
    void outputHasRequiredFormat() {
        ParallelMonteCarloPi.Result r = new ParallelMonteCarloPi.Result(3.142214, 785553500L,
                1_000_000_000L, 8, 12.834);
        assertEquals("PI is 3.14221\nTHREADS 8\nITERATIONS 1,000,000,000\nTIME 12.83ms",
                ParallelMonteCarloPi.format(r));
    }

    @Test
    void medianOfThreeRuns() {
        assertEquals(20.0, PiExperiments.median(new double[]{30, 10, 20}));
        assertEquals(15.0, PiExperiments.median(new double[]{10, 20}));
    }
}
