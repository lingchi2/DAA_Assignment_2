package org.example.benchmark;

import org.example.metrics.Metrics;
import org.example.structures.DynamicArray;
import org.example.structures.MinHeap;
import org.example.structures.MyLinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public final class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int WARMUP_RUNS = 1;
    private static final int MEASURED_RUNS = 5;
    private static final int RANDOM_ACCESS_QUERIES = 10_000;
    private static final int SEARCH_QUERIES = 1_000;
    private static final int INSERT_REMOVE_OPERATIONS = 1_000;

    private Benchmark() {
    }

    public static void main(String[] args) throws IOException {
        File resultsDir = new File("results");
        if (!resultsDir.exists() && !resultsDir.mkdirs()) {
            throw new IOException("Could not create results directory");
        }

        File csvFile = new File(resultsDir, "results.csv");
        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile))) {
            csvWriter.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                runRandomAccess(n, csvWriter);
                runSearch(n, csvWriter);
                runInsertRemove(n, "head", csvWriter);
                runInsertRemove(n, "middle", csvWriter);
                runPriorityProcessing(n, csvWriter);
                System.out.println("Finished size n = " + n);
            }
        }

        System.out.println("Benchmark finished. Results saved to " + csvFile.getPath());
    }

    private static void runRandomAccess(int n, PrintWriter csv) {
        Random random = new Random(42);
        int[] data = generateData(n, random);
        int[] queries = new int[RANDOM_ACCESS_QUERIES];
        for (int i = 0; i < queries.length; i++) {
            queries[i] = random.nextInt(n);
        }

        Metrics[] daRuns = new Metrics[MEASURED_RUNS];
        Metrics[] listRuns = new Metrics[MEASURED_RUNS];

        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            DynamicArray da = new DynamicArray();
            MyLinkedList list = new MyLinkedList();
            fill(da, list, data);

            da.metrics.reset();
            long start = System.nanoTime();
            for (int query : queries) {
                da.get(query);
            }
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                da.metrics.timeNano = elapsed;
                daRuns[run - WARMUP_RUNS] = da.metrics.copy();
            }

            list.metrics.reset();
            start = System.nanoTime();
            for (int query : queries) {
                list.get(query);
            }
            elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                list.metrics.timeNano = elapsed;
                listRuns[run - WARMUP_RUNS] = list.metrics.copy();
            }
        }

        writeResult(csv, "RandomAccess", "-", "DynamicArray", n, getMedian(daRuns));
        writeResult(csv, "RandomAccess", "-", "MyLinkedList", n, getMedian(listRuns));
    }

    private static void runSearch(int n, PrintWriter csv) {
        Random random = new Random(42);
        int[] data = generateData(n, random);
        int[] queries = generateSearchQueries(data, random);

        Metrics[] daRuns = new Metrics[MEASURED_RUNS];
        Metrics[] listRuns = new Metrics[MEASURED_RUNS];

        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            DynamicArray da = new DynamicArray();
            MyLinkedList list = new MyLinkedList();
            fill(da, list, data);

            da.metrics.reset();
            long start = System.nanoTime();
            for (int query : queries) {
                da.contains(query);
            }
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                da.metrics.timeNano = elapsed;
                daRuns[run - WARMUP_RUNS] = da.metrics.copy();
            }

            list.metrics.reset();
            start = System.nanoTime();
            for (int query : queries) {
                list.contains(query);
            }
            elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                list.metrics.timeNano = elapsed;
                listRuns[run - WARMUP_RUNS] = list.metrics.copy();
            }
        }

        writeResult(csv, "Search", "-", "DynamicArray", n, getMedian(daRuns));
        writeResult(csv, "Search", "-", "MyLinkedList", n, getMedian(listRuns));
    }

    private static void runInsertRemove(int n, String variant, PrintWriter csv) {
        Random random = new Random(42);
        int[] data = generateData(n, random);
        int[] insertedValues = new int[INSERT_REMOVE_OPERATIONS];
        for (int i = 0; i < insertedValues.length; i++) {
            insertedValues[i] = random.nextInt();
        }

        Metrics[] daRuns = new Metrics[MEASURED_RUNS];
        Metrics[] listRuns = new Metrics[MEASURED_RUNS];

        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            DynamicArray da = new DynamicArray();
            MyLinkedList list = new MyLinkedList();
            fill(da, list, data);

            da.metrics.reset();
            long start = System.nanoTime();
            for (int i = 0; i < INSERT_REMOVE_OPERATIONS; i++) {
                int index = variant.equals("head") ? 0 : n / 2;
                da.add(index, insertedValues[i]);
                da.remove(index);
            }
            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                da.metrics.timeNano = elapsed;
                daRuns[run - WARMUP_RUNS] = da.metrics.copy();
            }

            list.metrics.reset();
            start = System.nanoTime();
            for (int i = 0; i < INSERT_REMOVE_OPERATIONS; i++) {
                int index = variant.equals("head") ? 0 : n / 2;
                list.add(index, insertedValues[i]);
                list.remove(index);
            }
            elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                list.metrics.timeNano = elapsed;
                listRuns[run - WARMUP_RUNS] = list.metrics.copy();
            }
        }

        writeResult(csv, "InsertRemove", variant, "DynamicArray", n, getMedian(daRuns));
        writeResult(csv, "InsertRemove", variant, "MyLinkedList", n, getMedian(listRuns));
    }

    private static void runPriorityProcessing(int n, PrintWriter csv) {
        Random random = new Random(42);
        int[] data = generateData(n, random);
        Metrics[] heapRuns = new Metrics[MEASURED_RUNS];

        for (int run = 0; run < WARMUP_RUNS + MEASURED_RUNS; run++) {
            MinHeap heap = new MinHeap();
            for (int value : data) {
                heap.insert(value);
            }

            heap.metrics.reset();
            long start = System.nanoTime();
            int previous = Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
                int current = heap.extractMin();
                if (i > 0 && current < previous) {
                    throw new AssertionError("MinHeap output is not non-decreasing at position " + i);
                }
                previous = current;
            }

            if (heap.size() != 0) {
                throw new AssertionError("Heap must be empty after n extractMin calls");
            }

            long elapsed = System.nanoTime() - start;
            if (run >= WARMUP_RUNS) {
                heap.metrics.timeNano = elapsed;
                heapRuns[run - WARMUP_RUNS] = heap.metrics.copy();
            }
        }

        writeResult(csv, "PriorityProcessing", "-", "MinHeap", n, getMedian(heapRuns));
    }

    private static int[] generateData(int n, Random random) {
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt();
        }
        return data;
    }

    private static int[] generateSearchQueries(int[] data, Random random) {
        int[] queries = new int[SEARCH_QUERIES];
        for (int i = 0; i < SEARCH_QUERIES / 2; i++) {
            queries[i] = data[random.nextInt(data.length)];
        }

        int absent = Integer.MIN_VALUE;
        for (int i = SEARCH_QUERIES / 2; i < SEARCH_QUERIES; i++) {
            while (containsRaw(data, absent)) {
                absent++;
            }
            queries[i] = absent++;
        }
        return queries;
    }

    private static boolean containsRaw(int[] data, int value) {
        for (int element : data) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    private static void fill(DynamicArray da, MyLinkedList list, int[] data) {
        for (int value : data) {
            da.add(value);
            list.add(value);
        }
    }

    private static Metrics getMedian(Metrics[] runs) {
        Metrics[] sorted = Arrays.copyOf(runs, runs.length);
        Arrays.sort(sorted, Comparator.comparingLong(m -> m.timeNano));
        return sorted[sorted.length / 2];
    }

    private static void writeResult(
            PrintWriter csv,
            String workload,
            String variant,
            String structure,
            int n,
            Metrics metrics) {
        double timeMs = metrics.timeNano / 1_000_000.0;
        csv.printf(
                "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                workload,
                variant,
                structure,
                n,
                timeMs,
                metrics.steps,
                metrics.moves,
                metrics.comparisons
        );
    }
}
