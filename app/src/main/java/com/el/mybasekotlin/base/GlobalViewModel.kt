package com.el.mybasekotlin.base

import com.el.mybasekotlin.data.network.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.app.Application
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * Created by ElChuanmen on 1/15/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@HiltViewModel
class GlobalViewModel @Inject constructor(private val apiHelper: ApiHelper, app: Application) :
    BaseViewModel(app, apiHelper) {

    private val _overlayStateFlow =
        MutableSharedFlow<MutableState<@Composable (BoxScope.() -> Unit)?>>()
    val overlayStateFlow = _overlayStateFlow.asSharedFlow()

    //    val appOverlayContent = mutableStateOf<@Composable (BoxScope.() -> Unit)?>(null)
    fun showOverlayContent(content: @Composable (BoxScope.() -> Unit)?) {
        launchJob {
            _overlayStateFlow.emit(mutableStateOf(content))
        }
    }

    private val _networkActive = MutableStateFlow(mutableStateOf(false))
    val networkActive = _networkActive.asStateFlow()

    private val _isHomeLoadingAgain = MutableStateFlow(mutableStateOf(false))
    val isHomeLoadingAgain = _isHomeLoadingAgain.asStateFlow()

    private val _wifiName = MutableStateFlow(mutableStateOf(""))
    val wifiName = _wifiName.asStateFlow()

    fun setNetworkState(available: Boolean) {
        _networkActive.value = mutableStateOf(available)
    }

    fun setWifiName(name: String) {
        _wifiName.value = mutableStateOf(name)
    }

}

val LocalGlobalViewModel = staticCompositionLocalOf<GlobalViewModel> {
    error("No GlobalViewModel provided")
}