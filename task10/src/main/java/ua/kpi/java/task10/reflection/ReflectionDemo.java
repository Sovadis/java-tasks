package ua.kpi.java.task10.reflection;

import ua.kpi.java.task10.ConsoleInput;

import java.lang.reflect.InaccessibleObjectException;

/**
 * Завдання 10.1: зміна значення рядка рефлексією для рядка-літерала і для рядка, введеного з клавіатури.
 * Нове значення задається програмно (Enter) або вводиться з клавіатури.
 * <p>
 * Діалог цього завдання лише українською: вибір мови (завдання 10.3) стосується застосунку 10.2.
 */
public class ReflectionDemo {

    /** Нове значення, якщо користувач не ввів власне (задане програмно). */
    static final String DEFAULT_REPLACEMENT = "Hello, Java!";

    private final ConsoleInput input;

    /**
     * @param input введення з клавіатури
     */
    public ReflectionDemo(ConsoleInput input) {
        this.input = input;
    }

    /** Цикл меню завдання до вибору «0». */
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1. Рядок-літерал");
            System.out.println("2. Рядок, введений з клавіатури");
            System.out.println("0. Повернутися до головного меню");
            String choice = input.readLine("Ваш вибір: ");
            try {
                switch (choice) {
                    case "1" -> changeLiteral();
                    case "2" -> changeTypedString();
                    case "0" -> running = false;
                    default -> System.out.println("Немає пункту меню «" + choice + "». Спробуйте ще раз.");
                }
            } catch (InaccessibleObjectException e) {
                System.out.println("Помилка: доступ до приватних полів java.lang.String заборонено ("
                        + e.getMessage() + ").");
                System.out.println("Запустіть JVM з параметром --add-opens java.base/java.lang=ALL-UNNAMED");
            } catch (ReflectiveOperationException e) {
                System.out.println("Помилка рефлексії: " + e);
            }
        }
    }

    private void changeLiteral() throws ReflectiveOperationException {
        System.out.println();
        System.out.println("--- Рядок-літерал ---");
        // Обидві змінні отримують один і той самий об'єкт з пулу рядків (літерали інтернуються).
        String literal = "Привіт, світ!";
        String sameLiteral = "Привіт, світ!";
        System.out.println("Код: String literal = \"Привіт, світ!\"; String sameLiteral = \"Привіт, світ!\";");
        System.out.println("literal == sameLiteral: " + (literal == sameLiteral));
        printState("До зміни", "literal", literal);

        String replacement = input.readLineOrDefault(
                "Введіть нове значення (Enter — «" + DEFAULT_REPLACEMENT + "»): ", DEFAULT_REPLACEMENT);
        StringMutator.replaceValue(literal, replacement);

        printState("Після зміни", "literal", literal);
        System.out.println("Побічний ефект пулу рядків (той самий об'єкт):");
        System.out.println("  sameLiteral          = «" + sameLiteral + "»");
        System.out.println("  літерал з методу     = «" + literalFromOtherMethod() + "»");
    }

    private void changeTypedString() throws ReflectiveOperationException {
        System.out.println();
        System.out.println("--- Рядок, введений з клавіатури ---");
        String typed = input.readNonEmptyLine("Введіть рядок: ");
        printState("До зміни", "typed", typed);
        int hashBefore = typed.hashCode();

        String replacement = input.readLineOrDefault(
                "Введіть нове значення (Enter — «" + DEFAULT_REPLACEMENT + "»): ", DEFAULT_REPLACEMENT);
        StringMutator.replaceValue(typed, replacement);

        printState("Після зміни", "typed", typed);
        System.out.println("hashCode(): до зміни " + hashBefore + ", після зміни " + typed.hashCode()
                + " (дорівнює hashCode нового значення: " + (typed.hashCode() == replacement.hashCode()) + ")");
    }

    private static void printState(String title, String name, String s) throws ReflectiveOperationException {
        System.out.println(title + ":");
        System.out.println("  " + name + " = «" + s + "»");
        System.out.println("  " + StringMutator.describe(s));
    }

    /** Той самий літерал в іншому місці коду — інструкція ldc повертає той самий об'єкт з пулу. */
    private static String literalFromOtherMethod() {
        return "Привіт, світ!";
    }
}
