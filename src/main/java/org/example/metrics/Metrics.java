package org.example.metrics;

public final class Metrics {
    public long steps;
    public long moves;
    public long comparisons;
    public long timeNano;

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
        timeNano = 0;
    }

    public Metrics copy() {
        Metrics copy = new Metrics();
        copy.steps = steps;
        copy.moves = moves;
        copy.comparisons = comparisons;
        copy.timeNano = timeNano;
        return copy;
    }
}
