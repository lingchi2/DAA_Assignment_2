package org.example.structures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void insertMaintainsHeapProperty() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            heap.insert(random.nextInt());
            assertHeapProperty(heap);
        }
    }

    @Test
    void extractProducesNonDecreasingOutput() {
        MinHeap heap = new MinHeap();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            int value = random.nextInt();
            heap.insert(value);
            expected.add(value);
        }
        Collections.sort(expected);

        for (int expectedValue : expected) {
            assertEquals(expectedValue, heap.extractMin());
            assertHeapProperty(heap);
        }
        assertEquals(0, heap.size());
    }

    @Test
    void peekMinReturnsRootWithoutRemovingIt() {
        MinHeap heap = new MinHeap();
        heap.insert(5);
        heap.insert(2);
        heap.insert(9);

        assertEquals(2, heap.peekMin());
        assertEquals(3, heap.size());
        assertEquals(2, heap.extractMin());
    }

    @Test
    void emptyHeapThrows() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void duplicateValuesAreSupported() {
        MinHeap heap = new MinHeap();
        heap.insert(4);
        heap.insert(4);
        heap.insert(1);
        heap.insert(1);

        assertEquals(1, heap.extractMin());
        assertEquals(1, heap.extractMin());
        assertEquals(4, heap.extractMin());
        assertEquals(4, heap.extractMin());
    }

    private static void assertHeapProperty(MinHeap heap) {
        int[] values = heap.getHeapArray();
        int size = heap.size();
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;
            assertTrue(
                    values[parent] <= values[child],
                    "Heap property violated at parent " + parent + " and child " + child
            );
        }
    }
}
