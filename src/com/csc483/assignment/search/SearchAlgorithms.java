package com.csc483.assignment.search;

/**
 * Provides sequential search, binary search, and name-based search
 * operations on an array of {@link Product} objects.
 *
 * <p>All methods are static and operate on a {@code Product[]} that is
 * supplied by the caller, keeping the algorithms independent of any
 * specific data-structure class.</p>
 *
 * <h2>Time-Complexity Summary</h2>
 * <pre>
 *  Method                  Best       Average    Worst
 *  ─────────────────────── ────────── ────────── ──────────
 *  sequentialSearchById    O(1)       O(n)       O(n)
 *  binarySearchById        O(1)       O(log n)   O(log n)
 *  searchByName            O(1)       O(n)       O(n)
 *  addProduct              O(1)       O(n)       O(n)
 * </pre>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
public final class SearchAlgorithms {

    // Prevent instantiation – utility class
    private SearchAlgorithms() { }

    // -----------------------------------------------------------------------
    // Comparison counter (package-private so benchmark can reset / read it)
    // -----------------------------------------------------------------------

    /** Running count of comparisons made in the last search call. */
    static long lastComparisonCount = 0;

    /** @return the number of key comparisons made during the most recent search */
    public static long getLastComparisonCount() {
        return lastComparisonCount;
    }

    // -----------------------------------------------------------------------
    // 1. Sequential Search by ID
    // -----------------------------------------------------------------------

    /**
     * Searches {@code products} linearly for a product whose
     * {@code productId} equals {@code targetId}.
     *
     * <p><b>Pre-condition:</b> none – array need not be sorted.</p>
     * <p><b>Post-condition:</b> returns the matching product or {@code null}.</p>
     *
     * <p><b>Complexity:</b>
     * <ul>
     *   <li>Best  – O(1) : target is at index 0</li>
     *   <li>Avg   – O(n) : target at position n/2 on average</li>
     *   <li>Worst – O(n) : target not present, all n elements checked</li>
     * </ul>
     *
     * @param products array of products (may be null or empty)
     * @param targetId the product ID to find
     * @return matching {@link Product}, or {@code null} if not found
     */
    public static Product sequentialSearchById(Product[] products, int targetId) {
        lastComparisonCount = 0;

        if (products == null) return null;

        for (int i = 0; i < products.length; i++) {
            lastComparisonCount++;
            if (products[i] != null && products[i].getProductId() == targetId) {
                return products[i];
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // 2. Binary Search by ID  (array must be sorted ascending by productId)
    // -----------------------------------------------------------------------

    /**
     * Searches a <b>sorted</b> {@code products} array for a product whose
     * {@code productId} equals {@code targetId} using the binary search
     * algorithm.
     *
     * <p><b>Pre-condition:</b> {@code products} is sorted in ascending order
     * of {@code productId}.  If this condition is violated the result is
     * undefined.</p>
     *
     * <p><b>Complexity:</b>
     * <ul>
     *   <li>Best  – O(1)     : target is the middle element on the first probe</li>
     *   <li>Avg   – O(log n) : ~log<sub>2</sub>(n) probes on average</li>
     *   <li>Worst – O(log n) : target absent, exhausts all log<sub>2</sub>(n) levels</li>
     * </ul>
     *
     * @param products sorted array of products (may be null or empty)
     * @param targetId the product ID to find
     * @return matching {@link Product}, or {@code null} if not found
     */
    public static Product binarySearchById(Product[] products, int targetId) {
        lastComparisonCount = 0;

        if (products == null || products.length == 0) return null;

        int low  = 0;
        int high = products.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;   // avoids integer overflow
            lastComparisonCount++;

            int midId = products[mid].getProductId();

            if (midId == targetId) {
                return products[mid];            // found
            } else if (midId < targetId) {
                low = mid + 1;                   // target is in right half
            } else {
                high = mid - 1;                  // target is in left half
            }
        }
        return null;                             // not found
    }

    // -----------------------------------------------------------------------
    // 3. Sequential Search by Name
    // -----------------------------------------------------------------------

    /**
     * Searches {@code products} linearly for a product whose
     * {@code productName} equals {@code targetName} (case-insensitive).
     *
     * <p>Binary search cannot be used here because the array is sorted by
     * {@code productId}, not by name.</p>
     *
     * <p><b>Complexity:</b> O(n) for all cases.</p>
     *
     * @param products  array of products (may be null or empty)
     * @param targetName the product name to find (null-safe)
     * @return matching {@link Product}, or {@code null} if not found
     */
    public static Product searchByName(Product[] products, String targetName) {
        lastComparisonCount = 0;

        if (products == null || targetName == null) return null;

        for (Product p : products) {
            lastComparisonCount++;
            if (p != null && p.getProductName().equalsIgnoreCase(targetName)) {
                return p;
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // 4. Sorted-Insert (addProduct)
    // -----------------------------------------------------------------------

    /**
     * Inserts {@code newProduct} into a <b>sorted</b> array of products,
     * maintaining ascending order by {@code productId}.
     *
     * <p>This method uses <em>insertion into a sorted array</em>: it finds the
     * correct position with binary search (O(log n)) and then shifts all
     * subsequent elements right (O(n)), giving an overall O(n) insertion cost.</p>
     *
     * <p><b>Pre-condition:</b>
     * <ol>
     *   <li>{@code products} is sorted ascending by {@code productId}.</li>
     *   <li>The last element of {@code products} is {@code null} (a vacant slot).</li>
     * </ol>
     *
     * <p><b>Post-condition:</b> {@code products} contains {@code newProduct} in its
     * correct sorted position and the invariant is preserved.</p>
     *
     * <p><b>Complexity:</b> O(n) – dominated by the element-shift phase.</p>
     *
     * @param products   sorted array with one free slot at the end ({@code null})
     * @param newProduct product to insert (must not be null)
     * @throws IllegalArgumentException if {@code products} has no free slot
     */
    public static void addProduct(Product[] products, Product newProduct) {
        if (products == null || newProduct == null) {
            throw new IllegalArgumentException("products and newProduct must not be null");
        }

        // Verify the last slot is free
        if (products[products.length - 1] != null) {
            throw new IllegalArgumentException(
                    "No free slot available at end of array; resize before calling addProduct");
        }

        // Count how many real products are in the array
        int size = products.length - 1; // last slot is null

        // Binary search for the insertion position
        int low = 0, high = size - 1, insertPos = size;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (products[mid].getProductId() < newProduct.getProductId()) {
                low = mid + 1;
            } else {
                insertPos = mid;
                high = mid - 1;
            }
        }

        // Shift elements right to make room
        for (int i = size; i > insertPos; i--) {
            products[i] = products[i - 1];
        }

        // Place the new product
        products[insertPos] = newProduct;
    }
}
