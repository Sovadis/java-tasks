package ua.kpi.java.task9.buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Модульні тести кільцевого буфера: порядок, місткість, блокування й
 * цілісність даних за кількох виробників і споживачів.
 */
class RingBufferTest {

    /** Скільки чекати, щоб переконатися, що потік справді заблокований. */
    private static final long BLOCK_CHECK_MS = 300;
    private static final long JOIN_MS = 5_000;

    @Test
    void newBufferIsEmpty() {
        RingBuffer<String> buffer = new RingBuffer<>(3);

        assertTrue(buffer.isEmpty());
        assertFalse(buffer.isFull());
        assertEquals(0, buffer.size());
        assertEquals(3, buffer.capacity());
    }

    @Test
    void itemsAreTakenInFifoOrder() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(5);
        buffer.put("a");
        buffer.put("b");
        buffer.put("c");

        assertEquals("a", buffer.take());
        assertEquals("b", buffer.take());
        assertEquals("c", buffer.take());
        assertTrue(buffer.isEmpty());
    }

    @Test
    void bufferHoldsExactlyCapacityItems() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        buffer.put(1);
        buffer.put(2);
        assertFalse(buffer.isFull());
        buffer.put(3);

        assertTrue(buffer.isFull());
        assertEquals(3, buffer.size());
        buffer.take();
        assertFalse(buffer.isFull());
        assertEquals(2, buffer.size());
    }

    @Test
    void pointersWrapAroundTheRing() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        // 10 разів більше елементів, ніж місткість: покажчики багато разів проходять кільце
        for (int i = 0; i < 30; i += 2) {
            buffer.put(i);
            buffer.put(i + 1);
            assertEquals(i, buffer.take());
            assertEquals(i + 1, buffer.take());
        }
        assertTrue(buffer.isEmpty());
    }

    @Test
    void invalidCapacityAndNullItemAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<String>(0));
        RingBuffer<String> buffer = new RingBuffer<>(1);
        assertThrows(NullPointerException.class, () -> buffer.put(null));
        assertTrue(buffer.isEmpty());
    }

    @Test
    void takeBlocksOnEmptyBufferUntilPut() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(2);
        AtomicReference<String> taken = new AtomicReference<>();
        Thread consumer = new Thread(() -> {
            try {
                taken.set(buffer.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        consumer.join(BLOCK_CHECK_MS);
        assertTrue(consumer.isAlive(), "take() мав чекати на порожньому буфері");
        assertEquals(null, taken.get());

        buffer.put("дані");
        consumer.join(JOIN_MS);
        assertFalse(consumer.isAlive());
        assertEquals("дані", taken.get());
    }

    @Test
    void putBlocksOnFullBufferUntilTake() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(1);
        buffer.put("перший");
        AtomicBoolean putDone = new AtomicBoolean();
        Thread producer = new Thread(() -> {
            try {
                buffer.put("другий");
                putDone.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        producer.join(BLOCK_CHECK_MS);
        assertTrue(producer.isAlive(), "put() мав чекати на повному буфері");
        assertFalse(putDone.get());

        assertEquals("перший", buffer.take());
        producer.join(JOIN_MS);
        assertTrue(putDone.get());
        assertEquals("другий", buffer.take());
    }

    @Test
    void blockedTakeCanBeInterrupted() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(1);
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread consumer = new Thread(() -> {
            try {
                buffer.take();
            } catch (InterruptedException e) {
                interrupted.set(true);
            }
        });
        consumer.start();
        consumer.join(BLOCK_CHECK_MS);

        consumer.interrupt();
        consumer.join(JOIN_MS);
        assertTrue(interrupted.get());
    }

    @Test
    void manyProducersAndConsumersLoseAndDuplicateNothing() {
        int producers = 4;
        int consumers = 4;
        int perProducer = 10_000;
        int total = producers * perProducer;
        RingBuffer<Integer> buffer = new RingBuffer<>(16);
        Set<Integer> received = ConcurrentHashMap.newKeySet();
        AtomicBoolean duplicate = new AtomicBoolean();

        assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
            List<Thread> threads = new ArrayList<>();
            for (int p = 0; p < producers; p++) {
                int base = p * perProducer; // у кожного виробника свій діапазон чисел
                threads.add(new Thread(() -> {
                    try {
                        for (int i = 0; i < perProducer; i++) {
                            buffer.put(base + i);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            for (int c = 0; c < consumers; c++) {
                threads.add(new Thread(() -> {
                    try {
                        for (int i = 0; i < total / consumers; i++) {
                            if (!received.add(buffer.take())) {
                                duplicate.set(true);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            threads.forEach(Thread::start);
            for (Thread thread : threads) {
                thread.join();
            }
        });

        assertFalse(duplicate.get(), "елемент отримано двічі");
        assertEquals(total, received.size());
        for (int i = 0; i < total; i++) {
            assertTrue(received.contains(i), "втрачено елемент " + i);
        }
        assertTrue(buffer.isEmpty());
    }
}
