package ua.kpi.java.task9.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Модульні тести рахунку й потокобезпечного переказу коштів.
 */
class BankTest {

    /** Запускає {@code threads} потоків, які одночасно (після спільного «старту») виконують {@code task}. */
    private static void runConcurrently(int threads, Runnable task) throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        List<Thread> started = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                task.run();
            });
            started.add(thread);
            thread.start();
        }
        startGate.countDown();
        for (Thread thread : started) {
            thread.join();
        }
    }

    @Test
    void transferMovesMoney() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);
        Account b = bank.openAccount(50);

        assertTrue(bank.transfer(a, b, 30));

        assertEquals(70, a.getBalance());
        assertEquals(80, b.getBalance());
    }

    @Test
    void wholeBalanceCanBeTransferred() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);
        Account b = bank.openAccount(0);

        assertTrue(bank.transfer(a, b, 100));

        assertEquals(0, a.getBalance());
        assertEquals(100, b.getBalance());
    }

    @Test
    void insufficientFundsRejectsTransferAndKeepsBalances() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);
        Account b = bank.openAccount(50);

        assertFalse(bank.transfer(a, b, 101));

        assertEquals(100, a.getBalance());
        assertEquals(50, b.getBalance());
    }

    @Test
    void selfTransferIsRejectedAndBalanceUnchanged() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);

        assertThrows(IllegalArgumentException.class, () -> bank.transfer(a, a, 10));
        assertEquals(100, a.getBalance());
    }

    @Test
    void invalidAmountsAreRejected() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);
        Account b = bank.openAccount(100);

        assertThrows(IllegalArgumentException.class, () -> bank.transfer(a, b, 0));
        assertThrows(IllegalArgumentException.class, () -> bank.transfer(a, b, -5));
        assertThrows(IllegalArgumentException.class, () -> a.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> a.withdraw(-1));
        assertThrows(IllegalArgumentException.class, () -> bank.openAccount(-1));
        assertEquals(100, a.getBalance());
        assertEquals(100, b.getBalance());
    }

    @Test
    void nullAccountsAreRejected() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);

        assertThrows(NullPointerException.class, () -> bank.transfer(null, a, 10));
        assertThrows(NullPointerException.class, () -> bank.transfer(a, null, 10));
    }

    @Test
    void withdrawMoreThanBalanceThrowsAndKeepsBalance() {
        Account a = new Account(10);

        assertThrows(IllegalStateException.class, () -> a.withdraw(11));
        assertEquals(10, a.getBalance());
    }

    @Test
    void accountIdsAreUnique() {
        Account a = new Account(0);
        Account b = new Account(0);

        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void totalIsInvariantUnderHeavyConcurrency() throws InterruptedException {
        Bank bank = new Bank();
        Random random = new Random(9);
        for (int i = 0; i < 100; i++) {
            bank.openAccount(random.nextInt(1_000));
        }
        List<Account> accounts = bank.getAccounts();
        long before = bank.totalBalance();
        AtomicInteger attempts = new AtomicInteger();

        runConcurrently(2_000, () -> {
            Random rnd = new Random();
            for (int k = 0; k < 50; k++) {
                int from = rnd.nextInt(accounts.size());
                int to = (from + 1 + rnd.nextInt(accounts.size() - 1)) % accounts.size();
                bank.transfer(accounts.get(from), accounts.get(to), 1 + rnd.nextInt(500));
                attempts.incrementAndGet();
            }
        });

        assertEquals(2_000 * 50, attempts.get());
        assertEquals(before, bank.totalBalance());
        assertTrue(accounts.stream().allMatch(a -> a.getBalance() >= 0));
    }

    @Test
    void concurrentDrainNeverMakesBalanceNegative() throws InterruptedException {
        Bank bank = new Bank();
        Account source = bank.openAccount(1_000);
        Account target = bank.openAccount(0);
        AtomicInteger succeeded = new AtomicInteger();

        // 500 потоків намагаються зняти по 10: коштів вистачає рівно на 100 переказів
        runConcurrently(500, () -> {
            if (bank.transfer(source, target, 10)) {
                succeeded.incrementAndGet();
            }
        });

        assertEquals(100, succeeded.get());
        assertEquals(0, source.getBalance());
        assertEquals(1_000, target.getBalance());
    }

    @Test
    void oppositeTransfersBetweenSameAccountsDoNotDeadlock() {
        Bank bank = new Bank();
        Account a = bank.openAccount(1_000_000);
        Account b = bank.openAccount(1_000_000);

        // без упорядкування блокувань такий тест майже напевно «зависає»
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            AtomicInteger turn = new AtomicInteger();
            runConcurrently(1_000, () -> {
                boolean forward = turn.getAndIncrement() % 2 == 0;
                for (int k = 0; k < 1_000; k++) {
                    if (forward) {
                        bank.transfer(a, b, 1);
                    } else {
                        bank.transfer(b, a, 1);
                    }
                }
            });
        });

        assertEquals(2_000_000, a.getBalance() + b.getBalance());
    }
}
