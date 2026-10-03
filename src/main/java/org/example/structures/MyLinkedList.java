package org.example.structures;

import org.example.metrics.Metrics;

public final class MyLinkedList {
    private static final class Node {
        int value;
        Node next;
        Node prev;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    public final Metrics metrics = new Metrics();

    public void add(int x) {
        Node newNode = new Node(x);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            metrics.moves++;
            newNode.prev = tail;
            metrics.moves++;
            tail = newNode;
            metrics.moves++;
        }
        size++;
    }

    public void add(int index, int x) {
        checkPosition(index);

        if (index == size) {
            add(x);
            return;
        }

        if (index == 0) {
            Node newNode = new Node(x);
            newNode.next = head;
            metrics.moves++;
            head.prev = newNode;
            metrics.moves++;
            head = newNode;
            metrics.moves++;
            size++;
            return;
        }

        Node current = getNode(index);
        Node newNode = new Node(x);

        newNode.prev = current.prev;
        metrics.moves++;
        newNode.next = current;
        metrics.moves++;
        current.prev.next = newNode;
        metrics.moves++;
        current.prev = newNode;
        metrics.moves++;
        size++;
    }

    public void remove(int index) {
        checkElement(index);
        Node current = getNode(index);

        if (current.prev != null) {
            current.prev.next = current.next;
            metrics.moves++;
        } else {
            head = current.next;
            metrics.moves++;
        }

        if (current.next != null) {
            current.next.prev = current.prev;
            metrics.moves++;
        } else {
            tail = current.prev;
            metrics.moves++;
        }

        size--;
    }

    public int get(int index) {
        checkElement(index);
        return getNode(index).value;
    }

    public boolean contains(int x) {
        Node current = head;
        while (current != null) {
            metrics.steps++;
            metrics.comparisons++;
            if (current.value == x) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    private Node getNode(int index) {
        if (index < size / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                metrics.steps++;
                current = current.next;
            }
            return current;
        }

        Node current = tail;
        for (int i = size - 1; i > index; i--) {
            metrics.steps++;
            current = current.prev;
        }
        return current;
    }

    private void checkElement(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    private void checkPosition(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }
}
