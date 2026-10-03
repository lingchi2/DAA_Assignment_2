package org.example.structures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    @Test
    void randomOperationsMatchArrayList() {
        DynamicArray actual = new DynamicArray();
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
    void getAndInsertAtBoundariesWork() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(0, 5);
        array.add(array.size(), 15);

        assertEquals(5, array.get(0));
        assertEquals(10, array.get(1));
        assertEquals(15, array.get(2));
    }

    @Test
    void containsHandlesDuplicates() {
        DynamicArray array = new DynamicArray();
        array.add(7);
        array.add(7);
        array.add(9);

        assertTrue(array.contains(7));
        assertTrue(array.contains(9));
        assertFalse(array.contains(100));
    }

    @Test
    void invalidIndicesThrow() {
        DynamicArray array = new DynamicArray();
        array.add(1);

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
    }
}
