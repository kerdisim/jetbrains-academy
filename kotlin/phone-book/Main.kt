package phonebook

import java.io.File
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

/**
 * The [duration] is the amount of time elapsed to search for the items and the [found]
 * is the count of items that were found.
 */
data class SearchResult(val duration: Long, val found: Int)

/**
 * The [duration] is the time it took to sort the collection and [completed] determines
 * if the sorting was completed.
 */
data class SortingResult(val duration: Long, val completed: Boolean)

/**
 * Swaps the [i]th and [j]th element of the array [a].
 */
fun <T> swap(a: Array<T>, i: Int, j: Int) {
    val temp = a[i]
    a[i] = a[j]
    a[j] = temp
}

fun partition(a: Array<String>, lo: Int, hi: Int): Int {
    val pivot = a[hi]
    var i = lo

    for (j in lo..hi) {
        if (a[j] < pivot) {
            swap(a, i, j)
            i++
        }
    }
    swap(a, i, hi)

    return i
}

/**
 * Sorts [a] using quick sort.
 */
fun quickSort(a: Array<String>, lo: Int = 0, hi: Int = a.lastIndex) {
    if (lo < hi) {
        val p = partition(a, lo, hi)
        quickSort(a, lo, p - 1)
        quickSort(a, p + 1, hi)
    }
}

/**
 * Searches if the [target] is included in the [a] using binary search.
 */
fun binarySearch(a: Array<String>, target: String): Boolean {
    var lo = 0
    var hi = a.lastIndex

    while (lo <= hi) {
        val middle = floor((lo + hi) / 2.0).toInt()

        when {
            a[middle] < target -> {
                lo = middle + 1
            }
            a[middle] > target -> {
                hi = middle - 1
            }
            else -> {
                return true
            }
        }
    }

    return false
}

/**
 * Sorts the [a] using bubble sort. It stops sorting when the [durationLimit] is exceeded.
 * @return the duration of the sorting or null if the [durationLimit] was exceeded.
 */
fun bubbleSort(a: Array<String>, durationLimit: Long): SortingResult {
    var swapped: Boolean

    val start = System.currentTimeMillis()

    do {
        val now = System.currentTimeMillis()
        if (now - start > durationLimit) {
            return SortingResult(now - start, false)
        }

        swapped = false

        for (i in 0 until a.lastIndex) {
            if (a[i] > a[i + 1]) {
                swap(a, i, i + 1)
                swapped = true
            }
        }

    } while (swapped)

    return SortingResult(System.currentTimeMillis() - start, true)
}

/**
 * Searches if the [target] is included in the [a] using jump search.
 */
fun jumpSearch(a: Array<String>, target: String): Boolean {
    val n = a.size

    val jumpSize = floor(sqrt(n.toDouble())).toInt()
    var jump = jumpSize
    var cursor = 0

    while (a[min(jump, n) - 1] < target) {
        cursor = jump
        jump += jumpSize
        if (cursor >= n) return false
    }

    while (a[cursor] < target) {
        cursor++
        if (cursor == min(jump, n)) return false
    }

    return a[cursor] == target
}

/**
 * Searches if the [target] is included in the [a] using linear search.
 */
fun linearSearch(a: Array<String>, target: String): Boolean {
    for (entry in a) {
        if (target == entry) {
            return true
        }
    }
    return false
}

/**
 * Returns the [SearchResult] for the [itemsToSearchFor] in the [itemsToSearchIn] using linear search.
 */
fun linearSearchItems(
    itemsToSearchIn: Array<String>,
    itemsToSearchFor: Array<String>
): SearchResult {
    var found = 0
    val start = System.currentTimeMillis()

    for (item in itemsToSearchFor) {
        if (linearSearch(itemsToSearchIn, item)) {
            found++
        }
    }

    return SearchResult(System.currentTimeMillis() - start, found)
}

/**
 * Returns the [SearchResult] for the [itemsToSearchFor] in the [itemsToSearchIn] using jump search.
 */
fun jumpSearchItems(itemsToSearchIn: Array<String>, itemsToSearchFor: Array<String>): SearchResult {
    var found = 0
    val start = System.currentTimeMillis()

    for (item in itemsToSearchFor) {
        if (jumpSearch(itemsToSearchIn, item)) {
            found++
        }
    }

    return SearchResult(System.currentTimeMillis() - start, found)
}

