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
| [`task8`](task8) | №8 Паралельні обчислення | `ParallelMonteCarloPi`: число π методом Монте-Карло в N потоках, заміри точності й прискорення |
| [`task9`](task9) | №9 Багатопотоковість | переказ коштів без deadlock-ів; власний потокобезпечний кільцевий буфер, виробники й споживачі |
| [`task10`](task10) | №10 Рефлексія, логування, i18n | зміна `String` через рефлексію; java.util.logging у застосунку «Книги»; вибір мови через `ResourceBundle` |
| [`task11`](task11) | №11 Бази даних | JDBC + H2: відділи, співробітники, завдання; з'єднання з `db.properties`, DAO на `PreparedStatement` |

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

- **Особливості окремих робіт:**
  - `task8` — єдиний аргумент програми = кількість потоків: `./gradlew :task8:run --args=8`.
  - `task10` — для зміни `String` через рефлексію JVM потрібен параметр `--add-opens java.base/java.lang=ALL-UNNAMED`
    (у Gradle-задачах `run`/`test` уже додано; в IntelliJ — додати у VM options).
  - `task11` — база H2 створюється у `task11/data/` зі скриптів `schema.sql` і `data.sql` під час першого запуску;
    параметри з'єднання — у `task11/src/main/resources/db.properties`; веб-консоль H2: `./gradlew :task11:h2Console`.
