package ua.kpi.java.task2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Завдання 2.1: перевірка методу {@code equals} класу {@link Person}
 * після перетворення об'єкта в JSON і назад за допомогою бібліотеки Gson.
 */
public class Main {

    /**
     * Точка входу: виконує сценарій a) - d) із завдання.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("=== Завдання 2.1. Метод equals для класу Person ===");
        System.out.println();

        // a) Створюємо екземпляр Person.
        Person original = new Person("Петренко", "Олена", 19);
        System.out.println("a) Створюємо екземпляр Person:");
        System.out.println("   " + original);
        System.out.println();

        // b) Конвертуємо об'єкт у JSON.
        String json = gson.toJson(original);
        System.out.println("b) Конвертуємо об'єкт у JSON (Gson):");
        System.out.print(json.indent(3));
        System.out.println();

        // c) Конвертуємо JSON назад в об'єкт.
        Person restored = gson.fromJson(json, Person.class);
        System.out.println("c) Конвертуємо JSON назад в об'єкт:");
        System.out.println("   " + restored);
        System.out.println();

        // d) Порівнюємо початковий і одержаний об'єкти.
        System.out.println("d) Порівнюємо початковий і одержаний об'єкти:");
        System.out.println("   original.equals(restored) = " + original.equals(restored));
        System.out.println("   restored.equals(original) = " + restored.equals(original));
        System.out.println("   original == restored      = " + (original == restored)
                + "  (це два різні об'єкти в пам'яті)");
        System.out.println("   original.hashCode()       = " + original.hashCode());
        System.out.println("   restored.hashCode()       = " + restored.hashCode());
        System.out.println();

        if (original.equals(restored)) {
            System.out.println("Висновок: об'єкти рівні за вмістом (equals повертає true),");
            System.out.println("хоча це різні екземпляри (== повертає false), а їхні хеш-коди однакові.");
        } else {
            System.out.println("Висновок: об'єкти НЕ рівні — метод equals працює неправильно.");
        }
        System.out.println();

        // Додатково: особа з іншим віком не повинна дорівнювати початковій.
        Person older = new Person("Петренко", "Олена", 20);
        System.out.println("Додатково: порівняння з особою, у якої інший вік:");
        System.out.println("   " + older);
        System.out.println("   original.equals(older) = " + original.equals(older));
    }
}
