package com.el.mybasekotlin.ui.fragment

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.data.state.DataState
import com.el.mybasekotlin.data.state.getSuccessDataOrNull
import com.el.mybasekotlin.databinding.MainFragmentBinding
import com.el.mybasekotlin.utils.extension.collectIn
import com.el.mybasekotlin.utils.extension.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */

@AndroidEntryPoint
class MainFragment : BaseFragment<MainFragmentBinding>(MainFragmentBinding::inflate) {
    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
    public val mainViewModel: MainViewModel by viewModels()
    override fun initDataBeforeCreateView() {
    }

    override fun init() {

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }


        binding.apply {
            btClickToSplash.setOnClickListener { navigateTo(R.id.action_to_splash) }
            btClickLogin.setOnClickListener {
                mainViewModel.getSettingApp()
                mainViewModel.getConfigApp()

//                navigateTo(
//                    R.id.action_to_login,
//                    Bundle().apply { putExtrasBundle("data" to "userName") },
//                    null,
//                    false
//                )
//                val username = arguments?.getString("USERNAME")
            }
            btClickTest.setOnClickListener { navigateTo(R.id.action_to_A_screen) }
            btClickDownload.setOnClickListener {
                if (!handlePermission(permissions)) {
                    requestPermissionsLauncher.launch(permissions)
//                    requestPermission(permissions, callBack = {
//                        if (it.size == 0||it.isEmpty()) {
//                            //TODO action user
//                        } else {
//                            //Show dialog permission again or popup explain the reason
//                            Timber.d("Show lý do xin quền lại")
//                        }
//                    })
                } else {
                    Toast.makeText(
                        requireContext(),
                        "All permissions already granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    suspend fun fetchData(): String {
        delay(1000) // Giả lập API call
        return "Dữ liệu từ server"
    }

    // Chuyển thành Flow
    fun fetchDataFlow(): Flow<String> = flow {
        emit(fetchData()) // Gọi suspend function và emit kết quả
    }

    override fun initObserver() {

// Sử dụng Flow
        CoroutineScope(Dispatchers.Main).launch {
            fetchDataFlow().collect { data ->
                println("Nhận dữ liệu: $data")
            }
        }


//        val scope = CoroutineScope(Dispatchers.Main)
        val scope2 = lifecycleScope.launch {

        }
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {


        }

        callBackPermissionData.launchAndCollectIn(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Timber.d("Có permission nao do khong duoc chap nhan")
                for (item in it) {
                    Timber.d("QuangDV initObserver permission: ${item.name} ${item.state}")

                }
            }
        }
        mainViewModel.apply {
            combinedFlow.collectIn(this@MainFragment) {
                when (it.first) {
                    DataState.Empty -> {
                        Timber.d("Empty")

                    }

                    is DataState.Error -> {

                    }

                    DataState.Loading -> {

                    }

                    is DataState.Success -> {
    // val settings2 = (it.first as DataState.Success<MutableList<Setting>>).data

                        val settings = it.first.getSuccessDataOrNull()
                        settings?.let {
                            Timber.d("First Data: ${settings[0]}")
                        }
                    }
                }
                when (it.second) {
                    DataState.Empty -> {
                        Timber.d("Empty")

                    }

                    is DataState.Error -> {

                    }

                    DataState.Loading -> {

                    }

                    is DataState.Success -> {


                        val configApp = it.second.getSuccessDataOrNull()
                        configApp?.let {
                            Timber.d("Second Data: $configApp")
                        }
                    }
                }
            }
            listSetting.collectIn(this@MainFragment) { it ->
                when (it) {
                    DataState.Empty -> {
                        Timber.d("Empty")
                    }

                    is DataState.Error -> {}
                    is DataState.Loading -> {

                    }

                    is DataState.Success -> {
                        Timber.d("MainFragment data setting : ${it.data}")
                    }
                }
            }

        }

    }
}