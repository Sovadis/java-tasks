package ua.kpi.java.task9;

import ua.kpi.java.task9.bank.BankDemo;
import ua.kpi.java.task9.buffer.BufferDemo;

/**
 * Лабораторна робота 9: багатопотоковий переказ коштів (завдання 1)
 * і задача «виробник – споживач» на кільцевих буферах (завдання 2).
 */
public class Main {

    /**
     * Точка входу програми.
     *
     * @param args {@code bank} – лише завдання 1, {@code buffer} – лише завдання 2,
     *             без аргументів – обидва завдання по черзі
     * @throws InterruptedException якщо головний потік перервано під час очікування
     */
    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "all";
        switch (mode) {
            case "bank" -> BankDemo.run(System.out);
            case "buffer" -> BufferDemo.run(System.out);
            case "all" -> {
                BankDemo.run(System.out);
                System.out.println();
                BufferDemo.run(System.out);
            }
            default -> System.out.println("Невідомий режим «" + mode + "»; допустимо: bank, buffer");
        }
    }
}
