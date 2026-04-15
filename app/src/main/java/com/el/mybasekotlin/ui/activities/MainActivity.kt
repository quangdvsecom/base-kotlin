package com.el.mybasekotlin.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.network.NetworkStatusProvider
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.data.network.download.DownloadWorker
import com.el.mybasekotlin.data.state.ErrorAction
import com.el.mybasekotlin.helpers.TestMessage
import com.el.mybasekotlin.helpers.flowbus.collectFlowBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"
    private lateinit var navController: NavController
    private lateinit var broadcastErrorReceiver: BroadcastReceiver
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var broadcastNewNotice: BroadcastReceiver
    private val networkStatusProvider by lazy {
        NetworkStatusProvider(getSystemService(ConnectivityManager::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        onNewIntent(intent)
    }

    private fun init() {

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.container
        ) as NavHostFragment
        navController = navHostFragment.navController
        setupNetworkObserver()
        listenEvent()
        initBroadcastError()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastErrorReceiver)
        lifecycle.removeObserver(networkStatusProvider)
    }

    override fun onStart() {
        super.onStart()
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(broadcastReceiver, IntentFilter(FIREBASE_NEW_TOKEN))
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(broadcastNewNotice, IntentFilter(FIREBASE_NEW_NOTICE))

        val filter = IntentFilter()
        filter.addAction(ErrorAction.ACTION_FORCE_LOGOUT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastErrorReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastErrorReceiver, filter,RECEIVER_NOT_EXPORTED)
        }
    }

    /**
     * init event from flow bus like event bus
     * For example push event: busEvent(SearchEvent(1,"value"))
     */
    private fun listenEvent() {
        collectFlowBus<TestMessage> {
            Timber.d("MainActivity ${it.msg}")
//            when (it.event) {
//                1->{
//
//
//                }
//            }
        }

    }

    /**
     * Network listener
     * - Listener to network change, and show something UI if need
     */
    private fun setupNetworkObserver() {
        lifecycle.addObserver(networkStatusProvider)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    networkStatusProvider.status.collect {
                        Timber.d("Is Internet On? : $it")
//                        binding.networkStatus.visibleIf(!it)
                    }
                }
            }
        }
    }

    //init error
    private fun initBroadcastError() {
        broadcastErrorReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action.equals(ErrorAction.ACTION_FORCE_LOGOUT)) {
                    Timber.d("$TAG Action force logout and login by device ID")
                    AppPreferences.clearData()
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastNewNotice)
    }

    /**
     * Notice
     */
//    private fun listenerFCMChange() {
//        Timber.d("$TAG initFCMToken ")
//        broadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                //TODO if is new token then call api register new token from sharePreference
//                Timber.d("$TAG is new token")
//                val token = AppPreferences.fcmToken
//                if (token.isNotEmpty()) {
//                    registerToken(AppPreferences.fcmToken)
//                }
//            }
//        }
//    }
//
//    //get token from firebase
//    private fun checkFCMToken() {
//        Timber.d("$TAG checkFCMToken ")
//        FirebaseMessaging.getInstance().token
//            .addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Timber.tag(TAG).w(task.exception, "Fetching FCM registration token failed")
//                    return@addOnCompleteListener
//                }
//                // Get new FCM registration token
//                val token = task.result
//                val lastFcmToken = AppPreferences.fcmToken
//                Timber.d("$TAG checkFCMToken token =>> $token")
//                Timber.d("$TAG checkFCMToken lastFcmToken =>> $lastFcmToken")
//                if (token.isNotEmpty() && token != lastFcmToken
//                ) {
//                    Timber.d("$TAG checkFCMToken  =>> call api register token")
//                    AppPreferences.fcmToken = token
//                    //TODO call api register new token
//                    registerToken(token)
//                } else {
//                    Timber.d("$TAG checkFCMToken  =>> don't call api register token ")
//                }
//            }
//    }
//
//    private fun registerToken(fcmToken: String) {
//        noticeViewModel.registerFCM(
//            BodyNoticeParam(
//                deviceID = getAndroidId(this),
//                deviceToken = fcmToken,
//                sessionID = getAndroidId(this),
////                appVersion = BuildConfig.VERSION_NAME
//            )
//        )
//    }

    /*
     * Download test
     */
//    private var workInfoLiveData: LiveData<WorkInfo?> ?= null

//    private val mutableWorkInfoLiveData = MutableLiveData<WorkInfo?>()
    private var workInfoLiveData: LiveData<WorkInfo?>? = null
    private fun startDownload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Yêu cầu kết nối mạng
            .build()

        val inputData = workDataOf(
            DownloadWorker.KEY_FILE_URL to "https://example.com/file.zip",
            DownloadWorker.KEY_FILE_NAME to "file.zip"
        )

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
//        workInfoLiveData=mutableWorkInfoLiveData
        WorkManager.getInstance(this).enqueue(workRequest)
        workInfoLiveData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id)
        workInfoLiveData!!.observe(this) { workInfo ->
            when (workInfo?.state) {
                WorkInfo.State.ENQUEUED -> {
                    // Worker đã được lên lịch
                }
                WorkInfo.State.RUNNING -> {
                    // Worker đang chạy
                }
                WorkInfo.State.SUCCEEDED -> {
                    // Worker đã hoàn thành thành công
                    val result = workInfo?.outputData?.getString("result_key")
                    // Xử lý kết quả
//                    mutableWorkInfoLiveData.postValue(workInfo)
                }
                WorkInfo.State.FAILED -> {
                    // Worker đã thất bại
                }
                WorkInfo.State.CANCELLED -> {
                    // Worker đã bị hủy
                }
                else -> {
                    // Các trạng thái khác
                }
            }
        }
    }
}