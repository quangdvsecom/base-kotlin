package com.el.mybasekotlin.base

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.el.mybasekotlin.BuildConfig
import com.el.mybasekotlin.R
import com.el.mybasekotlin.data.local.AppPreferences
import com.el.mybasekotlin.data.local.database.DatabaseHelper
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.response.BaseDataResponse
import com.el.mybasekotlin.data.state.ErrorAction
import com.el.mybasekotlin.data.state.DataState
import com.el.mybasekotlin.data.state.ErrorCode
import com.el.mybasekotlin.data.state.SingleLiveEvent
import com.el.mybasekotlin.di.GlobalData
import com.el.mybasekotlin.utils.extension.getOrBlank
import com.el.mybasekotlin.utils.extension.mapErrorMessage
import com.google.gson.Gson
import com.el.mybasekotlin.data.network.interceptor.NetworkCheckerInterceptor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */


abstract class BaseViewModel(
    open val app: Application,
    private val apiHelper: ApiHelper,
) : ViewModel() {
    protected val tag: String = javaClass.simpleName
    var job: Job? = null

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var databaseHelper: DatabaseHelper

    @Inject
    lateinit var globalData: GlobalData
    protected val sharedPreferences = AppPreferences
    protected var jobCall: Job? = null
    val onError = SingleLiveEvent<Throwable>()
    private val _isLoading = MutableStateFlow(mutableStateOf(false))
    val isLoading = _isLoading.asStateFlow()

    private val _forceLogout = MutableStateFlow<Boolean>(false)
    val forceLogout: StateFlow<Boolean> = _forceLogout.asStateFlow()

    protected fun launchJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + createErrorHandler(withOutError = false), start, block)

    /**
     * This launchJob use to call api with response return unstructured
     * Handle exception in @exceptionHandler
     */
    protected fun launchJobCustom(
        exceptionHandler: CoroutineExceptionHandler,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + exceptionHandler, start, block)


    protected fun launchWithoutError(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + createErrorHandler(withOutError = true), start, block)

    protected fun launchLoadingJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context + createErrorHandler(withOutError = false), start) {
        _isLoading.value = mutableStateOf(true)
        try {
            block()
        } finally {
            _isLoading.value = mutableStateOf(false)
        }
    }

    private fun createErrorHandler(withOutError: Boolean) =
        CoroutineExceptionHandler { _, throwable ->
            if (BuildConfig.DEBUG) {
                throwable.printStackTrace()
            }
            if (throwable !is CancellationException) {
                if (withOutError) {
                    throwable.stackTrace
                } else {
                    onError.postCall(throwable)
                }
            }
        }

    companion object {
        const val SUBSCRIBE_STOP_TIMEOUT = 5000L
    }

    override fun onCleared() {
        super.onCleared()
        jobCall?.cancel()
    }

    /**
     * QuangDV edit area here
     */
    open fun <T> otherExceptions(exception: Throwable, data: MutableStateFlow<DataState<T>>) {
        when (exception) {
            is NetworkCheckerInterceptor.NoConnectivityException -> {
//                ToastUtil.error(
//                    app.applicationContext, app.applicationContext.getString(R.string.network_error)
//                )?.show()
                data.value = DataState.Error(
                    ErrorCode.NO_INTERNET.code,
                    message = app.applicationContext.getString(R.string.network_error),
                    isException = false
                )
            }

            else -> {
                Timber.e(
                    "Handler IOException ${exception.message} " + app.applicationContext.getString(
                        R.string.common_error
                    )
                )
                data.value = DataState.Error(
                    400.toString(),
                    message = app.applicationContext.getString(
                        R.string.common_error
                    ),
                    isException = true
                )
            }
        }
    }

    /**
     * @param T MutableStateFlow data
     */
    open fun <T> coroutineException(data: MutableStateFlow<DataState<T>>): CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, exception ->

            if (exception !is CancellationException) {
                when (exception) {
                    is HttpException -> {
                        Timber.e("Handler  coroutineException HttpException")
                        Timber.e("Handler  coroutineException HttpException exception ${exception.message()}")
                        val errorResponse =
                            exception.response()?.errorBody()?.string()?.mapErrorMessage()
                        if (errorResponse != null) {
                            if (errorResponse.errorCode != null) {
                                data.value = DataState.Error(
                                    errorResponse.errorCode.toString(),
                                    errorResponse.message!!,
                                    isException = true
                                )
                            } else if (errorResponse.status != null) {
                                Timber.e("Handler  HttpException $errorResponse?.status ")
                                data.value = DataState.Error(
                                    errorResponse.status.toString(),
                                    errorResponse.message!!,
                                    isException = true
                                )
                            } else data.value = DataState.Error(
                                (-1).toString(),
                                "Lỗi không xác định!",
                                isException = true
                            )
                        } else {
                            data.value = DataState.Error(
                                exception.code().toString(),
                                exception.message(),
                                isException = true
                            )
                            Timber.e("Handler  coroutineException errorResponse null with msg = ${exception.message()} ${exception.code()}")
                        }
                    }

                    else -> {
                        Timber.e("Handler  otherExceptions")
                        otherExceptions(exception, data)
                    }
                }
                data.value = DataState.Empty
            }
        }

    open fun <T> flowCatch(exception: Throwable, data: MutableStateFlow<DataState<T>>, tag:String="") {
        if (exception is HttpException) {
            Timber.e("flowCatch $tag  HttpException ${exception.message}")
            val errorResponse = exception.response()?.errorBody()?.string()
            Timber.e("flowCatch  $tag HttpException errorResponse $errorResponse")
            try {
                if (errorResponse != null) {
                    val errorObject = JSONObject(errorResponse)
                    val errorCode = errorObject.optInt("error_code", -1)
                    val errorMessage = errorObject.optString("message", "Unknown error")
                    data.value =
                        DataState.Error(errorCode.toString(), errorMessage, isException = true)
                    data.value = DataState.Empty
                    return
                }
            } catch (e: Exception) {
                data.value =
                    e.message?.let { DataState.Error(0.toString(), it, isException = true) }!!
                data.value = DataState.Empty
            }
            Timber.e(
                "flowCatch  HttpException2 ${exception.code()} | ${
                    exception.response()?.errorBody()?.string()
                }"
            )
        } else {
            Timber.e("flowCatch  otherExceptions" + exception.message)
            otherExceptions(exception, data)
        }
        data.value = DataState.Empty
    }

    open fun <T> catchSumError(data: MutableStateFlow<DataState<T>>, base: BaseDataResponse<T>) {
        when (base.code) {
            ErrorCode.TOKEN_EXPIRED.code -> {
                Timber.d("catchSumError code: ${base.code}")
                Timber.d("Need to force logout!")
                _forceLogout.value = true
//                val intent = Intent(app.applicationContext, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                app.applicationContext.startActivity(intent)
            }

            else -> {
                data.value =
                    DataState.Error(base.code.getOrBlank(), base.message, isException = true)
            }
        }
    }

    //Response catch error
    open fun <T> errorCatch(
        baseDataResponse: BaseDataResponse<T>,
        data: MutableStateFlow<DataState<T>>, tag:String=""
    ) {
        Timber.d("responseCatch $tag : ${baseDataResponse.errorCode}")
        if (baseDataResponse.errorCode.equals("7")) {
            Timber.d("responseCatch $tag : Send BroadCast to force logout")
            //ToDO call action force logout
            val intent = Intent(ErrorAction.ACTION_FORCE_LOGOUT)
            intent.setPackage("com.el.mybasekotlin")
            app.applicationContext.sendBroadcast(intent) // Send broadcast

        } else {
            data.value = DataState.Error(
                code = baseDataResponse.errorCode.toString(),
                message = baseDataResponse.message
            )
        }


    }

    // flow catch
    open suspend fun flowCatchComposeTest(
        exception: Throwable,
        data: MutableSharedFlow<Any>,
        isLastIdleStatus: Boolean = true
    ) {
        Timber.e("flowCatch  HttpException ${exception.message}")
        if (exception is HttpException) {
            val errorResponse = exception.response()?.errorBody()?.string()
            try {

                if (errorResponse != null) {
                    val errorObject = JSONObject(errorResponse)
                    val errorCode = errorObject.optInt("error_code", -1)
//                    if (errorCode == 404) {
                    val errorMessage = errorObject.optString("message", "Unknown error")
                    data.emit(DataState.Error(errorMessage, errorCode.toString()))
                    if (isLastIdleStatus) {
                        delay(100)
                        data.emit(DataState.Empty)
                    }
                    return

                }
            } catch (e: Exception) {
                Timber.e("flowCatch  Exception2  ${exception.message}")
                data.emit(DataState.Error(exception.message?.toString(), 0.toString()))
                if (isLastIdleStatus) {
                    delay(100)
                    data.emit(DataState.Empty)
                }
//                data.value = e.message?.toString()?.let { DataSate.error(null, 0, it) }!!
//                data.value = DataSate.nothing()
            }


        } else {
            Timber.e("flowCatch  otherExceptions " + exception.message)
//            otherExceptions(exception, data)
            data.emit(
                DataState.Error(
                    exception.message, 0.toString()
                )
            )
//            data.emit(
//                RequestState.Error(
//                    app.applicationContext.getString(
//                        R.string.common_error
//                    ), 0
//                )
//            )

        }
        data.emit(DataState.Empty)
        return

    }

}