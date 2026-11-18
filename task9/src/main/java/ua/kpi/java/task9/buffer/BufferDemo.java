package ua.kpi.java.task9.buffer;

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Демонстрація завдання 2: головний потік читає й друкує 100 повідомлень
 * з другого буфера конвеєра.
 */
public final class BufferDemo {

    private static final int MESSAGES = 100;
    private static final int CAPACITY = 10;
    /** Номер перекладача і номер генератора з перекладеного повідомлення. */
    private static final Pattern MOVED =
            Pattern.compile("^Потік № (\\d+) переклав повідомлення «Потік № (\\d+) згенерував");

    private BufferDemo() {
    }

    /**
     * Запускає конвеєр, друкує повідомлення та підсумок.
     *
     * @param out куди виводити протокол
     * @throws InterruptedException якщо головний потік перервано під час очікування
     */
    public static void run(PrintStream out) throws InterruptedException {
        out.println("=== Завдання 2. Виробник – споживач на кільцевих буферах ===");
        MessagePipeline pipeline = new MessagePipeline(CAPACITY);
        pipeline.start();
        boolean allDaemons = pipeline.threads().stream().allMatch(Thread::isDaemon);
        out.println("Місткість кожного з двох буферів: " + pipeline.capacity() + " повідомлень");
        out.println("Запущено генераторів: " + MessagePipeline.PRODUCERS + " (потоки № 1–5), перекладачів: "
                + MessagePipeline.MOVERS + " (потоки № 6–7); усі вони потоки-демони: "
                + (allDaemons ? "так" : "НІ"));
        out.println("Головний потік читає " + MESSAGES + " повідомлень з буфера 2:");

        Map<Integer, Integer> byMover = new TreeMap<>();
        Map<Integer, Integer> byGenerator = new TreeMap<>();
        for (int i = 1; i <= MESSAGES; i++) {
            String message = pipeline.take();
            out.printf("%4d. %s%n", i, message);
            Matcher m = MOVED.matcher(message);
            if (m.find()) {
                byMover.merge(Integer.parseInt(m.group(1)), 1, Integer::sum);
                byGenerator.merge(Integer.parseInt(m.group(2)), 1, Integer::sum);
            }
        }

        long alive = pipeline.threads().stream().filter(Thread::isAlive).count();
        out.println("Прочитано повідомлень: " + MESSAGES);
        out.println("Повідомлень за генераторами (потік → кількість): " + byGenerator);
        out.println("Повідомлень за перекладачами (потік → кількість): " + byMover);
        out.println("Потоків-демонів ще працює: " + alive + " з " + pipeline.threads().size()
                + "; головний потік завершується, і JVM зупиняє їх разом із собою.");
    }
}
