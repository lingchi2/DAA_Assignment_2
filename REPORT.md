# Assignment 2 — Data Structures Report

## 1. Overview

This project implements three data structures from scratch:

* `DynamicArray`
* `MyLinkedList`
* `MinHeap`

The benchmark uses four input sizes:

`100`, `1,000`, `10,000`, `100,000`.

All random data is generated with `Random(42)` so the benchmark can be reproduced. Each case has one warm-up run and five measured runs. The median of the five measured runs is stored in `results.csv`.

---

## 2. Complexity Analysis

| Structure    | Operation      | Best |        Average |    Worst | Auxiliary Space |
| ------------ | -------------- | ---: | -------------: | -------: | --------------: |
| DynamicArray | `add(x)`       | Θ(1) | Θ(1) amortized |     O(n) |            O(n) |
| DynamicArray | `add(i,x)`     | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| DynamicArray | `remove(i)`    | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| DynamicArray | `get(i)`       | Θ(1) |           Θ(1) |     Θ(1) |            O(n) |
| DynamicArray | `contains(x)`  | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| MyLinkedList | `add(x)`       | Θ(1) |           Θ(1) |     Θ(1) |            O(n) |
| MyLinkedList | `add(i,x)`     | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| MyLinkedList | `remove(i)`    | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| MyLinkedList | `get(i)`       | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| MyLinkedList | `contains(x)`  | Θ(1) |           Θ(n) |     Θ(n) |            O(n) |
| MinHeap      | `insert(x)`    | Θ(1) |       O(log n) | O(log n) |            O(n) |
| MinHeap      | `peekMin()`    | Θ(1) |           Θ(1) |     Θ(1) |            O(n) |
| MinHeap      | `extractMin()` | Θ(1) |       O(log n) | O(log n) |            O(n) |

DynamicArray provides constant-time indexed access because elements are stored in consecutive array positions. MyLinkedList must traverse nodes to reach an arbitrary index. MinHeap keeps the minimum element at the root, so `peekMin()` is constant time while insertion and extraction may require moving through the height of the heap.

---

## 3. Loop Invariant 1 — DynamicArray `remove(index)`

The removal operation shifts all elements after the removed index one position to the left:

```java
for (int i = index; i < size - 1; i++) {
    data[i] = data[i + 1];
}
```

**Invariant:** before every iteration, all positions before `i` in the affected range already contain the correct shifted values.

**Initialization:** before the first iteration, no elements have been shifted, so the invariant is true.

**Maintenance:** during each iteration, `data[i]` receives the value from `data[i + 1]`. Therefore the next position becomes correct as well.

**Termination:** when the loop ends, every element after the removed position has been shifted left by one. The size is then decreased by one.

Therefore, `remove(index)` produces the correct resulting array.

---

## 4. Loop Invariant 2 — MinHeap `bubbleDown`

After removing the minimum, the last element is moved to the root. `bubbleDown` restores the heap property.

**Invariant:** before every iteration, the heap property is valid everywhere except possibly at the current node.

**Initialization:** after replacing the root, all other subtrees are still valid heaps. Only the root may violate the property.

**Maintenance:** the algorithm compares the current node with its children and swaps it with the smaller child when necessary. The possible violation is moved lower in the tree.

**Termination:** the loop stops when the current node is smaller than both children or when it reaches a leaf.

At this point the complete structure satisfies the min-heap property.

---

## 5. Benchmark Design

### W1 — Random Access

For every `n`, both structures receive the same data. Then 10,000 valid random indexes are generated and used with `get(index)`.

The main difference is expected to be:

* DynamicArray — direct array access;
* MyLinkedList — node traversal.

![Random Access Time](results/plots/RandomAccess_time_vs_n.png)

![Random Access Steps](results/plots/RandomAccess_steps_vs_n.png)

![Random Access Moves](results/plots/RandomAccess_moves_vs_n.png)

![Random Access Comparisons](results/plots/RandomAccess_comparisons_vs_n.png)

---

### W2 — Search

Each case performs 1,000 `contains(x)` operations:

* 500 values are present in the structure;
* 500 values are guaranteed to be absent.

Both structures use the same data and the same queries.

![Search Time](results/plots/Search_time_vs_n.png)

![Search Steps](results/plots/Search_steps_vs_n.png)

![Search Moves](results/plots/Search_moves_vs_n.png)

![Search Comparisons](results/plots/Search_comparisons_vs_n.png)

---

### W3 — Insert & Remove

Two variants are tested:

* `head` — index `0`;
* `middle` — index `n / 2`.

Each variant performs 1,000 insertions followed by 1,000 removals.

![Insert Remove Time](results/plots/InsertRemove_time_vs_n.png)

![Insert Remove Steps](results/plots/InsertRemove_steps_vs_n.png)

![Insert Remove Moves](results/plots/InsertRemove_moves_vs_n.png)

![Insert Remove Comparisons](results/plots/InsertRemove_comparisons_vs_n.png)

---

### W4 — Priority Processing

For every `n`:

1. `n` values are inserted into `MinHeap`.
2. `extractMin()` is called `n` times.
3. Every returned value is checked against the previous value.
4. The output must be in non-decreasing order.

![Priority Processing Time](results/plots/PriorityProcessing_time_vs_n.png)

![Priority Processing Steps](results/plots/PriorityProcessing_steps_vs_n.png)

![Priority Processing Moves](results/plots/PriorityProcessing_moves_vs_n.png)

![Priority Processing Comparisons](results/plots/PriorityProcessing_comparisons_vs_n.png)

---

## 6. Metrics

The benchmark records three operation counters.

* **steps** — array-cell reads or node traversal steps;
* **moves** — shifted/copied array elements or pointer updates;
* **comparisons** — comparisons between data values.

The counters are reset immediately before the timed workload, so setup operations are not included in the measured workload.

---

## 7. Cache Locality and Pointer Chasing

DynamicArray stores its elements next to each other in memory. This gives better cache locality, especially when accessing nearby elements.

MyLinkedList stores elements in separate nodes. A traversal follows pointers from one node to another, which can cause more cache misses and additional memory overhead.

This is why two structures with similar theoretical complexity can still have noticeably different execution times.

For frequent indexed access, DynamicArray avoids traversal. MyLinkedList is more useful when elements can be inserted or removed through already-known nodes without shifting an entire array.

MinHeap is designed for a different workload: repeatedly obtaining the smallest element. Its minimum is available at the root, while `extractMin()` restores the heap using `bubbleDown`.

---

## 8. Results

The benchmark results are stored in:

```text
results/results.csv
```

The graphs show how execution time and operation counts change as `n` increases.

The main expected trends are:

* DynamicArray `get()` remains approximately constant in operation count.
* MyLinkedList `get()` requires traversal.
* Search becomes more expensive as the number of elements increases.
* Insert/remove operations in the middle require more work than operations at the head.
* MinHeap extraction grows logarithmically per operation.

Actual execution time depends on the CPU, JVM, operating system and current system load.

---

## 9. Testing

JUnit 5 tests cover:

* random operations compared with an expected list;
* empty and one-element structures;
* duplicate values;
* first and last indexes;
* invalid indexes;
* required exceptions;
* DynamicArray resizing;
* MyLinkedList links and size;
* MinHeap property after insertions;
* MinHeap property after extractions;
* non-decreasing `extractMin()` output.

The project can be tested with:

```bash
mvn test
```

The benchmark can be run with:

```bash
mvn -q compile exec:java
```

and the plots can be generated with:

```bash
python3 plot_results.py
```
