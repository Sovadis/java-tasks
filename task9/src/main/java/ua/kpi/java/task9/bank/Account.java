package ua.kpi.java.task9.bank;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Банківський рахунок з унікальним номером і невід'ємним балансом.
 *
 * <p>Методи, що читають або змінюють баланс, синхронізовані на самому рахунку
 * (власний монітор об'єкта). Той самий монітор захоплює {@link Bank#transfer},
 * тому поодинокі операції з рахунком не можуть вклинитися посеред переказу.
 * Монітор реентерабельний: потік, який уже тримає рахунок у {@code transfer},
 * може без блокування викликати {@link #withdraw} і {@link #deposit}.
 */
public class Account {

    /** Лічильник номерів; спільний для всіх потоків, тому атомарний. */
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    private final long id;
    private long balance;

    /**
     * Відкриває рахунок з початковим балансом і новим унікальним номером.
     *
     * @param initialBalance початковий баланс, не менше 0
     * @throws IllegalArgumentException якщо баланс від'ємний
     */
    public Account(long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Від'ємний початковий баланс: " + initialBalance);
        }
        this.id = NEXT_ID.getAndIncrement();
        this.balance = initialBalance;
    }

    /**
     * Повертає номер рахунку. Номери не повторюються, тому за ними можна
     * впорядкувати захоплення блокувань.
     *
     * @return унікальний номер рахунку
     */
    public long getId() {
        return id;
    }

    /**
     * Повертає поточний баланс.
     *
     * @return баланс рахунку
     */
    public synchronized long getBalance() {
        return balance;
    }

    /**
     * Зараховує кошти на рахунок.
     *
     * @param amount сума, більша за 0
     * @throws IllegalArgumentException якщо сума не додатна
     */
    public synchronized void deposit(int amount) {
        requirePositive(amount);
        balance = Math.addExact(balance, amount);
    }

    /**
     * Знімає кошти з рахунку. Баланс ніколи не стає від'ємним.
     *
     * @param amount сума, більша за 0
     * @throws IllegalArgumentException якщо сума не додатна
     * @throws IllegalStateException якщо на рахунку недостатньо коштів
     */
    public synchronized void withdraw(int amount) {
        requirePositive(amount);
        if (amount > balance) {
            throw new IllegalStateException("Недостатньо коштів на рахунку №" + id
                    + ": баланс " + balance + ", потрібно " + amount);
        }
        balance -= amount;
    }

    /** Спільна перевірка суми для рахунку й банку. */
    static void requirePositive(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сума має бути більшою за 0: " + amount);
        }
    }

    @Override
    public String toString() {
        return "Рахунок №" + id;
    }
}
