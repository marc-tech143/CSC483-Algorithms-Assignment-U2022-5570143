package com.csc483.assignment.search;

/**
 * Represents a product in the TechMart online electronics store.
 *
 * <p>This class implements {@link Comparable} so that arrays of Product
 * objects can be sorted by productId, which is required by
 * {@link SearchAlgorithms#binarySearchById(Product[], int)}.</p>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
public class Product implements Comparable<Product> {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Minimum valid product ID. */
    public static final int MIN_ID = 1;

    /** Maximum valid product ID used in random generation. */
    public static final int MAX_ID = 200_000;

    // -----------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------

    /** Unique identifier for the product. */
    private int productId;

    /** Human-readable product name. */
    private String productName;

    /** Category the product belongs to (e.g. "Laptop", "Phone"). */
    private String category;

    /** Retail price in Naira (NGN). */
    private double price;

    /** Number of units currently in stock. */
    private int stockQuantity;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Constructs a fully initialised Product.
     *
     * @param productId     unique identifier (must be &gt; 0)
     * @param productName   name of the product (must not be null)
     * @param category      product category (must not be null)
     * @param price         price in NGN (must be &gt;= 0)
     * @param stockQuantity units in stock (must be &gt;= 0)
     * @throws IllegalArgumentException if any argument violates its contract
     */
    public Product(int productId, String productName, String category,
                   double price, int stockQuantity) {
        if (productId <= 0) {
            throw new IllegalArgumentException("productId must be positive, got: " + productId);
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be null or blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category must not be null or blank");
        }
        if (price < 0) {
            throw new IllegalArgumentException("price must be >= 0, got: " + price);
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("stockQuantity must be >= 0, got: " + stockQuantity);
        }

        this.productId     = productId;
        this.productName   = productName;
        this.category      = category;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    // -----------------------------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------------------------

    /** @return the unique product identifier */
    public int getProductId()       { return productId; }

    /** @return the product name */
    public String getProductName()  { return productName; }

    /** @return the product category */
    public String getCategory()     { return category; }

    /** @return the retail price */
    public double getPrice()        { return price; }

    /** @return units currently in stock */
    public int getStockQuantity()   { return stockQuantity; }

    /**
     * Updates the product name.
     * @param productName new name (must not be null or blank)
     */
    public void setProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be null or blank");
        }
        this.productName = productName;
    }

    /**
     * Updates the category.
     * @param category new category (must not be null or blank)
     */
    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category must not be null or blank");
        }
        this.category = category;
    }

    /**
     * Updates the price.
     * @param price new price (must be >= 0)
     */
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("price must be >= 0");
        this.price = price;
    }

    /**
     * Updates the stock quantity.
     * @param stockQuantity new quantity (must be >= 0)
     */
    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) throw new IllegalArgumentException("stockQuantity must be >= 0");
        this.stockQuantity = stockQuantity;
    }

    // -----------------------------------------------------------------------
    // Comparable, equals, hashCode, toString
    // -----------------------------------------------------------------------

    /**
     * Compares products by productId (ascending).
     * Required for binary search on sorted arrays.
     */
    @Override
    public int compareTo(Product other) {
        return Integer.compare(this.productId, other.productId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product)) return false;
        Product other = (Product) obj;
        return this.productId == other.productId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%.2f, stock=%d}",
                productId, productName, category, price, stockQuantity);
    }
}
