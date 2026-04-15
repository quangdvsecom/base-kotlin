package com.el.mybasekotlin.utils.extension

/**
 * Created by ElChuanmen on 3/12/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
//The Method to help calculator time of method
inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    val data:Boolean =false
    return System.currentTimeMillis() - start
}
//fun main() {
//    val time = measureTimeMillis {
//        // Đoạn mã cần đo thời gian
//        for (i in 1..1000000) {
//            // ...
//        }
//    }
//    println("Thời gian thực thi: $time ms")
//}
inline fun <reified T> Any.isType(): Boolean {
    return this is T
}