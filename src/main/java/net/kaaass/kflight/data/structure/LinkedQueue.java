package net.kaaass.kflight.data.structure;

import lombok.AllArgsConstructor;

import java.util.function.Consumer;

/**
 * 链式队列
 *
 * @param <S>
 */
public class LinkedQueue<S> {

    @AllArgsConstructor
    private static class Node<S> {

        S data;

        Node<S> next = null;
    }

    private Node<S> head = null;

    private Node<S> tail = null;

    private int size = 0;

    public void push(S data) {
        var node = new Node<>(data, null);
        size++;
        if (head == null) {
            head = tail = node;
            return;
        }
        tail.next = node;
        tail = node;
    }

    public S popFront() {
        if (isEmpty())
            return null;
        var cur = head;
        head = head.next;
        size--;
        if (head == null)
            tail = null;
        return cur.data;
    }

    public S front() {
        return head == null ? null : head.data;
    }

    public S back() {
        return tail == null ? null : tail.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public void forEach(Consumer<S> consumer) {
        for (var it = head;
             it != null;
             it = it.next) {
            consumer.accept(it.data);
        }
    }
}
