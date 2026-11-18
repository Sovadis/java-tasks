package ua.kpi.java.task9.bank;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Тестовий сценарій завдання 1: тисячі потоків одночасно переказують
 * випадкові суми між випадковими рахунками, а загальна сума в банку
 * до і після переказів має збігатися.
 */
public final class BankDemo {

    private static final int ACCOUNTS = 200;
    private static final int MAX_INITIAL_BALANCE = 10_000;
    private static final int THREADS = 5_000;
    private static final int TRANSFERS_PER_THREAD = 20;
    private static final int MAX_AMOUNT = 2_000;

    private BankDemo() {
    }

    /**
     * Виконує сценарій і виводить результати.
     *
     * @param out куди виводити протокол
     * @throws InterruptedException якщо головний потік перервано під час очікування
     */
    public static void run(PrintStream out) throws InterruptedException {
        out.println("=== Завдання 1. Переказ коштів між рахунками ===");
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Bank bank = new Bank();
        for (int i = 0; i < ACCOUNTS; i++) {
            bank.openAccount(random.nextInt(MAX_INITIAL_BALANCE + 1));
        }
        List<Account> accounts = bank.getAccounts();
        long before = bank.totalBalance();
        out.println("1. Відкрито рахунків: " + accounts.size()
                + " (початковий баланс – випадковий, від 0 до " + MAX_INITIAL_BALANCE + ")");
        out.println("2. Сума коштів до переказів: " + before);

        CountDownLatch startGate = new CountDownLatch(1);
        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        List<Thread> threads = new ArrayList<>(THREADS);
        for (int i = 0; i < THREADS; i++) {
            Thread thread = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                for (int k = 0; k < TRANSFERS_PER_THREAD; k++) {
                    int fromIndex = rnd.nextInt(accounts.size());
                    // другий індекс зсуваємо, щоб рахунки завжди були різними
                    int toIndex = (fromIndex + 1 + rnd.nextInt(accounts.size() - 1)) % accounts.size();
                    int amount = 1 + rnd.nextInt(MAX_AMOUNT);
                    if (bank.transfer(accounts.get(fromIndex), accounts.get(toIndex), amount)) {
                        succeeded.incrementAndGet();
                    } else {
                        rejected.incrementAndGet();
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        out.println("3. Запущено потоків: " + THREADS + ", кожен виконує " + TRANSFERS_PER_THREAD
                + " переказів на випадкову суму від 1 до " + MAX_AMOUNT);

        long startTime = System.nanoTime();
        startGate.countDown(); // усі потоки вже чекають на «старт» і рушають одночасно
        for (Thread thread : threads) {
            thread.join();
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        long after = bank.totalBalance();
        long negative = accounts.stream().filter(a -> a.getBalance() < 0).count();
        long minBalance = accounts.stream().mapToLong(Account::getBalance).min().orElse(0);
        out.println("4. Усі потоки завершилися за " + elapsedMs + " мс");
        out.println("   Виконано переказів: " + succeeded.get()
                + ", відхилено через нестачу коштів: " + rejected.get()
                + " (усього " + (succeeded.get() + rejected.get()) + ")");
        out.println("   Сума коштів після переказів: " + after);
        out.println("   Суми до і після збігаються: " + (before == after ? "так" : "НІ"));
        out.println("   Рахунків з від'ємним балансом: " + negative
                + " (найменший баланс: " + minBalance + ")");
    }
}
