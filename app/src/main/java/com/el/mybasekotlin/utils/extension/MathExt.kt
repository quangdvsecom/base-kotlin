package com.el.mybasekotlin.utils.extension


fun String.isNumericInt(): Boolean {
    return this.toIntOrNull() != null
}

fun String.isNumericLong(): Boolean {
    return this.toLongOrNull() != null
}

fun String?.get(): String {
    return if (!this.isNullOrEmpty()) "" else this.toString()
}

fun String.areAllNumbers(sizeOfNumber: Int): Boolean {
    val numbers = this.split(",")

    for (number in numbers) {
        val dataNumber = number.removeAllSpace()
        if (!dataNumber?.isEmpty()!! && dataNumber.toIntOrNull() == null) {
            return false
        }
        if (dataNumber.length > sizeOfNumber)
            return false
    }

    return true
}
