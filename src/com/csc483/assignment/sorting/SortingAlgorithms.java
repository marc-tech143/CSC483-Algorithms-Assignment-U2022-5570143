package com.csc483.assignment.sorting;

/**
 * Hand-written implementations of four classic sorting algorithms,
 * each instrumented to count comparisons and swaps/assignments.
 *
 * <p>All methods sort an {@code int[]} in ascending order.
 * No Java built-in sorting methods are used in the implementations.</p>
 *
 * <h2>Complexity Summary</h2>
 * <pre>
 *  Algorithm       Best          Average       Worst         Space   Stable  In-Place
 *  ─────────────── ──────────── ─────────────  ────────────  ──────  ──────  ────────
 *  Insertion Sort  O(n)          O(n^2)         O(n^2)        O(1)    Yes     Yes
 *  Merge Sort      O(n log n)    O(n log n)     O(n log n)    O(n)    Yes     No
 *  Quick Sort      O(n log n)    O(n log n)     O(n^2)        O(log n)No     Yes
 *  Heap Sort       O(n log n)    O(n log n)     O(n log n)    O(1)    No      Yes
 * </pre>
 *
 * @author  CSC483 Student
 * @version 1.0
 */
public final class SortingAlgorithms {

    private SortingAlgorithms() { }

    // -----------------------------------------------------------------------
    // Instrumentation counters (reset at the start of each sort call)
    // -----------------------------------------------------------------------

    private static long comparisons = 0;
    private static long swaps       = 0;

    /** @return comparisons made during the most recent sort call */
    public static long getComparisons() { return comparisons; }

    /** @return swaps / assignments made during the most recent sort call */
    public static long getSwaps()       { return swaps; }

    // -----------------------------------------------------------------------
    // 1. INSERTION SORT
    // -----------------------------------------------------------------------

    /**
     * Sorts {@code arr} using the insertion-sort algorithm.
     *
     * <p>The algorithm builds a sorted sub-array from left to right.
     * Each new element is compared with elements in the sorted region and
     * inserted into its correct position.</p>
     *
     * <p><b>Best case O(n):</b>  Array already sorted – inner loop never executes.<br>
     * <b>Average O(n^2):</b>  Each element moves about n/4 positions.<br>
     * <b>Worst O(n^2):</b>   Reverse-sorted – every element shifts all the way left.</p>
     *
     * <p>Stable and in-place; preferred for small or nearly-sorted inputs.</p>
     *
     * @param arr array to sort (modified in-place)
     */
    public static void insertionSort(int[] arr) {
        comparisons = 0;
        swaps       = 0;
        if (arr == null || arr.length < 2) return;

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];        // element to insert
            int j   = i - 1;

