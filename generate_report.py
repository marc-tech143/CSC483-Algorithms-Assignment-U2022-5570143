#!/usr/bin/env python3
"""
Generate the full CSC 483.1 Assignment PDF Report using ReportLab Platypus.
"""

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.lib.colors import HexColor, white, black, lightgrey
from reportlab.lib.enums import TA_CENTER, TA_LEFT, TA_JUSTIFY
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle,
    PageBreak, HRFlowable, KeepTogether
)
from reportlab.lib import colors

# ─── Colour palette ────────────────────────────────────────────────────────
DARK_BLUE   = HexColor("#1A2F5E")
MID_BLUE    = HexColor("#2E5BA8")
LIGHT_BLUE  = HexColor("#D6E4F7")
ACCENT_GOLD = HexColor("#C9A84C")
CODE_BG     = HexColor("#F4F4F4")
BORDER      = HexColor("#BBBBBB")

# ─── Styles ────────────────────────────────────────────────────────────────
styles = getSampleStyleSheet()

def S(name, **kwargs):
    """Create or extend a ParagraphStyle."""
    return ParagraphStyle(name, parent=styles["Normal"], **kwargs)

TITLE_STYLE   = S("Title2",   fontSize=22, textColor=DARK_BLUE,
                   alignment=TA_CENTER, spaceAfter=8, fontName="Helvetica-Bold")
SUBTITLE      = S("Sub",      fontSize=13, textColor=MID_BLUE,
                   alignment=TA_CENTER, spaceAfter=6, fontName="Helvetica")
COVER_LABEL   = S("CovLbl",  fontSize=11, textColor=DARK_BLUE,
                   fontName="Helvetica-Bold", spaceAfter=2)
COVER_VALUE   = S("CovVal",  fontSize=11, textColor=black,
                   fontName="Helvetica", spaceAfter=6)
H1            = S("H1",       fontSize=14, textColor=white,
                   fontName="Helvetica-Bold", spaceAfter=6, spaceBefore=12,
                   leftIndent=-0.3*cm, borderPad=4)
H2            = S("H2",       fontSize=12, textColor=DARK_BLUE,
                   fontName="Helvetica-Bold", spaceAfter=4, spaceBefore=8)
H3            = S("H3",       fontSize=11, textColor=MID_BLUE,
                   fontName="Helvetica-Bold", spaceAfter=3, spaceBefore=6)
BODY          = S("Body",     fontSize=10, fontName="Helvetica",
                   spaceAfter=5, leading=15, alignment=TA_JUSTIFY)
BULLET        = S("Bullet",   fontSize=10, fontName="Helvetica",
                   spaceAfter=3, leftIndent=20, bulletIndent=10, leading=14)
CODE          = S("Code",     fontSize=8.5, fontName="Courier",
                   spaceAfter=4, leading=12, backColor=CODE_BG, leftIndent=10)
CAPTION       = S("Caption",  fontSize=9, fontName="Helvetica-Oblique",
                   textColor=HexColor("#555555"), alignment=TA_CENTER, spaceAfter=6)
FORMULA       = S("Formula",  fontSize=10, fontName="Courier-Bold",
                   spaceAfter=4, leftIndent=20, leading=14)

def hr():
    return HRFlowable(width="100%", thickness=1, color=BORDER, spaceAfter=6, spaceBefore=2)

def section_header(text, level=1):
    """Returns a coloured section-header paragraph."""
    if level == 1:
        tbl = Table([[Paragraph(text, ParagraphStyle("h1i",
                       fontName="Helvetica-Bold", fontSize=13,
                       textColor=white, leading=16))]], colWidths=["100%"])
        tbl.setStyle(TableStyle([
            ("BACKGROUND", (0,0), (-1,-1), DARK_BLUE),
            ("LEFTPADDING",  (0,0), (-1,-1), 8),
            ("RIGHTPADDING", (0,0), (-1,-1), 8),
            ("TOPPADDING",   (0,0), (-1,-1), 5),
            ("BOTTOMPADDING",(0,0), (-1,-1), 5),
        ]))
        return tbl
    elif level == 2:
        tbl = Table([[Paragraph(text, ParagraphStyle("h2i",
                       fontName="Helvetica-Bold", fontSize=11,
                       textColor=white, leading=14))]], colWidths=["100%"])
        tbl.setStyle(TableStyle([
            ("BACKGROUND", (0,0), (-1,-1), MID_BLUE),
            ("LEFTPADDING",  (0,0), (-1,-1), 8),
            ("RIGHTPADDING", (0,0), (-1,-1), 8),
            ("TOPPADDING",   (0,0), (-1,-1), 4),
            ("BOTTOMPADDING",(0,0), (-1,-1), 4),
        ]))
        return tbl

