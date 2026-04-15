package com.el.mybasekotlin.ui.fragment.gamedetails

import android.app.Application
import com.el.mybasekotlin.base.BaseViewModel
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.state.DataState
import com.el.mybasekotlin.ui.fragment.camera.CameraConfig
import com.el.mybasekotlin.ui.fragment.camera.DetectionMode
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult
import com.otaliastudios.cameraview.frame.Frame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyFragmentGameExampleViewModel @Inject constructor(
    private val application: Application,
    private val apiHelper: ApiHelper
) : BaseViewModel(application, apiHelper) {

    // Tạo một StateFlow để observe (hoặc dùng biến var thường nếu không cần observe lên UI)
    private val _currentGame = MutableStateFlow<GameData?>(null)
    val currentGame = _currentGame.asStateFlow()



}

