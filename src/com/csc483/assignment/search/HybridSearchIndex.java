package com.csc483.assignment.search;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Hybrid search index that enables O(1) average-case name look-up and
 * O(log n) ID look-up without requiring a fully sorted array to be
 * rebuilt on every insertion.
 *
 * <h2>Design Rationale</h2>
 * <p>The main sorted array used by {@link SearchAlgorithms#binarySearchById}
 * requires O(n) time to insert a new element (shift phase).  For workloads
 * that insert products frequently, a supplementary index is desirable.</p>
 *
 * <p>This class maintains two auxiliary structures in parallel with the
 * primary array:</p>
 * <ol>
 *   <li><b>nameIndex</b> – a {@link HashMap} mapping lower-cased product
 *       name → Product, giving O(1) average name look-up.</li>
 *   <li><b>idIndex</b>   – a {@link TreeMap} mapping productId → Product,
 *       giving O(log n) ID look-up <em>without</em> needing a contiguous
 *       sorted array (useful when the array has not been fully sorted yet).</li>
 * </ol>
 *
 * <h2>Time-Complexity Summary</h2>
 * <pre>
 *  Operation          Best / Average   Worst
 *  ────────────────── ──────────────── ──────────
 *  addProduct         O(log n)         O(log n)
 *  searchByName       O(1)             O(n)  *
 *  searchById (tree)  O(log n)         O(log n)
 *
 *  * worst case arises from hash collision chains (extremely rare with
 *    Java's HashMap)
 * </pre>
 *
 * <h2>Space Complexity</h2>
 * <p>O(n) – both maps hold references to the same Product objects (no
 * copies), so the overhead is two pointers per product.</p>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
public class HybridSearchIndex {

    // -----------------------------------------------------------------------
    // Internal index structures
    // -----------------------------------------------------------------------

    /**
     * Maps lower-cased product name → Product for O(1) average name search.
     * Key collision (two products with the same name) retains the most
     * recently added product.
     */
    private final Map<String, Product> nameIndex;

    /**
     * Maps productId → Product for O(log n) ID look-up without needing a
     * fully sorted contiguous array.
     */
    private final TreeMap<Integer, Product> idIndex;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty HybridSearchIndex.
     */
    public HybridSearchIndex() {
        this.nameIndex = new HashMap<>();
        this.idIndex   = new TreeMap<>();
    }

    /**
     * Creates a HybridSearchIndex pre-populated from an existing product array.
     *
     * @param products array of products to index (nulls are skipped)
     */
    public HybridSearchIndex(Product[] products) {
        this();
        if (products != null) {
            for (Product p : products) {
                if (p != null) addToIndex(p);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Core operations
    // -----------------------------------------------------------------------

    /**
     * Adds a product to both indexes.
     *
     * <p>If the primary sorted array is also being maintained, callers should
     * call {@link SearchAlgorithms#addProduct(Product[], Product)} as well to
     * keep the array in sync.</p>
     *
     * <p><b>Complexity:</b> O(log n) – dominated by the TreeMap insertion.</p>
     *
     * @param product the product to add (must not be null)
     * @throws IllegalArgumentException if product is null
     */
    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("product must not be null");
        addToIndex(product);
    }

    /**
     * Searches for a product by exact name (case-insensitive).
     *
     * <p><b>Complexity:</b> O(1) average, O(n) worst (hash collisions).</p>
     *
     * @param name the product name to find (null returns null)
     * @return matching {@link Product} or {@code null} if not found
     */
    public Product searchByName(String name) {
        if (name == null) return null;
        return nameIndex.get(name.toLowerCase());
    }

    /**
     * Searches for a product by ID using the TreeMap index.
     *
     * <p><b>Complexity:</b> O(log n) for all cases.</p>
     *
     * @param productId the ID to look up
     * @return matching {@link Product} or {@code null} if not found
     */
    public Product searchById(int productId) {
        return idIndex.get(productId);
    }

    /**
     * Removes a product from both indexes.
     *
     * @param productId ID of the product to remove
     * @return the removed {@link Product}, or {@code null} if not present
     */
    public Product removeProduct(int productId) {
        Product removed = idIndex.remove(productId);
        if (removed != null) {
            nameIndex.remove(removed.getProductName().toLowerCase());
        }
        return removed;
    }

    /**
     * @return the number of products currently in the index
     */
    public int size() {
        return idIndex.size();
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void addToIndex(Product p) {
        nameIndex.put(p.getProductName().toLowerCase(), p);
        idIndex.put(p.getProductId(), p);
    }
}
