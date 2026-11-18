package ua.kpi.java.task9.buffer;

/**
 * Генератор: безкінечно записує в буфер рядки
 * «Потік № N згенерував повідомлення № K».
 */
public class MessageProducer implements Runnable {

    private final int number;
    private final RingBuffer<String> output;

    /**
     * Створює генератор.
     *
     * @param number номер потоку, що виводиться в повідомленнях
     * @param output буфер, у який записуються повідомлення
     */
    public MessageProducer(int number, RingBuffer<String> output) {
        this.number = number;
        this.output = output;
    }

    /** Генерує повідомлення, доки потік не перервуть (або доки не завершиться JVM). */
    @Override
    public void run() {
        try {
            for (long k = 1; !Thread.currentThread().isInterrupted(); k++) {
                output.put("Потік № " + number + " згенерував повідомлення № " + k);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
