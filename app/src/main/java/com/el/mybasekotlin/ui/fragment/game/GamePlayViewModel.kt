package com.el.mybasekotlin.ui.fragment.game

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
class GamePlayViewModel @Inject constructor(
    private val application: Application,
    private val apiHelper: ApiHelper
) : BaseViewModel(application, apiHelper) {

    // Tạo một StateFlow để observe (hoặc dùng biến var thường nếu không cần observe lên UI)
    private val _currentGame = MutableStateFlow<GameData?>(null)
    val currentGame = _currentGame.asStateFlow()


//    private val _gameData = MutableStateFlow<DataState<GameData>>(DataState.Empty)
//    val gameData: StateFlow<DataState<GameData>> = _gameData.asStateFlow()
//    var gameData: DataState<GameData>? = null


    fun initGameData(gameData: GameData?) {
        if (gameData != null && _currentGame.value == null) {
            _currentGame.value = gameData
            initCamera(gameData)
            // Bắt đầu các logic khác, tải hình ảnh, đọc file json...
        }
    }

    /**
     * Setup camera config theo data từ game,
     * Định nghĩa từ  danh sách game, khi triển khai 1 game mới thì cần xác định game cần gì, cho hết vào config
     */
    private val _setUpCamera = MutableStateFlow<CameraConfig?>(null)
    val setUpCamera = _setUpCamera.asStateFlow()
    private fun initCamera(gameData: GameData) {
        val dataConfig = CameraConfig(
            detectType = gameData.cameraDetectType,
            detectedMode = DetectionMode.BASIC,
            isFrontCamera = true
        )
        _setUpCamera.value = dataConfig
    }

    /**
     * -------------------Handle data camera
     */
    /**
     * 1. Handle face detect data , share cho các class game dùng
     */
    private val _faceData = MutableStateFlow<FaceStateResult?>(null)
    val faceData: StateFlow<FaceStateResult?> = _faceData.asStateFlow()
    fun updateFaceData(data: FaceStateResult) {
        Timber.d("GamePlayViewModel: updateFaceData ${data.face?.headEulerAngleX}")
        _faceData.value = data
    }
    /**
     * Controller record
     */
    // Trong GamePlayViewModel
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
        // Tại đây bạn có thể gọi thêm logic bắt đầu/dừng record thực tế
    }


    /**
     * Game state
     */

    private val _gameState  = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()
    fun updateGameState(gameState: GameState){
        _gameState.value = gameState
    }
}


