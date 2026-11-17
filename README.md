# Мова програмування Java — лабораторні роботи

Савицький Денис, група ІА-41 (КПІ ім. Ігоря Сікорського). Номер у загальному списку — 85, варіант 5.

| Модуль | Робота | Що виконано |
|---|---|---|
| [`task1`](task1) | №1 Цикли, масиви, рядки | варіант 5: слова, які складаються тільки з різних символів |
| [`task2`](task2) | №2 equals | завдання 2.1: `Person.equals/hashCode`, JSON (Gson), тести EqualsVerifier |
| [`task3`](task3) | №3 ООП | завдання 3.2: книги, MVC, меню, `Comparator` |
| [`task4`](task4) | №4 Generics | завдання 4.1: транспорт і пасажири, wildcard, модульні тести |
| [`task5`](task5) | №5 I/O Streams | усі 4 завдання: рядок з макс. кількістю слів, збереження книг у файл, шифр через Filter-потоки, частота тегів за URL |
| [`task6`](task6) | №6 Колекції | Simple Task: англо-український перекладач на `HashMap<String, String>` |
| [`task7`](task7) | №7 Lambda | варіант 5 за допомогою lambda-виразів і Stream API |

## Запуск

Потрібен JDK 21. Проєкт — багатомодульний Gradle (wrapper у репозиторії).

- **IntelliJ IDEA:** відкрити папку проєкту, запустити `Main` потрібного модуля (`taskN/src/main/java/ua/kpi/java/taskN/Main.java`).
- **Тести всіх робіт:** `./gradlew test`, однієї роботи — `./gradlew :task4:test`.
- **З консолі:** `./gradlew :task1:run`.

  На Windows Gradle може спотворювати кирилицю в консолі. Тоді запускати так:

  ```
  GRADLE_OPTS="-Dfile.encoding=UTF-8" ./gradlew -q --console=plain -Dorg.gradle.jvmargs=-Dfile.encoding=UTF-8 :task1:run
  ```

  або напряму: `java -Dstdout.encoding=UTF-8 -cp task1/build/classes/java/main ua.kpi.java.task1.Main`.
