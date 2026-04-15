package com.el.mybasekotlin.ui.fragment.game

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult

// ===========================================
//  GAME STATE
// ===========================================

enum class GameState {
    IDLE,
    PLAYING,
    PAUSED,
    STOPPED,
    FINISHED
}




// ===========================================
//  BASE GAME VIEW
// ===========================================

/**
 * Base class cho tất cả game custom view.
 * Implement [GameController] để Fragment điều khiển qua interface.
 *
 * Team member tạo game mới:
 * ```
 * class MyGameView(context: Context, attrs: AttributeSet? = null) : BaseGameView(context, attrs) {
 *
 *     override fun onSetupGame(gameData: GameData) {
 *         // Inflate layout, load tài nguyên, parse gameData
 *     }
 *
 *     override fun onStartGame() {
 *         // Bắt đầu logic game
 *     }
 *
 *     override fun onStopGame() {
 *         // Dừng game hoàn toàn
 *     }
 *
 *     override fun onPauseGame() {
 *         // Tạm dừng animation, timer...
 *     }
 *
 *     override fun onResumeGame() {
 *         // Tiếp tục từ chỗ pause
 *     }
 *
 *     override fun onResetGame() {
 *         // Reset về trạng thái ban đầu, chơi lại
 *     }
 *
 *     override fun onDestroyGame() {
 *         // Giải phóng resource: bitmap, animation, sound, listener...
 *     }
 *
 *     override fun onClearCache() {
 *         // Xóa cache: file tạm, image cache, data cache...
 *     }
 *
 *     override fun onFaceDataUpdated(faceData: FaceStateResult) {
 *         // Xử lý face data từ camera mỗi frame
 *     }
 * }
 * ```
 *
 * Fragment sử dụng:
 * ```
 * val gameView = MyGameView(context)
 * val controller: GameController = gameView
 * controller.setup(gameData)
 * controller.start()
 *
 * // Truyền shared ViewModel data
 * gameView.attachSharedData(provider)
 * ```
 */
abstract class BaseGameCustomView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), GameController, DataController {

    // ---- Internal state ----
    override var gameState: GameState = GameState.IDLE
        protected set

    /**
     * Update trạng thái game, điểm để GamePlay Screen có thể lưu lại khi chuyển màn
     */
    var listener: GameViewListener? = null


    // ===========================================
    //  GAME CONTROLLER IMPLEMENTATION
    //  Fragment gọi các hàm này qua GameController interface
    // ===========================================

    override fun setup() {
        gameState = GameState.IDLE
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
    //  CAMERA DATA - Fragment gọi khi có face data mới
    // ===========================================
    // data controller , update data liên tục đối với detech
    override fun updateDataFaceDetect(faceData: FaceStateResult) {
        dispatchFaceData(faceData)
    }
    fun dispatchFaceData(faceData: FaceStateResult) {
        if (gameState == GameState.PLAYING) {
            onFaceDataUpdated(faceData)
        }
    }

    // ===========================================
    //  SCORE HELPER - Game con gọi để báo điểm lên Fragment
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
    //  ABSTRACT - Team member BẮT BUỘC override
    // ===========================================

    /** Khởi tạo game: inflate layout, load resource, parse gameData */
    protected abstract fun onSetupGame()

    /** Bắt đầu game: start animation, timer, logic chính */
    protected abstract fun onStartGame()

    /** Dừng game hoàn toàn (khác pause) */
    protected abstract fun onStopGame()

    /** Tạm dừng: freeze animation, timer... */
    protected abstract fun onPauseGame()

    /** Tiếp tục từ chỗ pause */
    protected abstract fun onResumeGame()

    /** Reset game về trạng thái ban đầu để chơi lại */
    protected abstract fun onResetGame()

    /** Giải phóng resource: bitmap, animation, sound, listener,clear cache */
    protected abstract fun onDestroyGame()

    // ===========================================
    //  OPTIONAL OVERRIDE - Camera data
    // ===========================================

    /** Nhận face data từ camera mỗi frame. Chỉ gọi khi game đang PLAYING. */
    protected open fun onFaceDataUpdated(faceData: FaceStateResult) {}

}
