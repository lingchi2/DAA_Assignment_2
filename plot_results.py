from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd

RESULTS_FILE = Path("results/results.csv")
PLOTS_DIR = Path("results/plots")
PLOTS_DIR.mkdir(parents=True, exist_ok=True)

if not RESULTS_FILE.exists():
    raise SystemExit(
        "Error: results/results.csv not found. Run the Java benchmark first."
    )

df = pd.read_csv(RESULTS_FILE)
required_columns = {
    "workload",
    "variant",
    "structure",
    "n",
    "time_ms",
    "steps",
    "moves",
    "comparisons",
}
missing = required_columns - set(df.columns)
if missing:
    raise SystemExit(f"Error: missing CSV columns: {sorted(missing)}")

markers = {
    "DynamicArray": "o",
    "MyLinkedList": "s",
    "MinHeap": "^",
}
colors = {
    "DynamicArray": "tab:blue",
    "MyLinkedList": "tab:red",
    "MinHeap": "tab:green",
}
line_styles = {
    "head": "--",
    "middle": ":",
    "-": "-",
}


def label_for(structure, variant):
    return structure if variant == "-" else f"{structure} ({variant})"


def create_time_plot(subset, workload):
    plt.figure(figsize=(10, 6))

    for (structure, variant), group in subset.groupby(["structure", "variant"]):
        group = group.sort_values("n")
        plt.plot(
            group["n"],
            group["time_ms"],
            marker=markers.get(structure, "o"),
            color=colors.get(structure),
            linestyle=line_styles.get(variant, "-"),
            label=label_for(structure, variant),
        )

    plt.xscale("log")
    if (subset["time_ms"] > 0).all():
        plt.yscale("log")

    plt.title(f"[{workload}] Time vs Input Size (n)", fontsize=14, fontweight="bold")
    plt.xlabel("Input Size (n)", fontsize=12)
    plt.ylabel("Time (ms)", fontsize=12)
    plt.grid(True, which="both", linestyle="--", alpha=0.7)
    plt.legend(title="Structure & Variant", bbox_to_anchor=(1.05, 1), loc="upper left")
    plt.tight_layout()
    path = PLOTS_DIR / f"{workload}_time_vs_n.png"
    plt.savefig(path, dpi=300)
    plt.close()


def create_operations_plot(subset, workload):
    plt.figure(figsize=(12, 7))

    metric_styles = {
        "steps": "-",
        "moves": "--",
        "comparisons": ":",
    }

    plotted = False
    for (structure, variant), group in subset.groupby(["structure", "variant"]):
        group = group.sort_values("n")
        base_label = label_for(structure, variant)
        for metric in ("steps", "moves", "comparisons"):
            values = group[metric]
            if values.eq(0).all():
                continue
            plt.plot(
                group["n"],
                values,
                marker=markers.get(structure, "o"),
                color=colors.get(structure),
                linestyle=metric_styles[metric],
                label=f"{base_label} — {metric}",
            )
            plotted = True

    plt.xscale("log")
    positive_values = pd.concat(
        [subset["steps"], subset["moves"], subset["comparisons"]]
    )
    non_zero = positive_values[positive_values > 0]
    if plotted and not non_zero.empty:
        plt.yscale("log")

    plt.title(
        f"[{workload}] Steps / Moves / Comparisons vs Input Size (n)",
        fontsize=14,
        fontweight="bold",
    )
    plt.xlabel("Input Size (n)", fontsize=12)
    plt.ylabel("Operation Count", fontsize=12)
    plt.grid(True, which="both", linestyle="--", alpha=0.7)
    plt.legend(title="Structure / Variant / Metric", bbox_to_anchor=(1.05, 1), loc="upper left")
    plt.tight_layout()
    path = PLOTS_DIR / f"{workload}_operations_vs_n.png"
    plt.savefig(path, dpi=300)
    plt.close()


for workload in df["workload"].drop_duplicates():
    subset = df[df["workload"] == workload]
    create_time_plot(subset, workload)
    create_operations_plot(subset, workload)
    print(f"Created plots for {workload}")

print(f"All plots saved to {PLOTS_DIR}")
