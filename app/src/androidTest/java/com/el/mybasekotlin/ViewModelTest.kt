package com.el.mybasekotlin

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.response.BaseDataResponse
import com.el.mybasekotlin.providertest.DispatcherProvider
import com.el.mybasekotlin.providertest.TestDispatcherProvider
import com.el.mybasekotlin.ui.fragment.MainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by ElChuanmen on 2/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 * https://developer.android.com/codelabs/basic-android-kotlin-compose-test-viewmodel#3
 * https://medium.com/@deepak.patidark93/a-complete-guide-to-mvvm-and-viewmodel-testing-in-android-hilt-junit-and-mockito-explained-df54324b8dca
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ViewModelTest() {
    @Mock
    private lateinit var apiHelper: ApiHelper
    private lateinit var viewModel: MainViewModel
    private lateinit var application: Application
    private lateinit var gson: Gson

    private lateinit var dispatcherProvider: DispatcherProvider


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        dispatcherProvider = TestDispatcherProvider()
//        dispatcherProvider.main
        gson = Gson()
        application = ApplicationProvider.getApplicationContext() // Get application context
        viewModel = MainViewModel(application, apiHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset lại Dispatcher về mặc định sau khi test
    }

    @Test
    fun testCallApiSetting() = runTest {


        val dataTest =
            "{\n" + "\"status\": 1,\n" + "\"error_code\": 0,\n" + "\"message\": \"success\",\n" + "\"data\": [\n" + "{\n" + "\"event_id\": 0,\n" + "\"data\": {\n" + "\"main\": {\n" + "\"id\": 1,\n" + "\"splash_screen\": \"https://event-tech.mediacdn.vn/background.jpg\",\n" + "\"background\": \"https://event-tech.mediacdn.vn/background.jpg\",\n" + "\"logo\": \"https://event-tech.mediacdn.vn/hybrid-logo-201.png\",\n" + "\"logo_footer\": \"https://event-tech.mediacdn.vn/vccorp-mwebp\",\n" + "\"hotline\": \"0888000631\",\n" + "\"email\": \"phuongdoanquynh@adsponsor.vn\",\n" + "\"address\": \"Tầng 20, Center Building Hapulico Complex, Số 1 Nguyễn Huy Tưởng, Thanh Xuân, Hà Nội\",\n" + "\"primary_color\": null,\n" + "\"secondary_color\": null,\n" + "\"highLightColor\": \"#10b9f0\",\n" + "\"textHighLightColor\": \"#10b9f0\",\n" + "\"textDefaultColor\": \"#ffffff\",\n" + "\"textListInfoColor\": \"#b5b5b5\",\n" + "\"variantIconColor\": \"#fff265\",\n" + "\"placeholderInputLogin\": \"Nhập Email/Số điện thoại\",\n" + "\"hintColor\": \"#ffffff\",\n" + "\"dialogColor\": \"#6c2578\",\n" + "\"bgTab\": null,\n" + "\"tabActiveColor\": \"#ff9163\",\n" + "\"tabInactiveColor\": \"#ffffff00\",\n" + "\"time_otp_expire\": 180,\n" + "\"win_rate_spin\": 2,\n" + "\"login_method\": 2,\n" + "\"zalo_login_template_id\": 398727,\n" + "\"zalo_ticket_template_id\": 398818,\n" + "\"kam_gift_zalo_login_template_id\": 397999,\n" + "\"kam_gift_zalo_ticket_template_id\": 398806,\n" + "\"enable_recaptcha_login\": 0,\n" + "\"enable_recaptcha_ticket\": 0,\n" + "\"enable_provider_login\": 1,\n" + "\"enable_sms_zalo_not_existed\": 1,\n" + "\"created_at\": \"2025-02-16T16:00:03.000000Z\",\n" + "\"updated_at\": \"2025-02-16T16:00:03.000000Z\"\n" + "}\n" + "}\n" + "}\n" + "]\n" + "}"
        val type = object : TypeToken<BaseDataResponse<MutableList<Setting>>>() {}.type
        val baseResponseSetting: BaseDataResponse<MutableList<Setting>> =
            gson.fromJson(dataTest, type)
//        println("baseResponseSetting: $dataTest")


        Mockito.`when`(apiHelper.getSetting()).thenReturn(flow {
            emit(
                baseResponseSetting
            )
        })
        viewModel.getSettingApp()

//        delay(200)
        advanceUntilIdle()

//        val collectedValues = mutableListOf<DataState<MutableList<Setting>>>()
//        val job = launch {
//            viewModel.listSetting.collect { state ->
//                when (state) {
//                    DataState.Empty ->    println("Collected Empty: ")
//                    is DataState.Error ->    println("Collected Error: ")
//                    DataState.Loading ->    println("Collected Loading: ")
//                    is DataState.Success ->    println("Collected Success:")
//                }
//            }
//        }
//        job.cancel() // Dừng collect sau khi test

        println("Collected values first: ${ viewModel.listSetting.first()}")
        println("Collected values first2: ${ viewModel.listSetting.value}")
//        println("result ViewModelTest: $result")
//        assertNotNull(result)
    }


}