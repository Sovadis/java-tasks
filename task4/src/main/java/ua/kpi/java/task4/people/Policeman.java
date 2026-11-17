package ua.kpi.java.task4.people;

/**
 * Поліцейський. Може їхати в поліцейській машині, а також в автобусі чи таксі.
 */
public class Policeman extends Human {

    /**
     * Створює поліцейського.
     *
     * @param name ім'я поліцейського
     */
    public Policeman(String name) {
        super(name);
    }

    /**
     * Повертає назву ролі.
     *
     * @return «Поліцейський»
     */
    @Override
    public String getRole() {
        return "Поліцейський";
    }
}
