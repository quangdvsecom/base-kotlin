package com.el.mybasekotlin.ui.fragment

import androidx.lifecycle.lifecycleScope
import com.el.mybasekotlin.BuildConfig
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.compose.ComposeActivity
import com.el.mybasekotlin.databinding.SplashScreenBinding
import com.el.mybasekotlin.utils.decryptData
import com.el.mybasekotlin.utils.encryptData
import com.el.mybasekotlin.utils.extension.openActivity
import com.el.mybasekotlin.utils.getOrCreateSecretKey
import com.el.mybasekotlin.utils.saveEncryptedApiKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.checkerframework.common.returnsreceiver.qual.This
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.system.measureTimeMillis

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@AndroidEntryPoint
class SplashScreenFragment : BaseFragment<SplashScreenBinding>(SplashScreenBinding::inflate) {

    private external fun stringFromJNI(): String

    //    private external fun stringFromJNI(): String
//    private external fun encryptDecrypt(): String
    private external fun decryptDecrypt(): String
    external fun stringPrivate(): String
    override fun initDataBeforeCreateView() {
    }

    override fun init() {
        binding.btClick.setOnClickListener {
//            requireActivity().openActivity<ComposeActivity>("value" to true )

            navigateTo(R.id.action_to_home, null, R.id.fragmentSplash, true)
////            navigateTo(R.id.action_to_main)
//
//            val secretKey = getOrCreateSecretKey()  // Lấy khóa từ Keystore
//            val encryptedApiKey = encryptData(BuildConfig.API_KEY, secretKey)  // Mã hóa API Key
//            println("Decrypted encryptedApiKey API Key: $encryptedApiKey")  // Ki
//            saveEncryptedApiKey(requireActivity(), encryptedApiKey.toString())  // Lưu vào SharedPreferences
//
//// Khi cần sử dụng API Key
//            val secretKey2 = getOrCreateSecretKey()
//            val decryptedApiKey = decryptData(encryptedApiKey.toString(), secretKey2)
//            println("Decrypted API Key: $decryptedApiKey")  // Ki
//
//            println("stringFromJNI : ${stringFromJNI()}")  // Ki
////            println("encryptDecrypt : ${encryptDecrypt()}")  // Ki
//            println("decryptDecrypt : ${decryptDecrypt()}")  // Ki
        }

    }



    override fun initObserver() {
        //        flowOfNumbers().launchAndCollectIn(viewLifecycleOwner) {
//            println("QuangDV initObserver: $it")
//        }

//
//        val scope = CoroutineScope(Dispatchers.IO)
//        scope.launch {
//            // ...
//            doSomething()
//            flowOfNumbers().collect{
//                println("QuangDV main: $it")
//            }
//        }
//        lifecycleScope.launch {
//            doSomething()
//        }
//        launchAndRepeatStarted({
//            flowOfNumbers().collect{
//                println("QuangDV launchAndRepeatStarted: $it")
//            }
//
//        })
//        flowOfNumbers().collectIn(this@SplashScreenFragment) {
//            println("QuangDV collectIn: $it")
//        }
//        testAsynchronous()
        lifecycleScope.launch { fetchUsers() }


//        main()

    }

    private fun main() = runBlocking {
        launch {
            delay(1000L)
            println("World!")
//            flowOfNumbers()
            doSomething()
        }
        println("Hello,")


    }

    private fun flowOfNumbers(): Flow<Int> = flow {
        emit(1)
        delay(1000L)
        emit(2)
        delay(500L)
        emit(3)
    }

    private suspend fun doSomething() {
        println("Doing something...")
        delay(500L)
        flowOfNumbers().collect {
            println("QuangDV doSomething: $it")
        }

    }

    //    Dispatchers.IO: Dùng cho các tác vụ IO như đọc/ghi file, gọi API.
//    Dispatchers.Default: Dùng cho các tác vụ nặng về CPU.
//    Dispatchers.Main: Dùng cho UI (chỉ trên Android hoặc các nền tảng hỗ trợ).

    suspend fun fetchDataFromApi(): String {
        delay(1000L) // Giả lập thời gian lấy dữ liệu từ API
        return "API Data"
    }

    suspend fun processData(data: String): String {
        delay(500L) // Giả lập thời gian xử lý dữ liệu
        return "Processed: $data"
    }

    private fun testAsynchronous() {
        val scope = CoroutineScope(Dispatchers.IO)
        var text1 = ""
        scope.launch {
            delay(1000L) // Giả lập tác vụ mất 1 giây
            println("Task 1 completed")
            text1 = "Task 1 completed"
        }
        var text2 = ""
        scope.launch {
            delay(500L) // Giả lập tác vụ mất 0.5 giây
            println("Task 2 completed")
            text2 = "Task 2 completed"
        }

        scope.launch {
            val result = async {
                val apiData = fetchDataFromApi()
                processData(apiData)
            }
            println("Result: ${result.await()}")
        }
    }
    //


    suspend fun fetchUser1(): String {
        delay(1000L) // Giả lập gọi API mất 1 giây
        return "User 1 Data"
    }

    suspend fun fetchUser2(): String {
        delay(1500L) // Giả lập gọi API mất 1.5 giây
        return "User 2 Data"
    }

    suspend fun fetchUsers() {
        // Chạy song song hai API
        lifecycleScope.launch {
            val user1Deferred = async { fetchUser1() }
            val user2Deferred = async { fetchUser2() }

            // Đợi cả hai API hoàn thành
            val user1Result = user1Deferred.await()
            val user2Result = user2Deferred.await()
            println("data : $user1Result , $user2Result")

        }


//        return user1Result to user2Result

    }

    fun main2() = runBlocking {
        val time = measureTimeMillis {
            val task1 = async {
                delay(1000)
                "Task 1 done!"
            }
            val task2 = async {
                delay(1000)
                "Task 2 done!"
            }
            println(task1.await()) // Lấy kết quả song song
            println(task2.await())
        }
        println("Total time: $time ms")
    }

}