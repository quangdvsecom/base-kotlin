package com.el.mybasekotlin

import com.el.mybasekotlin.ui.MathHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import kotlin.random.Random

/**
 * Created by ElChuanmen on 2/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
class MathHelperTest {
    private val mathHelper = MathHelper()




    @Test
    fun testMath1() {
        val result = mathHelper.add(3, 5)
        assertEquals(true, result) // Kiểm tra kết quả
    } @Test
    fun testMath() {
        val result = mathHelper.add(2, 5)
        assertEquals(true, result) // Kiểm tra kết quả
    }
    @Test
    fun testMath2() {
        val a = Random.nextInt(1, 100)  // Random số từ 1 đến 100
        val b = Random.nextInt(1, 100)
        val result = mathHelper.add(a, b)

        assertEquals(true, result) // Kiểm tra kết quả
    }
    @Test
    fun repeatedTest() {
        repeat(10) {
            val a = Random.nextInt(1, 100)  // Random số từ 1 đến 100
            val b = Random.nextInt(1, 100)
            val result = mathHelper.add(a, b)

//            println("Test case: a = $a, b = $b, expected = true got = $result")
//            assertEquals(a + b, result)

            assertEquals("Test failed for a = $a, b = $b. Expected: true, but got: $result", true, result)
            assertEquals(true, result) // Kiểm tra kết quả
        }
    }
 @Test
    fun repeatedTest2() {
     val errors = mutableListOf<String>() // Lưu các lỗi xảy ra
     val success = mutableListOf<String>() // Lưu các lỗi xảy ra

     repeat(10) {
         val a = Random.nextInt(1, 100)
         val b = Random.nextInt(1, 100)
         val result = mathHelper.add(a, b)

         if (!result) {
             errors.add("Test failed for a = $a, b = $b. Expected: ${a + b}, but got: $result")
         } else {success.add("Test success for a = $a, b = $b. Expected: ${a + b}, but got: $result")
                 println("✅ Test success PASSED U+2705 : a=$a, b=$b, expected= true, got=$result")
         }
     }

     if (errors.isNotEmpty()) {
         fail(errors.joinToString("\n")) // Chỉ báo lỗi sau khi test xong
     }

    }

    companion object {
        @JvmStatic
        fun randomNumbers(): List<Array<Int>> {
            return List(5) { arrayOf(Random.nextInt(1, 100), Random.nextInt(1, 100)) }
        }
    }

//    @ParameterizedTest
//    @MethodSource("randomNumbers")
//    fun randomTest(a: Int, b: Int) {
//        val expected = a + b
//        val result = mathHelper.add(a, b)
//
//        assertEquals(expected, result)
//    }


}