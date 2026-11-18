-- Схема бази даних «Відділи — Співробітники — Завдання» (лабораторна робота 11).
-- Скрипт можна виконати повторно: спочатку таблиці видаляються (у зворотному до зв'язків порядку).

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;

-- Відділи
CREATE TABLE departments (
    department_id INTEGER     NOT NULL,  -- Номер відділу
    name          CHAR(30)    NOT NULL,  -- Назва
    phone         CHAR(15),              -- Телефон (O)
    CONSTRAINT departments_pk PRIMARY KEY (department_id)
);

-- Співробітники
CREATE TABLE employees (
    employee_id   INTEGER     NOT NULL,  -- Номер співробітника
    last_name     VARCHAR(20) NOT NULL,  -- Прізвище
    first_name    VARCHAR(10) NOT NULL,  -- Ім'я
    position      VARCHAR(20),           -- Посада (O)
    department_id INTEGER     NOT NULL,  -- Номер відділу (FK)
    CONSTRAINT employees_pk PRIMARY KEY (employee_id),
    CONSTRAINT departments_employees_fk FOREIGN KEY (department_id)
        REFERENCES departments (department_id)
);

-- Завдання: номер генерує СУБД; разом зі співробітником видаляються і його завдання.
CREATE TABLE tasks (
    task_id       INTEGER     NOT NULL AUTO_INCREMENT,  -- Номер завдання
    description   VARCHAR(50) NOT NULL,                 -- Опис завдання
    employee_id   INTEGER     NOT NULL,                 -- Номер співробітника (FK)
    CONSTRAINT tasks_pk PRIMARY KEY (task_id),
    CONSTRAINT employees_tasks_fk FOREIGN KEY (employee_id)
        REFERENCES employees (employee_id) ON DELETE CASCADE
);
