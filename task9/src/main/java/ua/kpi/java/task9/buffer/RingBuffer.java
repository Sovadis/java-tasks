package ua.kpi.java.task9.buffer;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Потокобезпечний кільцевий буфер обмеженої місткості.
 *
 * <p>Буфер – однозв'язний список, замкнений у кільце. Покажчик {@code head}
 * вказує на найстаріший елемент, {@code tail} – на вільний вузол, куди буде
 * записано наступний. Запис зсуває {@code tail}, читання – {@code head}; після
 * останнього вузла обидва покажчики самі переходять на перший, бо список
 * замкнений. Збіг {@code head == tail} означає порожній буфер. Щоб повний
 * буфер не плутався з порожнім, один вузол завжди лишається вільним: буфер
 * повний, коли {@code tail.next == head}. Тому для місткості {@code n}
 * створюється {@code n + 1} вузол.
 *
 * <p>Синхронізація – {@link ReentrantLock} з двома умовами: на {@code notFull}
 * чекають лише записувачі, на {@code notEmpty} – лише читачі. Тому
 * {@code signal()} будить потік саме потрібного виду. З {@code synchronized}
 * обидва види чекали б в одній черзі монітора, і довелося б будити всіх через
 * {@code notifyAll()}. Умову перевіряють у циклі {@code while}: потік може
 * прокинутися без сигналу (spurious wakeup) або після сигналу місце/елемент
 * встигне забрати інший потік, який захопив блокування першим.
 *
 * @param <T> тип елементів; null не допускається
 */
public class RingBuffer<T> {

    /** Вузол кільця. */
    private static final class Node<T> {
        private T value;
        private Node<T> next;
    }

    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private Node<T> head;
    private Node<T> tail;

    /**
     * Створює порожній буфер.
     *
     * @param capacity найбільша кількість елементів, не менше 1
     * @throws IllegalArgumentException якщо місткість менша за 1
     */
    public RingBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Місткість буфера має бути не менше 1: " + capacity);
        }
        this.capacity = capacity;
        Node<T> first = new Node<>();
        Node<T> last = first;
        for (int i = 0; i < capacity; i++) { // ще capacity вузлів: один завжди вільний
            last.next = new Node<>();
            last = last.next;
        }
        last.next = first; // замикаємо список у кільце
        head = first;
        tail = first;
    }

    /**
     * Додає елемент у кінець буфера; якщо буфер повний, чекає на вільне місце.
     *
     * @param item елемент, не null
     * @throws InterruptedException якщо потік перервано під час очікування
     * @throws NullPointerException якщо елемент дорівнює null
     */
    public void put(T item) throws InterruptedException {
        Objects.requireNonNull(item, "Елемент буфера не може бути null");
        lock.lockInterruptibly();
        try {
            while (tail.next == head) { // повний
                notFull.await();
            }
            tail.value = item;
            tail = tail.next;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Забирає найстаріший елемент; якщо буфер порожній, чекає на появу даних.
     *
     * @return елемент з початку буфера
     * @throws InterruptedException якщо потік перервано під час очікування
     */
    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (head == tail) { // порожній
                notEmpty.await();
            }
            T item = head.value;
            head.value = null; // не тримаємо посилання на вже забраний елемент
            head = head.next;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Повертає кількість елементів у буфері (обходить кільце від head до tail).
     *
     * @return кількість елементів на момент виклику
     */
    public int size() {
        lock.lock();
        try {
            int count = 0;
            for (Node<T> node = head; node != tail; node = node.next) {
                count++;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Перевіряє, чи буфер порожній.
     *
     * @return {@code true}, якщо {@code head == tail}
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return head == tail;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Перевіряє, чи буфер повний.
     *
     * @return {@code true}, якщо {@code tail.next == head}
     */
    public boolean isFull() {
        lock.lock();
        try {
            return tail.next == head;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Повертає місткість буфера.
     *
     * @return найбільша кількість елементів
     */
    public int capacity() {
        return capacity;
    }
}