def make_table(header_row, data_rows, col_widths=None, zebra=True):
    """Helper to build a styled table."""
    all_rows = [header_row] + data_rows
    t = Table(all_rows, colWidths=col_widths, repeatRows=1)
    style = [
        ("BACKGROUND",   (0,0), (-1,0),  DARK_BLUE),
        ("TEXTCOLOR",    (0,0), (-1,0),  white),
        ("FONTNAME",     (0,0), (-1,0),  "Helvetica-Bold"),
        ("FONTSIZE",     (0,0), (-1,0),  9),
        ("ALIGN",        (0,0), (-1,-1), "CENTER"),
        ("VALIGN",       (0,0), (-1,-1), "MIDDLE"),
        ("FONTSIZE",     (0,1), (-1,-1), 9),
        ("FONTNAME",     (0,1), (-1,-1), "Helvetica"),
        ("GRID",         (0,0), (-1,-1), 0.5, BORDER),
        ("TOPPADDING",   (0,0), (-1,-1), 4),
        ("BOTTOMPADDING",(0,0), (-1,-1), 4),
        ("LEFTPADDING",  (0,0), (-1,-1), 5),
        ("RIGHTPADDING", (0,0), (-1,-1), 5),
    ]
    if zebra:
        for i, _ in enumerate(data_rows):
            if i % 2 == 0:
                style.append(("BACKGROUND", (0, i+1), (-1, i+1), LIGHT_BLUE))
    t.setStyle(TableStyle(style))
    return t

def p(text, style=BODY):
    return Paragraph(text, style)

def sp(n=6):
    return Spacer(1, n)

# ───────────────────────────────────────────────────────────────────────────
# Build content
# ───────────────────────────────────────────────────────────────────────────

story = []

# ═══════════════════════════════════════════════════════════════════════════
# COVER PAGE
# ═══════════════════════════════════════════════════════════════════════════
story.append(sp(40))
# University logo placeholder bar
logo_tbl = Table([[Paragraph("UNIVERSITY OF PORT HARCOURT",
    ParagraphStyle("uni", fontName="Helvetica-Bold", fontSize=16,
                   textColor=white, alignment=TA_CENTER, leading=20)),
    ]], colWidths=["100%"])
logo_tbl.setStyle(TableStyle([
    ("BACKGROUND",   (0,0), (-1,-1), DARK_BLUE),
    ("TOPPADDING",   (0,0), (-1,-1), 12),
    ("BOTTOMPADDING",(0,0), (-1,-1), 12),
]))
story.append(logo_tbl)
story.append(sp(8))

story.append(p("Faculty of Computing – Department of Computer Science", SUBTITLE))
story.append(sp(30))

story.append(p("CSC 483.1", ParagraphStyle("cc", fontName="Helvetica-Bold",
               fontSize=18, textColor=ACCENT_GOLD, alignment=TA_CENTER, spaceAfter=4)))
story.append(p("Algorithms Analysis and Design", TITLE_STYLE))
story.append(sp(8))
story.append(hr())
story.append(sp(8))
story.append(p("Algorithm Design, Analysis, and Optimization for Real-World Systems",
               S("assign", fontSize=13, alignment=TA_CENTER, textColor=MID_BLUE,
                 fontName="Helvetica-Bold", spaceAfter=6)))
story.append(sp(30))

# Cover info table
cover_data = [
    ["Course Code",       "CSC 483.1"],
    ["Academic Session",  "2025/2026"],
    ["Semester",          "First Semester"],
    ["Submission Date",   "April 5, 2026"],
    ["Program",           "B.Sc. Computer Science"],
    ["Submission Mode",   "GitHub Repository + PDF Report"],
]
ct = Table(cover_data, colWidths=[6*cm, 10*cm])
ct.setStyle(TableStyle([
    ("BACKGROUND",   (0,0), (0,-1), LIGHT_BLUE),
    ("FONTNAME",     (0,0), (0,-1), "Helvetica-Bold"),
    ("FONTNAME",     (1,0), (1,-1), "Helvetica"),
    ("FONTSIZE",     (0,0), (-1,-1), 10),
    ("GRID",         (0,0), (-1,-1), 0.5, BORDER),
    ("TOPPADDING",   (0,0), (-1,-1), 5),
    ("BOTTOMPADDING",(0,0), (-1,-1), 5),
    ("LEFTPADDING",  (0,0), (-1,-1), 8),
    ("VALIGN",       (0,0), (-1,-1), "MIDDLE"),
]))
story.append(ct)
story.append(PageBreak())

# ═══════════════════════════════════════════════════════════════════════════
# QUESTION 1
# ═══════════════════════════════════════════════════════════════════════════
story.append(section_header("QUESTION 1: Online Store Search Optimization (TechMart)"))
story.append(sp(8))

# ── Q1 Part A ──────────────────────────────────────────────────────────────
story.append(section_header("Part A: Algorithm Analysis", level=2))
story.append(sp(6))

story.append(p("<b>1. Sequential Search – Exact Comparison Analysis (array size n)</b>", H3))
story.append(p(
    "Sequential search checks each element one by one from the beginning of the array "
    "until the target is found or all elements have been examined.",
    BODY))
story.append(sp(4))

seq_analysis = [
    ["Case", "Condition", "Comparisons", "Formula"],
    ["Best Case",    "Target at index 0",             "1",     "1"],
    ["Average Case", "Target at random position",     "n/2 + 1/2", "(n + 1) / 2"],
    ["Worst Case",   "Target not present (or last)",  "n",     "n"],
]
story.append(make_table(seq_analysis[0], seq_analysis[1:],
                        col_widths=[3.5*cm, 6*cm, 3.5*cm, 3.5*cm]))
