import os
import pandas as pd
import matplotlib.pyplot as plt


INPUT_FILE = "results/results.csv"
OUTPUT_DIR = "results/plots"

os.makedirs(OUTPUT_DIR, exist_ok=True)


try:
    df = pd.read_csv(INPUT_FILE)
except FileNotFoundError:
    print(f"Error: {INPUT_FILE} not found.")
    exit(1)


required_columns = [
    "workload",
    "variant",
    "structure",
    "n",
    "time_ms",
    "steps",
    "moves",
    "comparisons"
]

missing = [column for column in required_columns if column not in df.columns]

if missing:
    print("Error: missing columns:")
    for column in missing:
        print(f"  - {column}")
    exit(1)


markers = {
    "DynamicArray": "o",
    "MyLinkedList": "s",
    "MinHeap": "^"
}

line_styles = {
    "head": "--",
    "middle": ":",
    "-": "-"
}


def create_plot(
        subset_df,
        metric,
        title,
        ylabel,
        filename
):
    plt.figure(figsize=(10, 6))

    groups = subset_df.groupby(
        ["structure", "variant"],
        sort=False
    )

    for (structure, variant), group in groups:

        group = group.sort_values("n")

        if variant == "-":
            label = structure
        else:
            label = f"{structure} ({variant})"

        plt.plot(
            group["n"],
            group[metric],
            marker=markers.get(structure, "o"),
            linestyle=line_styles.get(variant, "-"),
            linewidth=2,
            markersize=6,
            label=label
        )

    plt.xscale("log")

    # Operation counters can contain zero.
    # Log scale would make zero impossible to display.
    if metric != "time_ms":
        positive_values = subset_df[metric][subset_df[metric] > 0]

        if len(positive_values) > 0:
            plt.yscale("log")

    else:
        positive_values = subset_df["time_ms"][subset_df["time_ms"] > 0]

        if len(positive_values) > 0:
            plt.yscale("log")

    plt.title(title, fontsize=14, fontweight="bold")
    plt.xlabel("Array Size (n)", fontsize=12)
    plt.ylabel(ylabel, fontsize=12)

    plt.grid(
        True,
        which="both",
        linestyle="--",
        alpha=0.5
    )

    plt.legend(
        title="Structure & Variant"
    )

    plt.tight_layout()

    filepath = os.path.join(
        OUTPUT_DIR,
        filename
    )

    plt.savefig(
        filepath,
        dpi=300,
        bbox_inches="tight"
    )

    plt.close()

    print(f"Created: {filepath}")


workloads = df["workload"].drop_duplicates()


for workload in workloads:

    subset = df[df["workload"] == workload]

    create_plot(
        subset_df=subset,
        metric="time_ms",
        title=f"{workload} — Time vs n",
        ylabel="Time (ms)",
        filename=f"{workload}_time_vs_n.png"
    )

    create_plot(
        subset_df=subset,
        metric="steps",
        title=f"{workload} — Steps vs n",
        ylabel="Steps",
        filename=f"{workload}_steps_vs_n.png"
    )

    create_plot(
        subset_df=subset,
        metric="moves",
        title=f"{workload} — Moves vs n",
        ylabel="Moves",
        filename=f"{workload}_moves_vs_n.png"
    )

    create_plot(
        subset_df=subset,
        metric="comparisons",
        title=f"{workload} — Comparisons vs n",
        ylabel="Comparisons",
        filename=f"{workload}_comparisons_vs_n.png"
    )


print()
print("All plots generated successfully.")