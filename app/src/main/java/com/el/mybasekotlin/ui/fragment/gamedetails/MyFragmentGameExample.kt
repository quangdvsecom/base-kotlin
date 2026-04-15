package com.el.mybasekotlin.ui.fragment.gamedetails

import androidx.fragment.app.viewModels
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.databinding.MyFragmentGameExampleBinding
import com.el.mybasekotlin.ui.fragment.game.BaseGameFragment
import com.el.mybasekotlin.ui.fragment.game.GamePlayViewModel
import com.el.mybasekotlin.utils.extension.collectIn
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyFragmentGameExample() :
    BaseGameFragment<MyFragmentGameExampleBinding>(MyFragmentGameExampleBinding::inflate) {
    private val gameViewModel: GamePlayViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    /**
     * Viewmodel riêng cho từng fragment game con
     */
    private val myFragmentGameExampleViewModel: MyFragmentGameExampleViewModel by viewModels()
    var gameData: GameData? = null

    override fun initDataBeforeCreateView() {
//        val dataGame = arguments?.parcelable<GameData>("KEY_GAME_MODEL")

//        gamePlayViewModel.initGameData(dataGame)
        gameData = gameViewModel.currentGame.value
        Timber.d("QuangDV ${gameViewModel.currentGame.value?.name}")
    }

    override fun init() {


    }

    override fun onSetupGame() {
    }

    override fun onStartGame() {
        binding.tvStatus.text = "Game is start......"
    }

    override fun onStopGame() {
        binding.tvStatus.text = "Game is stoped"
    }

    override fun onPauseGame() {
    }

    override fun onResumeGame() {
    }

    override fun onResetGame() {
    }

    override fun onDestroyGame() {
    }


    override fun initObserver() {

        gameViewModel.apply {
            faceData.collectIn(this@MyFragmentGameExample) {
                binding.tvContent.text = "Content game by fragment ${it?.angleX}"
            }
        }

//        gamePlayViewModel.apply {
//            faceData.collectIn(this@MyFragmentGameExample) {
//                Timber.d("$TAG faceData: ${it?.angleX.toString()}")
//            }
//
//        }

//        sharedViewModel.apply {
//            currentGame.collectIn(this@MyFragmentGameExample) {
//                gameData = it
//            }
//        }
//
//        sharedViewModel.faceData.collectIn(this) { faceResult ->
//            faceResult?.let {
//                Timber.d("$TAG con nhận được data: ${it.angleY}")
//            }
//        }

    }


}