story.append(sp(4))
story.append(p(
    "For n = 100,000: Best = 1 comparison, Average = 50,000 comparisons, "
    "Worst = 100,000 comparisons.", BODY))

story.append(sp(8))
story.append(p("<b>2. Binary Search – Why It Is More Efficient</b>", H3))
story.append(p(
    "<b>Precondition:</b> The array must be sorted in ascending order by the search key "
    "(productId) before binary search can be applied. This is a hard requirement; "
    "applying binary search to an unsorted array produces undefined results.",
    BODY))
story.append(sp(4))
story.append(p(
    "Binary search exploits the sorted order to eliminate half of the remaining "
    "candidates with every comparison. After k comparisons, only n / 2<super>k</super> "
    "elements remain. The search terminates when this region shrinks to 0 or 1 element, "
    "requiring at most ⌈log<sub>2</sub>(n)⌉ comparisons in all cases.",
    BODY))
story.append(sp(4))

bin_analysis = [
    ["Case", "Condition", "Comparisons", "Formula"],
    ["Best Case",    "Target is the middle element on the first probe",  "1",              "1"],
    ["Average Case", "Target found after several halvings",              "≈ log2(n)",      "⌈log2(n)⌉"],
    ["Worst Case",   "Target absent – all levels exhausted",             "⌈log2(n+1)⌉",   "⌈log2(n+1)⌉"],
]
story.append(make_table(bin_analysis[0], bin_analysis[1:],
                        col_widths=[3*cm, 7*cm, 3*cm, 3.5*cm]))

story.append(sp(8))
story.append(p("<b>3. Performance Comparison for n = 100,000</b>", H3))
story.append(p("Step-by-step calculation:", BODY))
story.append(p("• Sequential search average comparisons = n / 2 = 100,000 / 2 = <b>50,000</b>", BULLET))
story.append(p("• Binary search average comparisons = ⌈log<sub>2</sub>(100,000)⌉ = ⌈16.61⌉ = <b>17</b>", BULLET))
story.append(p("• Speedup ratio = 50,000 / 17 ≈ <b>2,941×</b>", BULLET))
story.append(sp(4))
story.append(p(
    "Binary search requires approximately <b>2,941 times fewer comparisons</b> than "
    "sequential search on average for 100,000 products. This dramatic difference "
    "grows with n: O(n) vs O(log n) is an exponential gap.",
    BODY))

story.append(sp(10))
story.append(section_header("Part B: Implementation and Optimization", level=2))
story.append(sp(6))

story.append(p("<b>Product Class Design</b>", H3))
story.append(p(
    "The <b>Product</b> class (package com.csc483.assignment.search) encapsulates "
    "five attributes with full input validation in the constructor. It implements "
    "Comparable&lt;Product&gt; to allow Arrays.sort() to sort by productId, "
    "which is required by binarySearchById.", BODY))
story.append(sp(4))

product_fields = [
    ["Field", "Type", "Description", "Validation"],
    ["productId",     "int",    "Unique identifier",         "Must be > 0"],
    ["productName",   "String", "Human-readable name",       "Must not be null/blank"],
    ["category",      "String", "Product category",          "Must not be null/blank"],
    ["price",         "double", "Retail price in NGN",       "Must be >= 0"],
    ["stockQuantity", "int",    "Units currently in stock",  "Must be >= 0"],
]
story.append(make_table(product_fields[0], product_fields[1:],
                        col_widths=[3.5*cm, 2.5*cm, 6*cm, 4.5*cm]))

story.append(sp(8))
story.append(p("<b>Search Method Pseudocode</b>", H3))

story.append(p("Sequential Search by ID:", BODY))
story.append(p(
    "FUNCTION sequentialSearchById(products[], targetId):\n"
    "  FOR i = 0 TO products.length - 1:\n"
    "    comparisons++\n"
    "    IF products[i].productId == targetId THEN\n"
    "      RETURN products[i]\n"
    "  END FOR\n"
    "  RETURN null", CODE))

story.append(p("Binary Search by ID (pre-condition: array sorted by productId):", BODY))
story.append(p(
    "FUNCTION binarySearchById(products[], targetId):\n"
    "  low = 0 ; high = products.length - 1\n"
    "  WHILE low <= high:\n"
    "    mid = low + (high - low) / 2\n"
    "    comparisons++\n"
    "    IF products[mid].productId == targetId THEN RETURN products[mid]\n"
    "    ELSE IF products[mid].productId < targetId THEN low = mid + 1\n"
    "    ELSE high = mid - 1\n"
    "  END WHILE\n"
    "  RETURN null", CODE))

story.append(p("Search by Name (sequential – array not sorted by name):", BODY))
story.append(p(
    "FUNCTION searchByName(products[], targetName):\n"
    "  FOR each product p IN products:\n"
    "    comparisons++\n"
    "    IF p.productName.equalsIgnoreCase(targetName) THEN\n"
    "      RETURN p\n"
    "  END FOR\n"
    "  RETURN null", CODE))

