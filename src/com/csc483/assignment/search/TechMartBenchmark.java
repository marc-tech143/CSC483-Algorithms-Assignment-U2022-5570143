package com.csc483.assignment.search;

import java.util.Arrays;
import java.util.Random;

/**
 * Benchmark driver for TechMart search performance analysis.
 *
 * <p>This program:</p>
 * <ol>
 *   <li>Generates a dataset of 100,000 random {@link Product} objects.</li>
 *   <li>Measures and compares execution times of sequential search and
 *       binary search for best, average, and worst cases.</li>
 *   <li>Demonstrates the hybrid search index for name-based look-up.</li>
 *   <li>Outputs results in a formatted table matching the assignment specification.</li>
 * </ol>
 *
 * <p><b>How to compile and run:</b></p>
 * <pre>
 *   javac -d out src/com/csc483/assignment/search/*.java
 *   java  -cp out com.csc483.assignment.search.TechMartBenchmark
 * </pre>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
public class TechMartBenchmark {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    private static final int    DATASET_SIZE   = 100_000;
    private static final int    WARMUP_RUNS    = 3;
    private static final int    TIMED_RUNS     = 5;
    private static final Random RNG            = new Random(42L); // fixed seed for reproducibility

    /** Sample product names used during generation. */
    private static final String[] NAMES = {
        "Laptop Pro", "UltraPhone", "SmartTab", "Gaming PC", "Wireless Headset",
        "4K Monitor", "Mechanical Keyboard", "Optical Mouse", "USB-C Hub", "NVMe SSD",
        "Smart Watch", "Bluetooth Speaker", "Action Camera", "Drone X", "VR Headset",
        "Router Pro", "Portable Charger", "LED Strip", "Smart Bulb", "Web Cam HD"
    };

    /** Sample categories used during generation. */
    private static final String[] CATEGORIES = {
        "Laptop", "Phone", "Tablet", "Desktop", "Audio",
        "Display", "Keyboard", "Mouse", "Accessories", "Storage"
    };

    // -----------------------------------------------------------------------
    // Main method
    // -----------------------------------------------------------------------

    public static void main(String[] args) {

        System.out.println("================================================================");
        System.out.println("  TECHMART SEARCH PERFORMANCE ANALYSIS (n = 100,000 products)  ");
        System.out.println("================================================================");
        System.out.println();

        // ── 1. Generate dataset ─────────────────────────────────────────────
        System.out.println("Generating " + DATASET_SIZE + " random products...");
        long[] usedIds = generateUniqueIds(DATASET_SIZE);
        Product[] products = generateProducts(usedIds);

        // ── 2. Sort a copy for binary search ────────────────────────────────
        Product[] sortedProducts = Arrays.copyOf(products, products.length);
        Arrays.sort(sortedProducts);   // uses Product.compareTo() → sorts by ID
        System.out.println("Dataset generated and sorted.\n");

        // ── 3. Choose test targets ───────────────────────────────────────────
        int bestCaseId   = sortedProducts[0].getProductId();                      // first element
        int avgCaseId    = sortedProducts[DATASET_SIZE / 2].getProductId();       // middle element
        int worstCaseId  = 999_999;                                                // guaranteed absent

        // ── 4. Sequential Search Benchmark ──────────────────────────────────
        System.out.println("── SEQUENTIAL SEARCH ──────────────────────────────────────────");
        double seqBest    = benchmarkSearch(products, bestCaseId,  false, "best");
        double seqAverage = benchmarkSearch(products, avgCaseId,   false, "average");
        double seqWorst   = benchmarkSearch(products, worstCaseId, false, "worst");

        printSearchResult("Sequential", "Best Case  (ID at position 0)", seqBest,
                SearchAlgorithms.sequentialSearchById(products, bestCaseId));
        printSearchResult("Sequential", "Avg Case   (random ID)        ", seqAverage,
                SearchAlgorithms.sequentialSearchById(products, avgCaseId));
        printSearchResult("Sequential", "Worst Case (ID not found)     ", seqWorst, null);
        System.out.println();

        // ── 5. Binary Search Benchmark ──────────────────────────────────────
        System.out.println("── BINARY SEARCH ──────────────────────────────────────────────");
        int midId       = sortedProducts[DATASET_SIZE / 2].getProductId();  // found at first probe
        double binBest  = benchmarkSearch(sortedProducts, midId,        true, "best");
        double binAvg   = benchmarkSearch(sortedProducts, avgCaseId,    true, "average");
        double binWorst = benchmarkSearch(sortedProducts, worstCaseId,  true, "worst");

        printSearchResult("Binary", "Best Case  (ID at middle)     ", binBest,
                SearchAlgorithms.binarySearchById(sortedProducts, midId));
        printSearchResult("Binary", "Avg Case   (random ID)        ", binAvg,
                SearchAlgorithms.binarySearchById(sortedProducts, avgCaseId));
        printSearchResult("Binary", "Worst Case (ID not found)     ", binWorst, null);
        System.out.println();

        // ── 6. Performance ratio ────────────────────────────────────────────
        double ratio = seqAverage / binAvg;
        System.out.printf("PERFORMANCE IMPROVEMENT: Binary search is ~%.0fx faster on average%n%n", ratio);

        // ── 7. Theoretical comparison count analysis ─────────────────────────
        System.out.println("── THEORETICAL COMPARISON ANALYSIS ────────────────────────────");
        System.out.printf("  n = %,d%n", DATASET_SIZE);
        System.out.printf("  Sequential Avg Comparisons : %,d   (n/2)%n", DATASET_SIZE / 2);
        int binaryComparisons = (int) Math.ceil(Math.log(DATASET_SIZE) / Math.log(2));
        System.out.printf("  Binary     Avg Comparisons : %,d   (log2(n))%n", binaryComparisons);
        System.out.printf("  Theoretical Speedup        : ~%.0fx%n",
                (double)(DATASET_SIZE / 2) / binaryComparisons);
        System.out.println();

        // ── 8. Hybrid Name Search Benchmark ─────────────────────────────────
        System.out.println("── HYBRID NAME SEARCH ─────────────────────────────────────────");
        HybridSearchIndex hybridIndex = new HybridSearchIndex(sortedProducts);

        // Benchmark name search
        String sampleName = products[RNG.nextInt(DATASET_SIZE)].getProductName();
        double nameSearchTime = benchmarkHybridNameSearch(hybridIndex, sampleName);
        System.out.printf("  Average name search time : %.3f ms%n", nameSearchTime);

        // Benchmark addProduct (insert into index + array)
        double insertTime = benchmarkHybridInsert(sortedProducts, hybridIndex);
        System.out.printf("  Average insert time      : %.3f ms%n", insertTime);
        System.out.println();

        // ── 9. Comparison count table ────────────────────────────────────────
        printComparisonTable(products, sortedProducts, bestCaseId, avgCaseId, worstCaseId);

        System.out.println("================================================================");
        System.out.println("  END OF TECHMART SEARCH PERFORMANCE ANALYSIS                  ");
        System.out.println("================================================================");
    }

    // -----------------------------------------------------------------------
    // Benchmarking helpers
    // -----------------------------------------------------------------------

    /**
     * Runs a search benchmark, warming up first then timing {@link #TIMED_RUNS} runs.
     *
     * @param products   array to search
     * @param targetId   ID to search for
     * @param binary     true → binary search, false → sequential search
     * @param caseLabel  label for console progress output
     * @return average elapsed time in milliseconds
     */
    private static double benchmarkSearch(Product[] products, int targetId,
                                          boolean binary, String caseLabel) {
        // Warm-up (discarded)
        for (int i = 0; i < WARMUP_RUNS; i++) {
            if (binary) SearchAlgorithms.binarySearchById(products, targetId);
            else        SearchAlgorithms.sequentialSearchById(products, targetId);
        }

        // Timed runs
        long totalNanos = 0;
        for (int i = 0; i < TIMED_RUNS; i++) {
            long start = System.nanoTime();
            if (binary) SearchAlgorithms.binarySearchById(products, targetId);
            else        SearchAlgorithms.sequentialSearchById(products, targetId);
            totalNanos += System.nanoTime() - start;
        }
        return (totalNanos / TIMED_RUNS) / 1_000_000.0;  // nanoseconds → ms
    }

    /**
     * Benchmarks the hybrid index name search.
     */
    private static double benchmarkHybridNameSearch(HybridSearchIndex index, String name) {
        for (int i = 0; i < WARMUP_RUNS; i++) index.searchByName(name);

        long totalNanos = 0;
        for (int i = 0; i < TIMED_RUNS; i++) {
            long start = System.nanoTime();
            index.searchByName(name);
            totalNanos += System.nanoTime() - start;
        }
        return (totalNanos / TIMED_RUNS) / 1_000_000.0;
    }

    /**
     * Benchmarks addProduct: inserts a new product into a copy of the sorted array
     * and into the hybrid index.
     */
    private static double benchmarkHybridInsert(Product[] sortedProducts,
                                                 HybridSearchIndex index) {
        long totalNanos = 0;
        for (int i = 0; i < TIMED_RUNS; i++) {
            // Create a fresh array with one extra slot for the insert
            Product[] copy = new Product[sortedProducts.length + 1];
            System.arraycopy(sortedProducts, 0, copy, 0, sortedProducts.length);

            int newId = 200_001 + i;          // IDs guaranteed not to collide
            Product newProd = new Product(newId, "New Product " + i, "Electronics",
                                          999.99, 10);

            long start = System.nanoTime();
            SearchAlgorithms.addProduct(copy, newProd);
            index.addProduct(newProd);
            totalNanos += System.nanoTime() - start;

            // Remove from index to avoid accumulation across runs
            index.removeProduct(newId);
        }
        return (totalNanos / TIMED_RUNS) / 1_000_000.0;
    }

    // -----------------------------------------------------------------------
    // Formatting helpers
    // -----------------------------------------------------------------------

    private static void printSearchResult(String algo, String caseLabel,
                                          double timeMs, Product found) {
        System.out.printf("  %-36s : %8.3f ms  [%s]%n",
                caseLabel, timeMs,
                (found != null ? "FOUND id=" + found.getProductId() : "NOT FOUND"));
    }

    /**
     * Prints a table of comparison counts for both search algorithms.
     */
    private static void printComparisonTable(Product[] products, Product[] sorted,
                                              int bestId, int avgId, int worstId) {
        System.out.println("── COMPARISON COUNTS ───────────────────────────────────────────");
        System.out.printf("  %-20s %15s %15s%n", "Case", "Sequential", "Binary");
        System.out.println("  " + "─".repeat(52));

        // Best
        SearchAlgorithms.sequentialSearchById(products, bestId);
        long seqBestCmp = SearchAlgorithms.getLastComparisonCount();
        SearchAlgorithms.binarySearchById(sorted, sorted[sorted.length / 2].getProductId());
        long binBestCmp = SearchAlgorithms.getLastComparisonCount();
        System.out.printf("  %-20s %,15d %,15d%n", "Best Case", seqBestCmp, binBestCmp);

        // Average
        SearchAlgorithms.sequentialSearchById(products, avgId);
        long seqAvgCmp = SearchAlgorithms.getLastComparisonCount();
        SearchAlgorithms.binarySearchById(sorted, avgId);
        long binAvgCmp = SearchAlgorithms.getLastComparisonCount();
        System.out.printf("  %-20s %,15d %,15d%n", "Average Case", seqAvgCmp, binAvgCmp);

        // Worst
        SearchAlgorithms.sequentialSearchById(products, worstId);
        long seqWorstCmp = SearchAlgorithms.getLastComparisonCount();
        SearchAlgorithms.binarySearchById(sorted, worstId);
        long binWorstCmp = SearchAlgorithms.getLastComparisonCount();
        System.out.printf("  %-20s %,15d %,15d%n", "Worst Case", seqWorstCmp, binWorstCmp);
        System.out.println();
    }

    // -----------------------------------------------------------------------
    // Dataset generation
    // -----------------------------------------------------------------------

    /**
     * Generates {@code count} unique integers in the range [1, 200_000].
     */
    private static long[] generateUniqueIds(int count) {
        boolean[] used = new boolean[Product.MAX_ID + 1];
        long[] ids = new long[count];
        int generated = 0;
        while (generated < count) {
            int id = 1 + RNG.nextInt(Product.MAX_ID);
            if (!used[id]) {
                used[id] = true;
                ids[generated++] = id;
            }
        }
        return ids;
    }

    /**
     * Builds a Product array from a list of unique IDs.
     */
    private static Product[] generateProducts(long[] ids) {
        Product[] arr = new Product[ids.length];
        for (int i = 0; i < ids.length; i++) {
            String name     = NAMES[i % NAMES.length] + " " + ids[i];
            String category = CATEGORIES[i % CATEGORIES.length];
            double price    = 1_000 + RNG.nextInt(500_000) / 100.0;
            int    stock    = RNG.nextInt(500);
            arr[i] = new Product((int) ids[i], name, category, price, stock);
        }
        return arr;
    }
}
