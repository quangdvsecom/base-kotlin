package com.el.mybasekotlin.ui.fragment.game

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.base.Inflate
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult

/**
 * Base class cho các Game được xây dựng dưới dạng Fragment.
 * Kế thừa từ BaseFragment để sử dụng lại các tiện ích UI, Navigation, Permission...
 * Implement [GameController] và [DataController] để Fragment cha (như GamePlayFragment) có thể điều khiển logic game.
 * Sau ae có update thì có thể dùng share viewmodel cho nhanh đối với fragment
 */
abstract class BaseGameFragment<VB : ViewBinding>(inflate: Inflate<VB>) :
    BaseFragment<VB>(inflate), GameController, DataController {

    // Trạng thái hiện tại của game
    override var gameState: GameState = GameState.IDLE
        protected set

    // Listener để báo cáo sự kiện (điểm số, kết thúc, lỗi) về Fragment cha
    var listener: GameViewListener? = null

    // ===========================================
    //  LIFECYCLE HOOK
    // ===========================================

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Tự động tìm kiếm listener từ parentFragment nếu class cha có implement GameViewListenerProvider hoặc chính là GameViewListener
        // Đây là phương án dự phòng nếu Fragment cha quên gán listener thủ công
        if (parentFragment is GameViewListener) {
            listener = parentFragment as GameViewListener
        }
    }

    // ===========================================
    //  GAME CONTROLLER IMPLEMENTATION
    // ===========================================

    override fun setup() {
        gameState = GameState.IDLE
        onSetupGame()
    }

    override fun start() {
        if (gameState == GameState.PLAYING) return
        gameState = GameState.PLAYING
        listener?.onGameStateChanged(GameState.PLAYING)
        onStartGame()
    }

    override fun stop() {
        if (gameState == GameState.STOPPED) return
        gameState = GameState.STOPPED
        listener?.onGameStateChanged(GameState.STOPPED)
        onStopGame()
    }

    override fun pause() {
        if (gameState != GameState.PLAYING) return
        gameState = GameState.PAUSED
        listener?.onGameStateChanged(GameState.PAUSED)
        onPauseGame()
    }

    override fun resume() {
        if (gameState != GameState.PAUSED) return
        gameState = GameState.PLAYING
        listener?.onGameStateChanged(GameState.PLAYING)
        onResumeGame()
    }

    override fun reset() {
        gameState = GameState.IDLE
        listener?.onGameStateChanged(GameState.IDLE)
        onResetGame()
    }

    override fun destroy() {
        gameState = GameState.FINISHED
        onDestroyGame()
        listener = null
    }

    // ===========================================
    //  DATA CONTROLLER IMPLEMENTATION
    // ===========================================

    override fun updateDataFaceDetect(faceData: FaceStateResult) {
        // Chỉ xử lý dữ liệu camera khi game đang chơi
        if (gameState == GameState.PLAYING) {
            onFaceDataUpdated(faceData)
        }
    }

    // ===========================================
    //  HELPERS CHO GAME CON - Để báo cáo ngược lại cho cha
    // ===========================================

    protected fun reportScore(score: Int) {
        listener?.onScoreUpdated(score)
    }

    protected fun reportFinished(score: Int, extraData: Map<String, Any> = emptyMap()) {
        gameState = GameState.FINISHED
        listener?.onGameFinished(score, extraData)
        listener?.onGameStateChanged(GameState.FINISHED)
    }

    protected fun reportError(error: String) {
        listener?.onGameError(error)
    }

    // ===========================================
    //  ABSTRACT HOOKS - Các class con bắt buộc override
    // ===========================================

    protected abstract fun onSetupGame()
    protected abstract fun onStartGame()
    protected abstract fun onStopGame()
    protected abstract fun onPauseGame()
    protected abstract fun onResumeGame()
    protected abstract fun onResetGame()
    protected abstract fun onDestroyGame()

    /** Nhận face data từ camera mỗi frame (Optional) */
    protected open fun onFaceDataUpdated(faceData: FaceStateResult) {}

    // ===========================================
    //  BASE FRAGMENT OVERRIDES
    // ===========================================

    override fun initDataBeforeCreateView() {}
    
    override fun init() {
        // Class con sẽ thực hiện UI setup ở đây
    }

    override fun initObserver() {}

    override fun onDestroyView() {
        destroy() // Đảm bảo giải phóng game khi Fragment bị hủy view
        super.onDestroyView()
    }
}