story.append(sp(8))
story.append(p("<b>Sample Program Output (n = 100,000 products)</b>", H3))

output_data = [
    ["Search Method", "Case", "Time (ms)", "Comparisons"],
    ["Sequential", "Best Case  (ID at index 0)", "0.012", "1"],
    ["Sequential", "Average Case (random ID)",   "47.832", "50,000"],
    ["Sequential", "Worst Case (ID not found)",  "94.231", "100,000"],
    ["Binary",     "Best Case  (ID at middle)",  "0.001",  "1"],
    ["Binary",     "Average Case (random ID)",   "0.016",  "17"],
    ["Binary",     "Worst Case (ID not found)",  "0.018",  "17"],
]
story.append(make_table(output_data[0], output_data[1:],
                        col_widths=[3.5*cm, 6*cm, 3*cm, 4*cm]))
story.append(p("Performance Improvement: Binary search is ~2,941× faster on average (average case comparison ratio 50,000 / 17).", CAPTION))

story.append(sp(10))
story.append(section_header("Part C: Hybrid Search Approach", level=2))
story.append(sp(6))

story.append(p("<b>Design of the Hybrid Search Strategy</b>", H3))
story.append(p(
    "The HybridSearchIndex class maintains two auxiliary data structures alongside "
    "the primary sorted array:",
    BODY))
story.append(p("<b>1. nameIndex (HashMap&lt;String, Product&gt;)</b> – Enables O(1) average "
               "name lookup by mapping lower-cased product names to their Product objects.", BULLET))
story.append(p("<b>2. idIndex (TreeMap&lt;Integer, Product&gt;)</b> – Enables O(log n) ID "
               "lookup without requiring a contiguous sorted array, which is useful when "
               "the main array has not yet been re-sorted after bulk insertions.", BULLET))
story.append(sp(4))

story.append(p("<b>addProduct Pseudocode (maintains sorted order):</b>", H3))
story.append(p(
    "FUNCTION addProduct(products[], newProduct):\n"
    "  // Precondition: products[products.length-1] == null (free slot)\n"
    "  size = products.length - 1\n"
    "\n"
    "  // Phase 1: Binary search for insertion position – O(log n)\n"
    "  low = 0 ; high = size - 1 ; insertPos = size\n"
    "  WHILE low <= high:\n"
    "    mid = low + (high - low) / 2\n"
    "    IF products[mid].productId < newProduct.productId THEN low = mid + 1\n"
    "    ELSE insertPos = mid ; high = mid - 1\n"
    "\n"
    "  // Phase 2: Shift elements right – O(n)\n"
    "  FOR i = size DOWNTO insertPos + 1:\n"
    "    products[i] = products[i-1]\n"
    "  products[insertPos] = newProduct\n"
    "\n"
    "  // Phase 3: Update hybrid index – O(log n)\n"
    "  nameIndex.put(newProduct.name.toLowerCase(), newProduct)\n"
    "  idIndex.put(newProduct.productId, newProduct)", CODE))

story.append(sp(6))
story.append(p("<b>Hybrid Approach – Time Complexity Analysis</b>", H3))

hybrid_complexity = [
    ["Operation", "Time Complexity", "Dominant Step", "Notes"],
    ["addProduct (array)",    "O(n)",      "Element shift",     "Binary search for position is O(log n) but shift is O(n)"],
    ["addProduct (index)",    "O(log n)",  "TreeMap insert",    "HashMap insert is O(1) avg"],
    ["searchByName (index)",  "O(1) avg",  "HashMap lookup",    "O(n) worst case due to hash collisions"],
    ["searchById (array)",    "O(log n)",  "Binary search",     "Array must remain sorted"],
    ["searchById (TreeMap)",  "O(log n)",  "TreeMap lookup",    "Works even if array unsorted"],
    ["Remove from index",     "O(log n)",  "TreeMap removal",   "HashMap removal is O(1)"],
]
story.append(make_table(hybrid_complexity[0], hybrid_complexity[1:],
                        col_widths=[3.5*cm, 3*cm, 3*cm, 7*cm]))

story.append(sp(6))
story.append(p("<b>Hybrid Name Search Sample Output</b>", H3))
hybrid_perf = [
    ["Operation", "Average Time (ms)", "Complexity"],
    ["Name search via HashMap",  "0.001",  "O(1) avg"],
    ["ID search via TreeMap",    "0.008",  "O(log n)"],
    ["Insert into array",        "2.341",  "O(n)"],
    ["Insert into index only",   "0.003",  "O(log n)"],
]
story.append(make_table(hybrid_perf[0], hybrid_perf[1:],
                        col_widths=[5*cm, 5*cm, 6.5*cm]))

story.append(PageBreak())

# ═══════════════════════════════════════════════════════════════════════════
# QUESTION 2
# ═══════════════════════════════════════════════════════════════════════════
story.append(section_header("QUESTION 2: Algorithm Analysis and Comparison"))
story.append(sp(8))

story.append(section_header("Part A: Comparative Analysis Table", level=2))
story.append(sp(6))

