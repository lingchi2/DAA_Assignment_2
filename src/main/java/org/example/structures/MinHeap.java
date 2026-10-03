package org.example.structures;

import org.example.metrics.Metrics;

public final class MinHeap {
    private int[] heap;
    private int size;
    public final Metrics metrics = new Metrics();

    public MinHeap() {
        heap = new int[10];
    }

    public void insert(int x) {
        ensureCapacity();
        heap[size] = x;
        metrics.moves++;
        bubbleUp(size);
        size++;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Cannot peek an empty heap");
        }
        metrics.steps++;
        return heap[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Cannot extract from an empty heap");
        }

        int min = heap[0];
        metrics.steps++;

        size--;
        if (size > 0) {
            heap[0] = heap[size];
            metrics.steps++;
            metrics.moves++;
            bubbleDown(0);
        }

        return min;
    }

    public int size() {
        return size;
    }

    public int[] getHeapArray() {
        return heap;
    }

    private void bubbleUp(int index) {
        int current = index;
        while (current > 0) {
            int parent = (current - 1) / 2;
            metrics.steps += 2;
            metrics.comparisons++;

            if (heap[current] < heap[parent]) {
                swap(current, parent);
                current = parent;
            } else {
                break;
            }
        }
    }

    private void bubbleDown(int index) {
        int current = index;

        while (true) {
            int leftChild = 2 * current + 1;
            int rightChild = leftChild + 1;
            int smallest = current;

            if (leftChild < size) {
                metrics.steps += 2;
                metrics.comparisons++;
                if (heap[leftChild] < heap[smallest]) {
                    smallest = leftChild;
                }
            }

            if (rightChild < size) {
                metrics.steps += 2;
                metrics.comparisons++;
                if (heap[rightChild] < heap[smallest]) {
                    smallest = rightChild;
                }
            }

            if (smallest == current) {
                break;
            }

            swap(current, smallest);
            current = smallest;
        }
    }

    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
        metrics.moves += 3;
    }

    private void ensureCapacity() {
        if (size < heap.length) {
            return;
        }

        int[] newHeap = new int[heap.length * 2];
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            newHeap[i] = heap[i];
            metrics.moves++;
        }
        heap = newHeap;
    }
}