            // Shift elements that are greater than key one position to the right
            while (j >= 0) {
                comparisons++;
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];  // shift right
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;  // insert key at its correct position
            swaps++;
        }
    }

    // -----------------------------------------------------------------------
    // 2. MERGE SORT
    // -----------------------------------------------------------------------

    /**
     * Sorts {@code arr} using the top-down merge-sort algorithm.
     *
     * <p>The array is recursively divided into halves until sub-arrays of
     * size 1 are reached (trivially sorted), then merged back in sorted order.</p>
     *
     * <p><b>All cases O(n log n):</b> guarantees the same behaviour regardless
     * of input order.  Requires O(n) auxiliary space for the temporary array.</p>
     *
     * <p>Stable; the preferred algorithm when sort stability is required and
     * memory is not constrained.</p>
     *
     * @param arr array to sort (modified in-place, but uses O(n) auxiliary space)
     */
    public static void mergeSort(int[] arr) {
        comparisons = 0;
        swaps       = 0;
        if (arr == null || arr.length < 2) return;

        int[] temp = new int[arr.length];
        mergeSortHelper(arr, temp, 0, arr.length - 1);
    }

    private static void mergeSortHelper(int[] arr, int[] temp, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSortHelper(arr, temp, left, mid);
        mergeSortHelper(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right) {
        // Copy the sub-array into temp
        for (int k = left; k <= right; k++) {
            temp[k] = arr[k];
            swaps++;
        }

        int i = left;       // pointer into left half
        int j = mid + 1;    // pointer into right half
        int k = left;       // pointer into result

        while (i <= mid && j <= right) {
            comparisons++;
            if (temp[i] <= temp[j]) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
            swaps++;
        }
        // Drain remaining left-half elements (right-half are already in place)
        while (i <= mid) {
            arr[k++] = temp[i++];
            swaps++;
        }
    }

    // -----------------------------------------------------------------------
    // 3. QUICK SORT  (randomised pivot – median-of-three)
    // -----------------------------------------------------------------------

    /**
     * Sorts {@code arr} using randomised quick-sort with median-of-three
     * pivot selection.
     *
     * <p>The median-of-three strategy avoids the O(n^2) degenerate case on
     * already-sorted or reverse-sorted inputs that plagues naive pivot selection.</p>
     *
     * <p><b>Best / Average O(n log n):</b> balanced partitions.<br>
     * <b>Worst O(n^2):</b>  highly skewed partitions (unlikely with median-of-three).</p>
     *
     * <p>In-place, not stable.  The fastest practical sorting algorithm for
     * random data on modern hardware due to cache-friendly access patterns.</p>
     *
     * @param arr array to sort (modified in-place)
     */
    public static void quickSort(int[] arr) {
        comparisons = 0;
        swaps       = 0;
        if (arr == null || arr.length < 2) return;

        quickSortHelper(arr, 0, arr.length - 1);
    }

    private static void quickSortHelper(int[] arr, int low, int high) {
        if (low >= high) return;

        // Small sub-arrays: fall back to insertion sort for efficiency
        if (high - low < 10) {
            insertionSortRange(arr, low, high);
            return;
        }

        int pivotIndex = partition(arr, low, high);
        quickSortHelper(arr, low, pivotIndex - 1);
        quickSortHelper(arr, pivotIndex + 1, high);
    }

    /**
     * Median-of-three pivot selection and Lomuto partition scheme.
     */
    private static int partition(int[] arr, int low, int high) {
        // Median-of-three: pick median of arr[low], arr[mid], arr[high] as pivot
        int mid = low + (high - low) / 2;
        medianOfThree(arr, low, mid, high);
        // Now arr[high] is the median value (our pivot)

        int pivot = arr[high];
        int i     = low - 1;

        for (int j = low; j < high; j++) {
            comparisons++;
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * Rearranges so that arr[high] = median(arr[low], arr[mid], arr[high]).
     */
    private static void medianOfThree(int[] arr, int low, int mid, int high) {
        comparisons += 3;
        if (arr[low] > arr[mid])  swap(arr, low, mid);
        if (arr[low] > arr[high]) swap(arr, low, high);
        if (arr[mid] > arr[high]) swap(arr, mid, high);
        // Place pivot (arr[mid]) at arr[high-1] to keep it out of partition
        swap(arr, mid, high);
    }

    /** Insertion sort on the sub-range [low..high] (used for small arrays). */
    private static void insertionSortRange(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j   = i - 1;
            while (j >= low) {
                comparisons++;
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            swaps++;
        }
    }

    // -----------------------------------------------------------------------
    // 4. HEAP SORT
    // -----------------------------------------------------------------------

    /**
     * Sorts {@code arr} using the heap-sort algorithm.
     *
     * <p>Phase 1 builds a max-heap in O(n) time.
     * Phase 2 repeatedly extracts the maximum to the end of the array,
     * restoring the heap property after each extraction (O(log n) per step),
     * yielding O(n log n) total.</p>
     *
     * <p><b>All cases O(n log n):</b> unlike quick-sort, worst case is also
     * O(n log n).  In-place but not stable.  Useful when guaranteed worst-case
     * performance is required and no additional memory is available.</p>
     *
     * @param arr array to sort (modified in-place)
     */
    public static void heapSort(int[] arr) {
        comparisons = 0;
        swaps       = 0;
        if (arr == null || arr.length < 2) return;

        int n = arr.length;

        // Phase 1: Build max-heap (heapify from last non-leaf to root)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Phase 2: Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);             // move current root (max) to end
            heapify(arr, i, 0);          // restore heap on reduced array
        }
    }

    /**
     * Maintains the max-heap property for the sub-tree rooted at index {@code root}
     * within the first {@code heapSize} elements.
     */
    private static void heapify(int[] arr, int heapSize, int root) {
        int largest = root;
        int left    = 2 * root + 1;
        int right   = 2 * root + 2;

        if (left < heapSize) {
            comparisons++;
            if (arr[left] > arr[largest]) largest = left;
        }
        if (right < heapSize) {
            comparisons++;
            if (arr[right] > arr[largest]) largest = right;
        }
        if (largest != root) {
            swap(arr, root, largest);
            heapify(arr, heapSize, largest);
        }
    }

    // -----------------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------------

    private static void swap(int[] arr, int i, int j) {
        int tmp  = arr[i];
        arr[i]   = arr[j];
        arr[j]   = tmp;
        swaps++;
    }
}