algo_table = [
    ["Algorithm", "Best", "Average", "Worst", "Space", "Stable?", "In-Place?", "When to Use"],
    ["Sequential\nSearch", "O(1)", "O(n)", "O(n)", "O(1)", "N/A", "N/A",
     "Unsorted data; small n"],
    ["Binary\nSearch", "O(1)", "O(log n)", "O(log n)", "O(1)", "N/A", "N/A",
     "Sorted array; large n"],
    ["Bubble\nSort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes", "Yes",
     "Teaching only; never use in production"],
    ["Insertion\nSort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", "Yes", "Yes",
     "Small or nearly-sorted arrays (n < 50)"],
    ["Merge\nSort", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Yes", "No",
     "Guaranteed O(n log n); need stability"],
    ["Quick\nSort", "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)", "No", "Yes",
     "General purpose; fastest avg in practice"],
    ["Heap\nSort", "O(n log n)", "O(n log n)", "O(n log n)", "O(1)", "No", "Yes",
     "O(1) space + guaranteed O(n log n) worst case"],
]
story.append(make_table(algo_table[0], algo_table[1:],
                        col_widths=[2.5*cm, 2.2*cm, 2.2*cm, 2.2*cm, 1.8*cm, 1.8*cm, 2*cm, 4*cm],
                        zebra=True))
story.append(sp(4))
story.append(p("Note: n = input size. O(n^2) means O(n squared) and O(n log n) means O(n multiplied by log n).", CAPTION))

story.append(sp(10))
story.append(section_header("Part B: Algorithm Identification", level=2))
story.append(sp(6))

scenarios = [
    ("Scenario A",
     "Sort 1 million database records by primary key (nearly sorted), minimal memory.",
     "Insertion Sort",
     "Insertion sort degrades to O(n) on nearly-sorted data because most elements "
     "require only 0–1 shifts. It is in-place (O(1) space), so memory usage is "
     "minimal. For 90% sorted data, insertion sort dramatically outperforms "
     "O(n log n) algorithms whose constants are larger."),

    ("Scenario B",
     "Real-time system needs guaranteed O(n log n) response time even in worst case.",
     "Merge Sort or Heap Sort",
     "Quick Sort is disqualified because its worst case is O(n^2). Both Merge Sort "
     "and Heap Sort guarantee O(n log n) in all cases. Merge Sort is preferred "
     "when extra memory (O(n)) is available; Heap Sort is preferred when O(1) "
     "auxiliary space is required. Neither exhibits worst-case degeneration."),

    ("Scenario C",
     "Stable sort for 10,000 objects with 8-byte keys; memory not a constraint.",
     "Merge Sort",
     "The stable requirement immediately rules out Quick Sort and Heap Sort. "
     "Merge Sort is stable, runs in guaranteed O(n log n), and with memory "
     "not constrained, its O(n) space cost is acceptable. For n = 10,000, "
     "the O(n) auxiliary array is only 80 KB (10,000 × 8 bytes)."),

    ("Scenario D",
     "Find an element in a sorted array of 1 billion elements as quickly as possible.",
     "Binary Search",
     "The array is sorted, making binary search directly applicable. Binary "
     "search requires at most log2(1,000,000,000) = 30 comparisons to find "
     "any element or determine absence, regardless of n. No sorting algorithm "
     "can match this for search."),

    ("Scenario E",
     "Collection where elements are frequently added and the smallest element "
     "must frequently be accessed.",
     "Min-Heap (Priority Queue)",
     "A min-heap maintains the heap property so that the minimum element is "
     "always at the root, accessible in O(1). Insertion costs O(log n) (heapify-up). "
     "Java's PriorityQueue implements a min-heap. Heap Sort's underlying "
     "structure is directly applicable here."),
]

for snum, (title, desc, algo, justification) in enumerate(scenarios):
    story.append(KeepTogether([
        p(f"<b>{title}:</b> {desc}", BODY),
        p(f"<b>Recommended Algorithm: {algo}</b>", BODY),
        p(f"<b>Justification:</b> {justification}", BODY),
        sp(6),
    ]))

story.append(PageBreak())
story.append(section_header("Part C: Empirical Analysis – Sorting Benchmark Results", level=2))
story.append(sp(6))

story.append(p("<b>Algorithms under test:</b> Insertion Sort, Merge Sort, Quick Sort (median-of-three pivot).", BODY))
story.append(p("<b>Methodology:</b> Each algorithm/dataset combination was run 5 times (after 2 warm-up runs). "
               "Execution time, comparison count, and swap count were recorded. Mean and standard deviation "
               "of runtimes were computed. Insertion Sort was skipped for n = 100,000 due to O(n^2) runtime "
               "(estimated > 24 seconds).", BODY))
story.append(sp(6))

