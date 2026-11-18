package ua.kpi.java.task9.buffer;

/**
 * Перекладач: забирає повідомлення з першого буфера й записує в другий рядок
 * «Потік № N переклав повідомлення «…»» з вихідним повідомленням усередині.
 */
public class MessageMover implements Runnable {

    private final int number;
    private final RingBuffer<String> input;
    private final RingBuffer<String> output;

    /**
     * Створює перекладача.
     *
     * @param number номер потоку, що виводиться в повідомленнях
     * @param input буфер, з якого читаються повідомлення
     * @param output буфер, у який записуються перекладені повідомлення
     */
    public MessageMover(int number, RingBuffer<String> input, RingBuffer<String> output) {
        this.number = number;
        this.input = input;
        this.output = output;
    }

    /** Перекладає повідомлення, доки потік не перервуть (або доки не завершиться JVM). */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String message = input.take();
                output.put("Потік № " + number + " переклав повідомлення «" + message + "»");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
