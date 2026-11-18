package ua.kpi.java.task11;

import java.util.ArrayList;
import java.util.List;

/**
 * Виводить дані на консоль у вигляді таблиці з вирівняними стовпцями.
 * Порожні значення ({@code null}) показуються як «—».
 */
public class TablePrinter {

    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    /** @param headers заголовки стовпців */
    public TablePrinter(String... headers) {
        this.headers = headers;
    }

    /**
     * Додає рядок таблиці.
     *
     * @param values значення стовпців (у тому ж порядку, що й заголовки)
     * @return ця сама таблиця, щоб можна було додавати рядки ланцюжком
     */
    public TablePrinter addRow(Object... values) {
        String[] row = new String[headers.length];
        for (int i = 0; i < row.length; i++) {
            row[i] = values[i] == null ? "—" : values[i].toString();
        }
        rows.add(row);
        return this;
    }

    /** Виводить таблицю: заголовок, лінію-роздільник, рядки й кількість рядків. */
    public void print() {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
            for (String[] row : rows) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }
        printRow(headers, widths);
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < widths.length; i++) {
            line.append(i == 0 ? "" : "-+-").append("-".repeat(widths[i]));
        }
        System.out.println(line);
        for (String[] row : rows) {
            printRow(row, widths);
        }
        System.out.println("(рядків: " + rows.size() + ")");
    }

    private static void printRow(String[] values, int[] widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(i == 0 ? "" : " | ").append(String.format("%-" + widths[i] + "s", values[i]));
        }
        System.out.println(sb.toString().stripTrailing());
    }
}
