package org.example.structures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {
    @Test
    void randomOperationsMatchArrayList() {
        MyLinkedList actual = new MyLinkedList();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int step = 0; step < 2_000; step++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt();
                int index = random.nextInt(expected.size() + 1);
                actual.add(index, value);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                actual.remove(index);
                expected.remove(index);
            }

            assertEquals(expected.size(), actual.size());
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(expected.get(i), actual.get(i));
            }
        }
    }

    @Test
    void headAndTailOperationsWork() {
        MyLinkedList list = new MyLinkedList();
        list.add(20);
        list.add(0, 10);
        list.add(list.size(), 30);

        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));

        list.remove(0);
        assertEquals(20, list.get(0));
        list.remove(list.size() - 1);
        assertEquals(1, list.size());
    }

    @Test
    void containsHandlesDuplicates() {
        MyLinkedList list = new MyLinkedList();
        list.add(7);
        list.add(7);
        list.add(9);

        assertTrue(list.contains(7));
        assertTrue(list.contains(9));
        assertFalse(list.contains(100));
    }

    @Test
    void invalidIndicesThrow() {
        MyLinkedList list = new MyLinkedList();
        list.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }
}