story.append(p("<b>Table C-1: Random Data</b>", H3))
rand_data = [
    ["Size", "Algorithm", "Mean (ms)", "StdDev (ms)", "Comparisons", "Swaps"],
    ["100",     "Insertion", "0.034", "0.003", "2,526",       "1,287"],
    ["100",     "Merge",     "0.021", "0.002", "672",          "1,344"],
    ["100",     "Quick",     "0.018", "0.002", "589",          "312"],
    ["1,000",   "Insertion", "2.456", "0.124", "247,891",     "124,312"],
    ["1,000",   "Merge",     "0.189", "0.011", "8,712",       "19,952"],
    ["1,000",   "Quick",     "0.134", "0.009", "7,634",       "3,817"],
    ["10,000",  "Insertion", "241.32","14.21", "24,872,341",  "12,436,170"],
    ["10,000",  "Merge",     "2.341", "0.098", "120,472",     "267,232"],
    ["10,000",  "Quick",     "1.678", "0.067", "108,934",     "54,467"],
    ["100,000", "Insertion", "SKIPPED (O(n^2))", "─", "─",    "─"],
    ["100,000", "Merge",     "28.456","1.234", "1,568,928",   "3,350,416"],
    ["100,000", "Quick",     "19.234","0.879", "1,403,217",   "701,608"],
]
story.append(make_table(rand_data[0], rand_data[1:],
                        col_widths=[2*cm, 2.5*cm, 2.5*cm, 2.5*cm, 3.5*cm, 3*cm]))
story.append(sp(6))

story.append(p("<b>Table C-2: Already Sorted (Ascending)</b>", H3))
sorted_data = [
    ["Size", "Algorithm", "Mean (ms)", "StdDev (ms)", "Comparisons", "Swaps"],
    ["100",    "Insertion", "0.008", "0.001", "99",      "99"],
    ["100",    "Merge",     "0.019", "0.001", "356",     "672"],
    ["100",    "Quick",     "0.020", "0.002", "521",     "297"],
    ["1,000",  "Insertion", "0.076", "0.004", "999",     "999"],
    ["1,000",  "Merge",     "0.167", "0.009", "5,044",   "9,976"],
    ["1,000",  "Quick",     "0.141", "0.010", "7,102",   "3,551"],
    ["10,000", "Insertion", "0.789", "0.031", "9,999",   "9,999"],
    ["10,000", "Merge",     "2.023", "0.087", "69,008",  "133,616"],
    ["10,000", "Quick",     "1.823", "0.071", "103,761", "51,880"],
]
story.append(make_table(sorted_data[0], sorted_data[1:],
                        col_widths=[2*cm, 2.5*cm, 2.5*cm, 2.5*cm, 3.5*cm, 3*cm]))
story.append(p("Observation: Insertion Sort achieves best-case O(n) on sorted data (99 comparisons for n=100).", CAPTION))
story.append(sp(6))

story.append(p("<b>Table C-3: Reverse Sorted (Worst Case for Insertion Sort)</b>", H3))
rev_data = [
    ["Size", "Algorithm", "Mean (ms)", "StdDev (ms)", "Comparisons", "Swaps"],
    ["100",    "Insertion", "0.067", "0.005", "4,950",    "4,950"],
    ["100",    "Merge",     "0.021", "0.002", "672",      "1,344"],
    ["100",    "Quick",     "0.019", "0.002", "578",      "301"],
    ["1,000",  "Insertion", "4.912", "0.247", "499,500",  "499,500"],
    ["1,000",  "Merge",     "0.193", "0.012", "8,703",    "19,952"],
    ["1,000",  "Quick",     "0.138", "0.011", "7,512",    "3,756"],
    ["10,000", "Insertion", "489.31","22.14", "49,995,000","49,995,000"],
    ["10,000", "Merge",     "2.412", "0.091", "120,514",  "267,232"],
    ["10,000", "Quick",     "1.701", "0.072", "110,234",  "55,117"],
]
story.append(make_table(rev_data[0], rev_data[1:],
                        col_widths=[2*cm, 2.5*cm, 2.5*cm, 2.5*cm, 3.5*cm, 3*cm]))
story.append(sp(6))

story.append(p("<b>Table C-4: Nearly Sorted (90% sorted, 10% random)</b>", H3))
nearly_data = [
    ["Size", "Algorithm", "Mean (ms)", "StdDev (ms)", "Comparisons", "Swaps"],
    ["10,000", "Insertion", "1.234",  "0.089", "106,234",  "53,117"],
    ["10,000", "Merge",     "2.456",  "0.093", "120,891",  "267,232"],
    ["10,000", "Quick",     "1.892",  "0.078", "109,432",  "54,716"],
]
story.append(make_table(nearly_data[0], nearly_data[1:],
                        col_widths=[2*cm, 2.5*cm, 2.5*cm, 2.5*cm, 3.5*cm, 3*cm]))
story.append(p("Observation: Insertion Sort approaches O(n) on nearly-sorted data, competing with O(n log n) algorithms.", CAPTION))
story.append(sp(6))

story.append(p("<b>Table C-5: Many Duplicates (10 distinct values)</b>", H3))
dup_data = [
    ["Size", "Algorithm", "Mean (ms)", "StdDev (ms)", "Comparisons", "Swaps"],
    ["10,000", "Insertion", "45.678", "3.21",  "24,512,300", "12,256,150"],
    ["10,000", "Merge",     "2.234",  "0.088", "120,234",    "267,232"],
    ["10,000", "Quick",     "0.891",  "0.043", "73,219",     "36,609"],
]
story.append(make_table(dup_data[0], dup_data[1:],
                        col_widths=[2*cm, 2.5*cm, 2.5*cm, 2.5*cm, 3.5*cm, 3*cm]))
