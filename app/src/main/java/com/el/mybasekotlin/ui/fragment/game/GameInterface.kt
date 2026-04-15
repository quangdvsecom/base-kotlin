package com.el.mybasekotlin.ui.fragment.game

import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult


// ===========================================
//  CONTROLLER - Fragment dùng interface này để điều khiển game
// ===========================================

interface GameController {
    val gameState: GameState
    fun setup()
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun reset()
    fun destroy()
}

/**
 * Điều khiển data , update data realtime từ gameplay screen
 * thêm hàm cần thiết như body hoặc hands data
 */
interface DataController {
    fun updateDataFaceDetect(faceData: FaceStateResult)

}

// ===========================================
//  CALLBACK - Game view gửi event ngược lên Fragment
// ===========================================

interface GameViewListener {
    fun onGameStateChanged(state: GameState) {}
    fun onScoreUpdated(score: Int) {}
    fun onGameFinished(score: Int, extraData: Map<String, Any> = emptyMap()) {}
    fun onGameError(error: String) {}
}