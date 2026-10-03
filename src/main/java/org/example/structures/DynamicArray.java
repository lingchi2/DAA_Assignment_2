package org.example.structures;

import org.example.metrics.Metrics;

public final class DynamicArray {
    private int[] data;
    private int size;
    public final Metrics metrics = new Metrics();

    public DynamicArray() {
        data = new int[10];
    }

    public void add(int x) {
        ensureCapacity();
        data[size] = x;
        size++;
    }

    public void add(int index, int x) {
        checkPosition(index);
        ensureCapacity();

        for (int i = size; i > index; i--) {
            metrics.steps++;
            data[i] = data[i - 1];
            metrics.moves++;
        }
        data[index] = x;
        size++;
    }

    public void remove(int index) {
        checkElement(index);

        for (int i = index; i < size - 1; i++) {
            metrics.steps++;
            data[i] = data[i + 1];
            metrics.moves++;
        }
        size--;
    }

    public int get(int index) {
        checkElement(index);
        metrics.steps++;
        return data[index];
    }

    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            metrics.comparisons++;
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] newData = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            newData[i] = data[i];
            metrics.moves++;
        }
        data = newData;
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
