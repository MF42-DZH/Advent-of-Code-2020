package net.nergi.utils

@Throws(ArrayIndexOutOfBoundsException::class)
fun <T> Iterable<T>.tail(): Iterable<T> {
    if (this.none()) {
        throw ArrayIndexOutOfBoundsException("Empty iterable.")
    } else {
        return this.drop(1)
    }
}

fun String.isInteger(): Boolean = this.all(Char::isDigit)

@Throws(ArrayIndexOutOfBoundsException::class)
fun <T> Iterable<T>.fold1(operation: (T, T) -> T): T {
    if (this.none()) {
        throw ArrayIndexOutOfBoundsException("Empty iterable.")
    } else {
        return this.tail().fold(this.first(), operation)
    }
}

inline fun <reified T> scanLUntil(func: (T, T) -> T, init: T, iterable: Iterable<T>, pred: (T) -> Boolean): List<T> {
    val result = mutableListOf(init)
    var acc = init
    for (item in iterable) {
        acc = func(acc, item)
        result.add(acc)

        if (pred(acc)) break
    }

    return result
}

@Throws(ArrayIndexOutOfBoundsException::class)
inline fun <reified T> scanL1Until(func: (T, T) -> T, iterable: Iterable<T>, pred: (T) -> Boolean): List<T> {
    if (iterable.none()) {
        throw ArrayIndexOutOfBoundsException("Empty iterable.")
    } else {
        return scanLUntil(func, iterable.first(), iterable.tail(), pred)
    }
}

/**
 * pass - acts like Python's pass keyword
 */
val pass = Unit
