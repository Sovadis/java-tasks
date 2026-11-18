package ua.kpi.java.task9.bank;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Банк: набір рахунків і потокобезпечний переказ коштів між ними.
 */
public class Bank {

    private final List<Account> accounts = new CopyOnWriteArrayList<>();

    /**
     * Відкриває в банку новий рахунок.
     *
     * @param initialBalance початковий баланс, не менше 0
     * @return створений рахунок
     */
    public Account openAccount(long initialBalance) {
        Account account = new Account(initialBalance);
        accounts.add(account);
        return account;
    }

    /**
     * Повертає рахунки банку.
     *
     * @return незмінний список рахунків у порядку відкриття
     */
    public List<Account> getAccounts() {
        return List.copyOf(accounts);
    }

    /**
     * Рахує суму коштів на всіх рахунках.
     *
     * <p>Рахунки блокуються по одному, тому результат точний лише тоді, коли
     * переказів у цей момент немає: інакше гроші, що «в дорозі», можуть бути
     * пораховані двічі або жодного разу. У демонстрації й тестах суму рахують
     * до запуску потоків і після їх завершення.
     *
     * @return загальна сума коштів
     */
    public long totalBalance() {
        long total = 0;
        for (Account account : accounts) {
            total += account.getBalance();
        }
        return total;
    }

    /**
     * Переказує {@code amount} з рахунку {@code from} на рахунок {@code to}
     * як одну неподільну операцію.
     *
     * <p>На час переказу блокуються обидва рахунки, тож жоден інший потік не
     * побачить стану, коли гроші вже зняті, але ще не зараховані. Блокування
     * захоплюються завжди в одному порядку – спершу рахунок з меншим номером.
     * Взаємне блокування (deadlock) потребує циклу очікування: потік 1 тримає A
     * і чекає B, а потік 2 тримає B і чекає A. За єдиного порядку обидва потоки
     * спершу беруть A, тому другий чекає ще на першому рахунку, нічого не
     * тримаючи, і цикл не може виникнути.
     *
     * <p>Нестача коштів – звичайна ситуація для банку, а не помилка програми,
     * тому метод повертає {@code false} і нічого не змінює. Некоректні аргументи
     * (null, недодатна сума, той самий рахунок) – помилка того, хто викликає,
     * і для них кидається виняток.
     *
     * @param from рахунок, з якого знімаються кошти
     * @param to рахунок, на який зараховуються кошти
     * @param amount сума, більша за 0
     * @return {@code true}, якщо переказ виконано; {@code false}, якщо на
     *         рахунку {@code from} недостатньо коштів
     * @throws NullPointerException якщо рахунок дорівнює null
     * @throws IllegalArgumentException якщо сума не додатна або
     *         {@code from} і {@code to} – той самий рахунок
     */
    public boolean transfer(Account from, Account to, int amount) {
        Objects.requireNonNull(from, "Рахунок-відправник не задано");
        Objects.requireNonNull(to, "Рахунок-отримувач не задано");
        Account.requirePositive(amount);
        if (from == to) {
            throw new IllegalArgumentException("Переказ на той самий рахунок: " + from);
        }

        Account first = from.getId() < to.getId() ? from : to;
        Account second = first == from ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    return false;
                }
                from.withdraw(amount);
                to.deposit(amount);
                return true;
            }
        }
    }
}
