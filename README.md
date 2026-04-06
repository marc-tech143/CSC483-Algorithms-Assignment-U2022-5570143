# CSC483-Algorithms-Assignment

**Course:** CSC 483.1 – Algorithms Analysis and Design  
**Title:** Algorithm Design, Analysis, and Optimization for Real-World Systems  
**Submission Date:** April 5, 2026  

---

## Project Structure

```
CSC483-Algorithms-Assignment/
├── src/
│   └── com/csc483/assignment/
│       ├── search/
│       │   ├── Product.java              # Product entity (Q1 Part B)
│       │   ├── SearchAlgorithms.java     # Sequential, Binary, Name search + addProduct
│       │   ├── HybridSearchIndex.java    # HashMap + TreeMap hybrid index (Q1 Part C)
│       │   └── TechMartBenchmark.java    # Main benchmark driver (Q1 Part B)
│       ├── sorting/
│       │   ├── SortingAlgorithms.java    # Insertion, Merge, Quick, Heap Sort (Q2)
│       │   └── SortingBenchmark.java     # Empirical sorting analysis (Q2 Part C)
│       └── test/
│           ├── ProductSearchTest.java    # JUnit 5 tests for search (Q1)
│           └── SortingTest.java          # JUnit 5 tests for sorting (Q2)
├── data/
│   └── (sample datasets generated at runtime)
├── output/
│   └── (benchmark output screenshots)
├── .gitignore
└── README.md
```

---

## Prerequisites

| Tool       | Version  |
|------------|----------|
| Java       | 11+      |
| JUnit 5    | 5.9+     |

---

## Compilation Instructions

### Option 1 – Command Line (no build tool)

```bash
# Create output directory
mkdir -p out

# Compile all source files
javac -d out \
  src/com/csc483/assignment/search/*.java \
  src/com/csc483/assignment/sorting/*.java

# Compile tests (requires junit-platform-standalone JAR on classpath)
# Download: https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/
javac -cp out:junit-platform-console-standalone-1.9.0.jar -d out \
  src/com/csc483/assignment/test/*.java
```

### Option 2 – IntelliJ IDEA

1. Open the project folder.
2. Go to **File → Project Structure → Libraries** and add JUnit 5.
3. Right-click any file and choose **Compile**.

### Option 3 – VS Code

Install the **Extension Pack for Java** and **Test Runner for Java**.  
JUnit 5 jars are resolved automatically if Maven/Gradle is configured.

---

## Execution Instructions

### Run TechMart Search Benchmark (Question 1)

```bash
java -cp out com.csc483.assignment.search.TechMartBenchmark
```

Expected output (approximate):

```
================================================================
  TECHMART SEARCH PERFORMANCE ANALYSIS (n = 100,000 products)
================================================================
...
PERFORMANCE IMPROVEMENT: Binary search is ~2941x faster on average
================================================================
```

### Run Sorting Benchmark (Question 2)

```bash
java -cp out com.csc483.assignment.sorting.SortingBenchmark
```

### Run JUnit Tests

```bash
java -cp out:junit-platform-console-standalone-1.9.0.jar \
  org.junit.platform.console.ConsoleLauncher \
  --scan-class-path
```

---

## Sample Usage

```java
// Create and search products
Product[] products = { new Product(10, "Laptop", "Laptop", 150000.0, 5) };
Product found = SearchAlgorithms.binarySearchById(products, 10);
System.out.println(found); // Product{id=10, name='Laptop', ...}

// Hybrid index
HybridSearchIndex index = new HybridSearchIndex(products);
Product byName = index.searchByName("Laptop");
```

---

## Dependencies

| Library                              | Purpose           | Where to get |
|--------------------------------------|-------------------|--------------|
| JUnit 5 (junit-platform-standalone)  | Unit testing      | [Maven Central](https://search.maven.org/) |

No other external dependencies are required.

---

## Known Limitations

1. `addProduct()` operates on a fixed-size array with a pre-allocated null slot.
   In production, a dynamic `ArrayList` would be more practical.
2. The benchmarks print wall-clock time; JVM warm-up effects may slightly skew
   first-run results. Always use the averaged values.
3. `SortingBenchmark` skips Insertion Sort for `n = 100,000` to avoid
   extremely long wait times (estimated ~25 seconds on modern hardware).

---

## .gitignore

```
# Compiled output
out/
*.class

# IDE files
.idea/
*.iml
.vscode/
.settings/
.classpath
.project

# OS files
.DS_Store
Thumbs.db
```