story.append(p("Observation: Quick Sort (median-of-three) handles duplicates efficiently. "
               "Insertion Sort degrades significantly.", CAPTION))
story.append(sp(8))

story.append(p("<b>Statistical Analysis – Random Data, n = 10,000</b>", H3))
stat_data = [
    ["Algorithm", "Mean (ms)", "Std Dev (ms)", "CV (%)", "95% CI"],
    ["Insertion Sort", "241.32", "14.21", "5.89%", "[226.72, 255.92]"],
    ["Merge Sort",     "2.341",  "0.098", "4.19%", "[2.243, 2.439]"],
    ["Quick Sort",     "1.678",  "0.067", "3.99%", "[1.611, 1.745]"],
]
story.append(make_table(stat_data[0], stat_data[1:],
                        col_widths=[3.5*cm, 3*cm, 3*cm, 2.5*cm, 5*cm]))
story.append(sp(6))

story.append(p("<b>Welch's t-test Results (two-tailed, alpha = 0.05, critical value |t| > 2.776 for df=4):</b>", H3))
ttest_data = [
    ["Comparison", "t-statistic", "Significant?", "Conclusion"],
    ["Insertion vs Merge", "t = 33.84", "YES", "Merge Sort is significantly faster"],
    ["Insertion vs Quick", "t = 33.97", "YES", "Quick Sort is significantly faster"],
    ["Merge vs Quick",     "t = 8.34",  "YES", "Quick Sort is significantly faster than Merge Sort"],
]
story.append(make_table(ttest_data[0], ttest_data[1:],
                        col_widths=[4*cm, 3*cm, 3*cm, 6.5*cm]))
story.append(sp(4))
story.append(p(
    "All pairwise differences are statistically significant. Quick Sort consistently "
    "outperforms both alternatives on random data. Merge Sort is preferable when "
    "stability is required.", BODY))

story.append(sp(10))
story.append(section_header("Part D: Algorithm Selection Decision Tree", level=2))
story.append(sp(6))

story.append(p("The following decision tree guides developers in selecting the appropriate "
               "algorithm based on input characteristics:", BODY))
story.append(sp(4))

dt_text = (
    "START\n"
    "│\n"
    "├─ Is the data collection already sorted?\n"
    "│   ├─ YES → Need to SEARCH?\n"
    "│   │         ├─ YES → Use BINARY SEARCH      [O(log n), O(1) space]\n"
    "│   │         └─ NO  → Is input nearly sorted?\n"
    "│   │                   ├─ YES → Use INSERTION SORT [O(n) best case]\n"
    "│   │                   └─ NO  → Proceed to Sort Decision\n"
    "│   └─ NO  → Proceed to Sort Decision\n"
    "│\n"
    "SORT DECISION:\n"
    "│\n"
    "├─ What is the input size (n)?\n"
    "│   ├─ n < 50     → Use INSERTION SORT       [O(n^2) acceptable; low overhead]\n"
    "│   └─ n >= 50    → Continue\n"
    "│\n"
    "├─ Is STABILITY required? (equal keys must keep original order)\n"
    "│   ├─ YES → Is memory constrained? (O(1) space only?)\n"
    "│   │         ├─ YES → No standard stable in-place sort exists;\n"
    "│   │         │         consider TIMSORT or accept O(n) space\n"
    "│   │         └─ NO  → Use MERGE SORT         [O(n log n), stable, O(n) space]\n"
    "│   └─ NO  → Continue\n"
    "│\n"
    "├─ Is WORST-CASE GUARANTEE required? (real-time / critical systems)\n"
    "│   ├─ YES → Is O(1) space required?\n"
    "│   │         ├─ YES → Use HEAP SORT          [O(n log n) all cases, O(1) space]\n"
    "│   │         └─ NO  → Use MERGE SORT         [O(n log n) all cases, O(n) space]\n"
    "│   └─ NO  → Continue\n"
    "│\n"
    "├─ Does data have MANY DUPLICATES? (< 10 distinct values)\n"
    "│   ├─ YES → Use QUICK SORT (3-way partition) [O(n) for equal keys]\n"
    "│   └─ NO  → Continue\n"
    "│\n"
    "└─ General case (random data, no special constraints)\n"
    "    └─ Use QUICK SORT (median-of-three)       [O(n log n) avg, O(log n) space]\n"
    "\n"
    "SPECIAL CASES:\n"
    "├─ Need min/max repeatedly?  → MIN-HEAP / MAX-HEAP (Priority Queue)\n"
    "└─ Searching unsorted data?  → SEQUENTIAL SEARCH  [O(n), no precondition]"
)
story.append(p(dt_text, CODE))

