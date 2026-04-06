package com.csc483.assignment.test;

import com.csc483.assignment.sorting.SortingAlgorithms;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for {@link SortingAlgorithms}.
 *
 * <p>Each of the four algorithms (Insertion, Merge, Quick, Heap) is tested
 * for correctness across the following scenarios:</p>
 * <ul>
 *   <li>Empty array</li>
 *   <li>Single element</li>
 *   <li>Already sorted</li>
 *   <li>Reverse sorted</li>
 *   <li>Random order</li>
 *   <li>Duplicates</li>
 *   <li>All same elements</li>
 *   <li>Two-element arrays</li>
 *   <li>Large arrays (n = 50,000) verified against Arrays.sort</li>
 * </ul>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
@DisplayName("Sorting Algorithm Tests")
class SortingTest {

    private static final Random RNG = new Random(42L);

    // -----------------------------------------------------------------------
    // Parameterised source: runs each test for all four algorithms
    // -----------------------------------------------------------------------

    /**
     * Functional interface that delegates to one of the four sort methods.
     */
    @FunctionalInterface
    interface Sorter {
        void sort(int[] arr);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> sorterProvider() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("InsertionSort",
                    (Sorter) SortingAlgorithms::insertionSort),
            org.junit.jupiter.params.provider.Arguments.of("MergeSort",
                    (Sorter) SortingAlgorithms::mergeSort),
            org.junit.jupiter.params.provider.Arguments.of("QuickSort",
                    (Sorter) SortingAlgorithms::quickSort),
            org.junit.jupiter.params.provider.Arguments.of("HeapSort",
                    (Sorter) SortingAlgorithms::heapSort)
        );
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    /** Returns true if arr is sorted in non-decreasing order. */
    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] > arr[i]) return false;
        }
        return true;
    }

    /** Returns a random int array of given size. */
    private static int[] randomArray(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) arr[i] = RNG.nextInt(size * 10);
        return arr;
    }

    // -----------------------------------------------------------------------
    // Parameterised correctness tests
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "{0} – null array (no-op)")
    @MethodSource("sorterProvider")
    @DisplayName("Null array – no exception")
    void testNullArray(String name, Sorter sorter) {
        assertDoesNotThrow(() -> sorter.sort(null));
    }

    @ParameterizedTest(name = "{0} – empty array")
    @MethodSource("sorterProvider")
    @DisplayName("Empty array – no-op")
    void testEmptyArray(String name, Sorter sorter) {
        int[] arr = {};
        sorter.sort(arr);
        assertEquals(0, arr.length);
    }

    @ParameterizedTest(name = "{0} – single element")
    @MethodSource("sorterProvider")
    @DisplayName("Single element – unchanged")
    void testSingleElement(String name, Sorter sorter) {
        int[] arr = {42};
        sorter.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @ParameterizedTest(name = "{0} – two elements (swapped)")
    @MethodSource("sorterProvider")
    @DisplayName("Two elements – sorted correctly")
    void testTwoElements(String name, Sorter sorter) {
        int[] arr = {9, 3};
        sorter.sort(arr);
        assertArrayEquals(new int[]{3, 9}, arr);
    }

    @ParameterizedTest(name = "{0} – two elements (already sorted)")
    @MethodSource("sorterProvider")
    @DisplayName("Two elements – already sorted")
    void testTwoElementsSorted(String name, Sorter sorter) {
        int[] arr = {3, 9};
        sorter.sort(arr);
        assertArrayEquals(new int[]{3, 9}, arr);
    }

    @ParameterizedTest(name = "{0} – all same elements")
    @MethodSource("sorterProvider")
    @DisplayName("All same elements – no-op (remains sorted)")
    void testAllSameElements(String name, Sorter sorter) {
        int[] arr = {7, 7, 7, 7, 7};
        sorter.sort(arr);
        assertTrue(isSorted(arr));
    }

    @ParameterizedTest(name = "{0} – already sorted n=100")
    @MethodSource("sorterProvider")
    @DisplayName("Already sorted array (best case for InsertionSort)")
    void testAlreadySorted(String name, Sorter sorter) {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) arr[i] = i;
        sorter.sort(arr);
        assertTrue(isSorted(arr), name + " failed on already-sorted array");
    }

    @ParameterizedTest(name = "{0} – reverse sorted n=100")
    @MethodSource("sorterProvider")
    @DisplayName("Reverse sorted array (worst case for InsertionSort)")
    void testReverseSorted(String name, Sorter sorter) {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) arr[i] = 100 - i;
        sorter.sort(arr);
        assertTrue(isSorted(arr), name + " failed on reverse-sorted array");
    }

    @ParameterizedTest(name = "{0} – random n=1000")
    @MethodSource("sorterProvider")
    @DisplayName("Random array n=1,000 – result matches Arrays.sort")
    void testRandomSmall(String name, Sorter sorter) {
        int[] arr      = randomArray(1_000);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        sorter.sort(arr);

        assertArrayEquals(expected, arr,
                name + " produced incorrect result on random n=1,000");
    }

    @ParameterizedTest(name = "{0} – duplicates n=500")
    @MethodSource("sorterProvider")
    @DisplayName("Array with many duplicates – result matches Arrays.sort")
    void testDuplicates(String name, Sorter sorter) {
        int[] arr = new int[500];
        for (int i = 0; i < 500; i++) arr[i] = RNG.nextInt(10);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        sorter.sort(arr);

        assertArrayEquals(expected, arr,
                name + " failed on duplicates array");
    }

    @ParameterizedTest(name = "{0} – negative numbers")
    @MethodSource("sorterProvider")
    @DisplayName("Array with negative numbers")
    void testNegativeNumbers(String name, Sorter sorter) {
        int[] arr      = {-5, 3, -1, 0, 7, -9, 2};
        int[] expected = arr.clone();
        Arrays.sort(expected);

        sorter.sort(arr);

        assertArrayEquals(expected, arr);
    }

    // -----------------------------------------------------------------------
    // Large-scale correctness test (MergeSort, QuickSort, HeapSort only)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("MergeSort – large array n=50,000 matches Arrays.sort")
    void testMergeSortLarge() {
        int[] arr      = randomArray(50_000);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);
        SortingAlgorithms.mergeSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    @DisplayName("QuickSort – large array n=50,000 matches Arrays.sort")
    void testQuickSortLarge() {
        int[] arr      = randomArray(50_000);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);
        SortingAlgorithms.quickSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    @DisplayName("HeapSort – large array n=50,000 matches Arrays.sort")
    void testHeapSortLarge() {
        int[] arr      = randomArray(50_000);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);
        SortingAlgorithms.heapSort(arr);
        assertArrayEquals(expected, arr);
    }

    // -----------------------------------------------------------------------
    // Instrumentation sanity tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("InsertionSort – 0 comparisons on single element")
    void testInsertionZeroComparisons() {
        SortingAlgorithms.insertionSort(new int[]{1});
        assertEquals(0, SortingAlgorithms.getComparisons());
    }

    @Test
    @DisplayName("InsertionSort – best-case comparisons ≈ n-1 on sorted array")
    void testInsertionBestCaseComparisons() {
        int n   = 100;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        SortingAlgorithms.insertionSort(arr);
        // Each of the n-1 outer iterations makes exactly 1 comparison (element <= key)
        assertEquals(n - 1, SortingAlgorithms.getComparisons());
    }

    @Test
    @DisplayName("MergeSort comparisons count is positive for n > 1")
    void testMergeSortComparisons() {
        SortingAlgorithms.mergeSort(randomArray(1_000));
        assertTrue(SortingAlgorithms.getComparisons() > 0);
    }

    @Test
    @DisplayName("QuickSort swap count is non-negative")
    void testQuickSortSwaps() {
        SortingAlgorithms.quickSort(randomArray(500));
        assertTrue(SortingAlgorithms.getSwaps() >= 0);
    }
}
