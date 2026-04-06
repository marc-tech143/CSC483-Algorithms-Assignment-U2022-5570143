package com.csc483.assignment.test;

import com.csc483.assignment.search.HybridSearchIndex;
import com.csc483.assignment.search.Product;
import com.csc483.assignment.search.SearchAlgorithms;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for {@link SearchAlgorithms} and {@link HybridSearchIndex}.
 *
 * <p>Tests cover:</p>
 * <ul>
 *   <li>Sequential search – all cases including null / empty input</li>
 *   <li>Binary search – all cases including null / empty input</li>
 *   <li>Name search – case-insensitivity</li>
 *   <li>addProduct – insertion maintains sorted order</li>
 *   <li>HybridSearchIndex – add, search by name/id, remove</li>
 * </ul>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
@DisplayName("Product Search Algorithm Tests")
class ProductSearchTest {

    // -----------------------------------------------------------------------
    // Fixtures
    // -----------------------------------------------------------------------

    private Product[] sortedArray;
    private Product[] unsortedArray;

    @BeforeEach
    void setUp() {
        // Sorted array (by productId)
        sortedArray = new Product[] {
            new Product(10, "Alpha Laptop",   "Laptop",  150_000.00, 5),
            new Product(20, "Beta Phone",     "Phone",    80_000.00, 12),
            new Product(30, "Gamma Tab",      "Tablet",  120_000.00, 8),
            new Product(40, "Delta Monitor",  "Display", 200_000.00, 3),
            new Product(50, "Epsilon Watch",  "Wearable", 45_000.00, 20)
        };

        // Unsorted array (different order)
        unsortedArray = new Product[] {
            new Product(30, "Gamma Tab",      "Tablet",  120_000.00, 8),
            new Product(10, "Alpha Laptop",   "Laptop",  150_000.00, 5),
            new Product(50, "Epsilon Watch",  "Wearable", 45_000.00, 20),
            new Product(20, "Beta Phone",     "Phone",    80_000.00, 12),
            new Product(40, "Delta Monitor",  "Display", 200_000.00, 3)
        };
    }

    // -----------------------------------------------------------------------
    // Product class tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Product Class")
    class ProductClassTests {

        @Test
        @DisplayName("Valid product creation")
        void testValidCreation() {
            Product p = new Product(1, "Test Product", "Test", 100.0, 10);
            assertEquals(1,              p.getProductId());
            assertEquals("Test Product", p.getProductName());
            assertEquals("Test",         p.getCategory());
            assertEquals(100.0,          p.getPrice(), 0.001);
            assertEquals(10,             p.getStockQuantity());
        }

