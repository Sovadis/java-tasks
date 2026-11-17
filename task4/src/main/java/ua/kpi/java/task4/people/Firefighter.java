package ua.kpi.java.task4.people;

/**
 * Пожежник. Може їхати в пожежній машині, а також в автобусі чи таксі.
 */
public class Firefighter extends Human {

    /**
     * Створює пожежника.
     *
     * @param name ім'я пожежника
     */
    public Firefighter(String name) {
        super(name);
    }

    /**
     * Повертає назву ролі.
     *
     * @return «Пожежник»
     */
    @Override
    public String getRole() {
        return "Пожежник";
    }
}