/**
 * Returns the [SearchResult] for the [itemsToSearchFor] in the [itemsToSearchIn] using jump search.
 */
fun binarySearchItems(
    itemsToSearchIn: Array<String>,
    itemsToSearchFor: Array<String>
): SearchResult {
    var found = 0
    val start = System.currentTimeMillis()

    for (item in itemsToSearchFor) {
        if (binarySearch(itemsToSearchIn, item)) {
            found++
        }
    }

    return SearchResult(System.currentTimeMillis() - start, found)
}

fun formatDuration(duration: Long): String {
    return String.format("%1\$tM min. %1\$tS sec. %1\$tL ms.", duration)
}

fun main() {
    val contactsToSearchIn = File("/Users/simi/Downloads/directory.txt")
        .readLines()
        .map { it.split(" ", ignoreCase = false, limit = 2) }
    val namesToSearchIn = contactsToSearchIn.map { it[1] }.toTypedArray()
    val namesToSearchFor = File("/Users/simi/Downloads/find.txt").readLines().toTypedArray()


    println("Start searching (linear search)...")
    val linearSearchResult = linearSearchItems(namesToSearchIn, namesToSearchFor)
    println(
        "Found ${linearSearchResult.found} / ${namesToSearchFor.size} entries. " +
                "Time taken: ${formatDuration(linearSearchResult.duration)}"
    )

    println("Start searching (bubble sort + jump search)...")
    val bubbleSorted = namesToSearchIn.copyOf()
    val bubbleSortResult = bubbleSort(bubbleSorted, linearSearchResult.duration * 10)

    if (bubbleSortResult.completed) {
        val jumpSearchResult = jumpSearchItems(bubbleSorted, namesToSearchFor)

        println(
            "Found ${jumpSearchResult.found} / ${namesToSearchFor.size} entries. Time taken: ${
                formatDuration(
                    bubbleSortResult.duration + jumpSearchResult.duration
                )
            }"
        )
        println("Sorting time: ${formatDuration(bubbleSortResult.duration)}")
        println("Searching time: ${formatDuration(jumpSearchResult.duration)}")
    } else {
        val linearSearchResult2 = linearSearchItems(bubbleSorted, namesToSearchFor)
        println(
            "Found ${linearSearchResult2.found} / ${namesToSearchFor.size} entries. Time taken: ${
                formatDuration(
                    bubbleSortResult.duration + linearSearchResult2.duration
                )
            }"
        )
        println("Sorting time: ${formatDuration(bubbleSortResult.duration)} - STOPPED, moved to linear search")
        println("Searching time: ${formatDuration(linearSearchResult2.duration)}")
    }

    println("Start searching (quick sort + binary search)...")
    val sortStart = System.currentTimeMillis()
    quickSort(namesToSearchIn)
    val sortDuration = System.currentTimeMillis() - sortStart

    val binarySearchResult = binarySearchItems(namesToSearchIn, namesToSearchFor)

    println(
        "Found ${binarySearchResult.found} / ${namesToSearchFor.size} entries. Time taken: ${
            formatDuration(
                sortDuration + binarySearchResult.duration
            )
        }"
    )
    println("Sorting time: ${formatDuration(sortDuration)}")
    println("Searching time: ${formatDuration(binarySearchResult.duration)}")

    println("Start searching (hash table)...")

    val creatingStart = System.currentTimeMillis()
    val table = mutableMapOf<String, String>()

    for (item in contactsToSearchIn) {
        val (number, name) = item
        table[name] = number
    }
    val creatingDuration = System.currentTimeMillis() - creatingStart

    val searchingStart = System.currentTimeMillis()
    var count = 0

    for (name in namesToSearchFor) {
        if (table[name] != null) {
            count++
        }
    }

    val searchingDuration = System.currentTimeMillis() - searchingStart

    println(
        "Found $count / ${namesToSearchFor.size} entries. Time take: ${
            formatDuration(
                creatingDuration + searchingDuration
            )
        }"
    )
    println("Creating time: ${formatDuration(creatingDuration)}")
    println("Searching time: ${formatDuration(searchingDuration)}")
}