        @Test
        @DisplayName("Invalid productId throws exception")
        void testInvalidId() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(0, "Name", "Cat", 10.0, 1));
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(-1, "Name", "Cat", 10.0, 1));
        }

        @Test
        @DisplayName("Null productName throws exception")
        void testNullName() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(1, null, "Cat", 10.0, 1));
        }

        @Test
        @DisplayName("Negative price throws exception")
        void testNegativePrice() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Product(1, "Name", "Cat", -1.0, 1));
        }

        @Test
        @DisplayName("compareTo sorts by productId ascending")
        void testCompareTo() {
            Product[] arr = Arrays.copyOf(unsortedArray, unsortedArray.length);
            Arrays.sort(arr);
            for (int i = 1; i < arr.length; i++) {
                assertTrue(arr[i - 1].getProductId() < arr[i].getProductId());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Sequential Search tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Sequential Search by ID")
    class SequentialSearchTests {

        @Test
        @DisplayName("Finds existing product (best case – first element)")
        void testBestCase() {
            Product found = SearchAlgorithms.sequentialSearchById(unsortedArray, 30);
            assertNotNull(found);
            assertEquals(30, found.getProductId());
            assertEquals(1,  SearchAlgorithms.getLastComparisonCount()); // only 1 comparison
        }

        @Test
        @DisplayName("Finds existing product in the middle")
        void testMiddleElement() {
            Product found = SearchAlgorithms.sequentialSearchById(unsortedArray, 50);
            assertNotNull(found);
            assertEquals(50, found.getProductId());
        }

        @Test
        @DisplayName("Returns null for absent ID (worst case)")
        void testWorstCase() {
            Product found = SearchAlgorithms.sequentialSearchById(unsortedArray, 999);
            assertNull(found);
            assertEquals(unsortedArray.length, SearchAlgorithms.getLastComparisonCount());
        }

        @Test
        @DisplayName("Returns null for null array")
        void testNullArray() {
            assertNull(SearchAlgorithms.sequentialSearchById(null, 10));
        }

        @Test
        @DisplayName("Returns null for empty array")
        void testEmptyArray() {
            assertNull(SearchAlgorithms.sequentialSearchById(new Product[0], 10));
        }

        @ParameterizedTest(name = "ID={0}")
        @ValueSource(ints = {10, 20, 30, 40, 50})
        @DisplayName("Finds all products in unsorted array")
        void testAllExistingIds(int id) {
            Product p = SearchAlgorithms.sequentialSearchById(unsortedArray, id);
            assertNotNull(p, "Expected to find ID " + id);
            assertEquals(id, p.getProductId());
        }
    }

    // -----------------------------------------------------------------------
    // Binary Search tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Binary Search by ID")
    class BinarySearchTests {

        @Test
        @DisplayName("Finds product at middle (best case)")
        void testBestCase() {
            // Middle element = productId 30
            Product found = SearchAlgorithms.binarySearchById(sortedArray, 30);
            assertNotNull(found);
            assertEquals(30, found.getProductId());
            assertEquals(1,  SearchAlgorithms.getLastComparisonCount());
        }

        @Test
        @DisplayName("Finds first element")
        void testFirstElement() {
            Product found = SearchAlgorithms.binarySearchById(sortedArray, 10);
            assertNotNull(found);
            assertEquals(10, found.getProductId());
        }

        @Test
        @DisplayName("Finds last element")
        void testLastElement() {
            Product found = SearchAlgorithms.binarySearchById(sortedArray, 50);
            assertNotNull(found);
            assertEquals(50, found.getProductId());
        }

        @Test
        @DisplayName("Returns null for absent ID (worst case)")
        void testAbsentId() {
            assertNull(SearchAlgorithms.binarySearchById(sortedArray, 999));
        }

        @Test
        @DisplayName("Returns null for null array")
        void testNullArray() {
            assertNull(SearchAlgorithms.binarySearchById(null, 10));
        }

        @Test
        @DisplayName("Returns null for empty array")
        void testEmptyArray() {
            assertNull(SearchAlgorithms.binarySearchById(new Product[0], 10));
        }

        @ParameterizedTest(name = "ID={0}")
        @ValueSource(ints = {10, 20, 30, 40, 50})
        @DisplayName("Finds all products in sorted array")
        void testAllExistingIds(int id) {
            Product p = SearchAlgorithms.binarySearchById(sortedArray, id);
            assertNotNull(p, "Expected to find ID " + id);
            assertEquals(id, p.getProductId());
        }

        @Test
        @DisplayName("Binary search makes fewer comparisons than sequential for large arrays")
        void testComparisonCount() {
            int n = 1000;
            Product[] large = new Product[n];
            for (int i = 0; i < n; i++) {
                large[i] = new Product(i + 1, "P" + i, "Cat", 100.0, 1);
            }

            SearchAlgorithms.sequentialSearchById(large, 999);
            long seqCmp = SearchAlgorithms.getLastComparisonCount();

            SearchAlgorithms.binarySearchById(large, 999);
            long binCmp = SearchAlgorithms.getLastComparisonCount();

            assertTrue(binCmp < seqCmp,
                    "Binary search should use fewer comparisons than sequential for n=" + n);
        }
    }

    // -----------------------------------------------------------------------
    // Name Search tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Search by Name")
    class NameSearchTests {

        @Test
        @DisplayName("Finds product by exact name")
        void testExactName() {
            Product p = SearchAlgorithms.searchByName(unsortedArray, "Beta Phone");
            assertNotNull(p);
            assertEquals(20, p.getProductId());
        }

        @Test
        @DisplayName("Name search is case-insensitive")
        void testCaseInsensitive() {
            assertNotNull(SearchAlgorithms.searchByName(unsortedArray, "BETA PHONE"));
            assertNotNull(SearchAlgorithms.searchByName(unsortedArray, "beta phone"));
            assertNotNull(SearchAlgorithms.searchByName(unsortedArray, "Beta Phone"));
        }

        @Test
        @DisplayName("Returns null for absent name")
        void testAbsentName() {
            assertNull(SearchAlgorithms.searchByName(unsortedArray, "NonExistent"));
        }

        @Test
        @DisplayName("Returns null for null name")
        void testNullName() {
            assertNull(SearchAlgorithms.searchByName(unsortedArray, null));
        }
    }

    // -----------------------------------------------------------------------
    // addProduct tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("addProduct (sorted-insert)")
    class AddProductTests {

        @Test
        @DisplayName("Insert at end maintains sorted order")
        void testInsertAtEnd() {
            Product[] arr = new Product[sortedArray.length + 1];
            System.arraycopy(sortedArray, 0, arr, 0, sortedArray.length);
            // arr[5] == null (free slot)

            Product newProd = new Product(60, "Zeta Drone", "Electronics", 300_000.0, 2);
            SearchAlgorithms.addProduct(arr, newProd);

            assertSorted(arr, arr.length);
            assertEquals(60, arr[arr.length - 1].getProductId());
        }

        @Test
        @DisplayName("Insert at beginning maintains sorted order")
        void testInsertAtStart() {
            Product[] arr = new Product[sortedArray.length + 1];
            System.arraycopy(sortedArray, 0, arr, 0, sortedArray.length);

            Product newProd = new Product(5, "Zeta Drone", "Electronics", 50_000.0, 2);
            SearchAlgorithms.addProduct(arr, newProd);

            assertSorted(arr, arr.length);
            assertEquals(5, arr[0].getProductId());
        }

        @Test
        @DisplayName("Insert in the middle maintains sorted order")
        void testInsertInMiddle() {
            Product[] arr = new Product[sortedArray.length + 1];
            System.arraycopy(sortedArray, 0, arr, 0, sortedArray.length);

            Product newProd = new Product(25, "Mid Product", "Electronics", 75_000.0, 5);
            SearchAlgorithms.addProduct(arr, newProd);

            assertSorted(arr, arr.length);
        }

        @Test
        @DisplayName("No free slot throws IllegalArgumentException")
        void testNoFreeSlot() {
            // Full array, no null at end
            Product newProd = new Product(99, "New", "Cat", 10.0, 1);
            assertThrows(IllegalArgumentException.class,
                    () -> SearchAlgorithms.addProduct(sortedArray, newProd));
        }

        private void assertSorted(Product[] arr, int size) {
            for (int i = 1; i < size; i++) {
                assertNotNull(arr[i]);
                assertTrue(arr[i - 1].getProductId() < arr[i].getProductId(),
                        "Array not sorted at index " + i);
            }
        }
    }

    // -----------------------------------------------------------------------
    // HybridSearchIndex tests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("HybridSearchIndex")
    class HybridIndexTests {

        private HybridSearchIndex index;

        @BeforeEach
        void setupIndex() {
            index = new HybridSearchIndex(sortedArray);
        }

        @Test
        @DisplayName("Index size equals array size after construction")
        void testInitialSize() {
            assertEquals(sortedArray.length, index.size());
        }

        @Test
        @DisplayName("Search by name returns correct product")
        void testSearchByName() {
            Product p = index.searchByName("Alpha Laptop");
            assertNotNull(p);
            assertEquals(10, p.getProductId());
        }

        @Test
        @DisplayName("Name search is case-insensitive")
        void testCaseInsensitiveNameSearch() {
            assertNotNull(index.searchByName("ALPHA LAPTOP"));
            assertNotNull(index.searchByName("alpha laptop"));
        }

        @Test
        @DisplayName("Search by ID returns correct product")
        void testSearchById() {
            Product p = index.searchById(40);
            assertNotNull(p);
            assertEquals("Delta Monitor", p.getProductName());
        }

        @Test
        @DisplayName("addProduct increases size and is findable")
        void testAddProduct() {
            Product newProd = new Product(99, "New Gadget", "Gadgets", 50_000.0, 5);
            index.addProduct(newProd);

            assertEquals(sortedArray.length + 1, index.size());
            assertNotNull(index.searchByName("New Gadget"));
            assertNotNull(index.searchById(99));
        }

        @Test
        @DisplayName("removeProduct decreases size")
        void testRemoveProduct() {
            index.removeProduct(20);
            assertEquals(sortedArray.length - 1, index.size());
            assertNull(index.searchById(20));
            assertNull(index.searchByName("Beta Phone"));
        }

        @Test
        @DisplayName("Search returns null for absent entries")
        void testMissingEntries() {
            assertNull(index.searchByName("NonExistentProduct"));
            assertNull(index.searchById(99999));
        }

        @Test
        @DisplayName("null name returns null safely")
        void testNullName() {
            assertNull(index.searchByName(null));
        }
    }
}
