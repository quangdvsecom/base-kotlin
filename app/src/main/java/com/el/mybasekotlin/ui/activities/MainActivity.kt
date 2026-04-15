package com.el.mybasekotlin.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.network.NetworkStatusProvider
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.data.state.ErrorAction
import com.el.mybasekotlin.helpers.TestMessage
import com.el.mybasekotlin.helpers.flowbus.collectFlowBus
import com.el.mybasekotlin.ui.fragment.MainViewModel
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
    private val mainViewModel : MainViewModel by viewModels()
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
        mainViewModel.getAllDataGame()
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



}