package com.el.mybasekotlin.ui.fragment.gamedetails

import android.content.Context
import android.view.LayoutInflater
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.databinding.MyGameViewBinding
import com.el.mybasekotlin.ui.fragment.camera.FaceStateResult
import com.el.mybasekotlin.ui.fragment.game.BaseGameCustomView
import timber.log.Timber

/**
 * Custom View cho Game Details sử dụng ViewBinding
 */

class MyGameCustomViewExample(context: Context, gameData: GameData) : BaseGameCustomView(context) {


    /**
     * Sử dụng binding
     */
    private val binding: MyGameViewBinding = MyGameViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    /**
     * Hoặc sử dụng add view cho thuần customview không layout xml
     * Draw view, glsurface
     */
    init {
//        addView(contrainlayout)
    }

    override fun onSetupGame() {
        // Thực hiện setup UI hoặc logic ban đầu dựa trên gameData
        // Ví dụ: binding.root.setBackgroundColor(...)
    }

    override fun onStartGame() {
        listener?.onScoreUpdated(10)

    }

    override fun onStopGame() {

    }

    override fun onPauseGame() {

    }

    override fun onResumeGame() {

    }

    override fun onResetGame() {

    }

    override fun onDestroyGame() {
        // Quan trọng: Giải phóng listener hoặc các tài nguyên tránh memory leak
    }

    override fun onFaceDataUpdated(faceData: FaceStateResult) {
        // Nhận dữ liệu camera liên tục tại đây
        Timber.d("MyGameViewExample: onFaceDataUpdated ${faceData.angleY}")
        binding.tvContent.text = faceData.angleY.toString()
//        ToastUtil.error(context,"${faceData.angleX}")?.show()
    }
}