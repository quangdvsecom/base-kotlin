package com.el.mybasekotlin.ui.fragment.gamedetails

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.databinding.MyFragmentGameExampleBinding
import com.el.mybasekotlin.ui.fragment.game.BaseGameFragment
import com.el.mybasekotlin.ui.fragment.game.GamePlayViewModel
import com.el.mybasekotlin.utils.extension.collectIn
import dagger.hilt.android.AndroidEntryPoint
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
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
    private var pigeonModelNode: io.github.sceneview.node.ModelNode? = null
    override fun init() {

//        lifecycleScope.launch {
//            val modelInstance = binding.sceneView.modelLoader.loadModelInstance("cat_ver3.glb")
//            if (modelInstance != null) {
//                pigeonModelNode = io.github.sceneview.node.ModelNode(modelInstance).apply {
//                    position = io.github.sceneview.math.Position(x = 0f, y = -1.0f, z = -3.0f)
//                    scale = io.github.sceneview.math.Scale(4f)
//                    isVisible = false
//                }
//                binding.sceneView.addChildNode(pigeonModelNode!!)
////                setupRingNode()
//            }
//        }
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