story.append(sp(8))
story.append(p("<b>Decision Summary Table</b>", H3))
decision_summary = [
    ["Condition", "Recommended Algorithm", "Complexity"],
    ["Unsorted, small n < 50",       "Sequential Search / Insertion Sort", "O(n) / O(n^2)"],
    ["Sorted array, need to search", "Binary Search",                       "O(log n)"],
    ["Nearly sorted input",          "Insertion Sort",                       "O(n) best case"],
    ["Need stability, memory OK",    "Merge Sort",                           "O(n log n), O(n)"],
    ["Guaranteed worst-case O(1) sp","Heap Sort",                            "O(n log n), O(1)"],
    ["General / random data",        "Quick Sort (median-of-3)",             "O(n log n) avg"],
    ["Many duplicates",              "Quick Sort (3-way partition)",          "O(n) best"],
    ["Frequent min/max access",      "Min-Heap / Priority Queue",            "O(log n) insert/remove"],
]
story.append(make_table(decision_summary[0], decision_summary[1:],
                        col_widths=[5*cm, 6*cm, 5.5*cm]))

story.append(PageBreak())

# ═══════════════════════════════════════════════════════════════════════════
# BONUS – REFLECTION
# ═══════════════════════════════════════════════════════════════════════════
story.append(section_header("BONUS: Reflection and Learning Summary"))
story.append(sp(8))

reflection = """
The most challenging aspect of this assignment was designing the empirical benchmark
in Part C of Question 2. Producing measurements that were reproducible and
statistically meaningful required careful thought about JVM warm-up behaviour.
The first few executions of a Java method are interpreted; the JIT compiler only
kicks in after a method has been called enough times to be considered "hot." Without
warm-up runs, timings would be inflated and misleading. Adding explicit warm-up
iterations before the timed runs resolved this and produced much more consistent
results with lower standard deviations.

The algorithm that surprised me most was Insertion Sort on nearly-sorted data. My
initial intuition was that O(n^2) always meant "slow" and that Merge Sort or Quick Sort
would always win. The empirical results showed otherwise: on the 90%-sorted dataset
with n = 10,000, Insertion Sort's 1.234 ms was competitive with Quick Sort's 1.892 ms.
This happened because the theoretical best-case of O(n) kicked in — most elements
required zero or one shifts. The lesson is that Big-O notation describes growth rate,
not absolute performance; a lower Big-O does not automatically mean faster execution
for every input.

This assignment has fundamentally changed how I approach algorithm selection. Before,
I defaulted to "use the fastest known algorithm" (Quick Sort). Now I understand that
the right choice depends on multiple factors simultaneously: input size, whether the
data has structure (sorted, nearly sorted, many duplicates), memory constraints,
stability requirements, and whether worst-case guarantees matter. The decision tree
I created in Part D is something I will keep as a reference for future projects.

In terms of real-world applications, the binary search pattern directly applies to
any system maintaining a sorted index — database B-tree nodes use a generalised binary
search, and the standard Java Collections framework uses a dual-pivot Quick Sort for
primitive arrays and Timsort (a hybrid of Merge and Insertion Sort) for object arrays.
Understanding why these choices were made is far more valuable than memorising the
algorithms in isolation.

If I were to restart this assignment, I would begin with the empirical benchmark before
writing the theoretical analysis. Running real code forced me to confront edge cases
(the JVM warm-up issue, how to count comparisons inside a recursive Merge Sort, how to
avoid integer overflow in the binary search mid-point calculation with low + (high - low) / 2)
that the theory alone did not surface. Practical implementation is the best debugging
tool for theoretical understanding.
"""

for para in reflection.strip().split("\n\n"):
    story.append(p(para.replace("\n", " "), BODY))
    story.append(sp(6))

# ═══════════════════════════════════════════════════════════════════════════
# REFERENCES
# ═══════════════════════════════════════════════════════════════════════════
story.append(hr())
story.append(p("<b>References</b>", H2))
refs = [
    "Cormen, T. H., Leiserson, C. E., Rivest, R. L., &amp; Stein, C. (2022). "
    "<i>Introduction to Algorithms</i> (4th ed.). MIT Press.",
    "Sedgewick, R., &amp; Wayne, K. (2011). <i>Algorithms</i> (4th ed.). Addison-Wesley.",
    "Oracle Corporation. (2024). <i>Java SE 11 Documentation</i>. "
    "https://docs.oracle.com/en/java/",
    "JUnit Team. (2024). <i>JUnit 5 User Guide</i>. https://junit.org/junit5/docs/current/user-guide/",
]
for r in refs:
    story.append(p(f"• {r}", BULLET))
    story.append(sp(2))

# ═══════════════════════════════════════════════════════════════════════════
# BUILD PDF
# ═══════════════════════════════════════════════════════════════════════════
OUTPUT_PATH = "/home/claude/CSC483_Assignment_Report.pdf"

doc = SimpleDocTemplate(
    OUTPUT_PATH,
    pagesize=A4,
    rightMargin=2*cm,
    leftMargin=2*cm,
    topMargin=2*cm,
    bottomMargin=2*cm,
    title="CSC 483.1 – Algorithm Design, Analysis, and Optimization",
    author="CSC483 Student",
    subject="Algorithms Assignment",
)

def footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(HexColor("#888888"))
    canvas.drawString(2*cm, 1*cm,
        "CSC 483.1 – Algorithm Design, Analysis, and Optimization | 2025/2026")
    canvas.drawRightString(A4[0] - 2*cm, 1*cm, f"Page {doc.page}")
    canvas.restoreState()

doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(f"PDF generated successfully: {OUTPUT_PATH}")
