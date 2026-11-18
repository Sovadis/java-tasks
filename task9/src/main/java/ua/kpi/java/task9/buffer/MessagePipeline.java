package ua.kpi.java.task9.buffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Конвеєр завдання 2: п'ять генераторів пишуть у буфер 1, два перекладачі
 * переносять повідомлення з буфера 1 у буфер 2, а з буфера 2 читає той, хто
 * викликає {@link #take()} (у програмі – головний потік).
 *
 * <p>Генератори й перекладачі працюють безкінечно, тому їх запускаємо як
 * потоки-демони: JVM не чекає на демонів і завершується, щойно закінчиться
 * останній звичайний потік – тут головний.
 */
public class MessagePipeline {

    /** Кількість потоків-генераторів (номери 1–5). */
    public static final int PRODUCERS = 5;
    /** Кількість потоків-перекладачів (номери 6–7). */
    public static final int MOVERS = 2;

    private final RingBuffer<String> first;
    private final RingBuffer<String> second;
    private final List<Thread> threads = new ArrayList<>();

    /**
     * Створює конвеєр з двома буферами однакової місткості.
     *
     * @param capacity місткість кожного буфера
     */
    public MessagePipeline(int capacity) {
        this.first = new RingBuffer<>(capacity);
        this.second = new RingBuffer<>(capacity);
    }

    /**
     * Запускає генератори й перекладачів як потоки-демони.
     *
     * @throws IllegalStateException якщо конвеєр уже запущено
     */
    public void start() {
        if (!threads.isEmpty()) {
            throw new IllegalStateException("Конвеєр уже запущено");
        }
        int number = 1;
        for (int i = 0; i < PRODUCERS; i++, number++) {
            startDaemon(new MessageProducer(number, first), "generator-" + number);
        }
        for (int i = 0; i < MOVERS; i++, number++) {
            startDaemon(new MessageMover(number, first, second), "mover-" + number);
        }
    }

    private void startDaemon(Runnable task, String name) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true); // до start(): після запуску змінити вже не можна
        threads.add(thread);
        thread.start();
    }

    /**
     * Забирає чергове повідомлення з другого буфера (чекає, якщо він порожній).
     *
     * @return перекладене повідомлення
     * @throws InterruptedException якщо потік перервано під час очікування
     */
    public String take() throws InterruptedException {
        return second.take();
    }

    /**
     * Повертає запущені потоки конвеєра.
     *
     * @return незмінний список потоків (спершу генератори, потім перекладачі)
     */
    public List<Thread> threads() {
        return List.copyOf(threads);
    }

    /**
     * Повертає місткість кожного з буферів.
     *
     * @return місткість буфера
     */
    public int capacity() {
        return first.capacity();
    }
}
