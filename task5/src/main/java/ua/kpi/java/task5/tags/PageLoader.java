package ua.kpi.java.task5.tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Завантаження HTML-сторінки за URL через потік введення {@link URLConnection#getInputStream()}.
 */
public final class PageLoader {

    private static final int TIMEOUT_MS = 15_000;

    private PageLoader() {
    }

    /**
     * Завантажує текст сторінки (вважається, що сторінка в кодуванні UTF-8).
     *
     * @param address адреса сторінки, наприклад {@code https://example.com}
     * @return HTML-текст сторінки
     * @throws IllegalArgumentException          якщо адреса некоректна (наприклад, містить пробіли або не абсолютна)
     * @throws java.net.MalformedURLException    якщо протокол адреси невідомий
     * @throws IOException                       помилка мережі або відповідь сервера з кодом помилки
     */
    public static String load(String address) throws IOException {
        URLConnection connection = URI.create(address).toURL().openConnection();
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        // Деякі сайти (зокрема Вікіпедія) відхиляють запити без User-Agent.
        connection.setRequestProperty("User-Agent", "KPI-Java-Lab/1.0 (student lab work)");

        StringBuilder html = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append('\n');
            }
        }
        return html.toString();
    }
}
