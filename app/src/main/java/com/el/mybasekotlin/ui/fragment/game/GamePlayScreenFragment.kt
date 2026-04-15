package com.el.mybasekotlin.ui.fragment.game

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.databinding.GamePlayFragmentBinding
import com.el.mybasekotlin.ui.fragment.camera.CameraConfig
import com.el.mybasekotlin.ui.fragment.camera.DetectType
import com.el.mybasekotlin.ui.fragment.camera.FaceAnalyzerCameraView
import com.el.mybasekotlin.utils.extension.collectIn
import com.el.mybasekotlin.utils.extension.getScreenSize
import com.el.mybasekotlin.utils.extension.parcelable
import com.otaliastudios.cameraview.controls.AudioCodec
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class GamePlayScreenFragment : BaseFragment<GamePlayFragmentBinding>(GamePlayFragmentBinding::inflate) {
    private val gamePlayViewModel: GamePlayViewModel by viewModels()
    private var currentCustomView: View? = null
    private var gameController: GameController? = null
    private var dataController: DataController? = null
    private var gameData: GameData? = null

    /**
     * Khởi tạo listener ra ngoài để dùng chung cho view hoặc fragment được add vào
     * Nếu xác định chỉ dùng fragment, không dùng customview để làm parent của game thì không cần khai báo
     * các state lưu hết vào gameViewModel
     */
    private val gameViewListener = object : GameViewListener {
        override fun onGameStateChanged(state: GameState) {
            Timber.d("GamePlayFragment: game stateChanged = $state")
            gamePlayViewModel.updateGameState(state)
        }

        override fun onScoreUpdated(score: Int) {
            Timber.d("GamePlayFragment: scoreUpdated = $score")
        }

        override fun onGameFinished(score: Int, extraData: Map<String, Any>) {
            Timber.d("GamePlayFragment: gameFinished, score = $score")
        }

        override fun onGameError(error: String) {
            Timber.e("GamePlayFragment: gameError = $error")

        }
    }


    /**
     * Xử lý data logic trước khi view được khởi tạo
     */
    override fun initDataBeforeCreateView() {
        val dataGame = arguments?.parcelable<GameData>("KEY_GAME_MODEL")
        gamePlayViewModel.initGameData(dataGame)
        Timber.d("GamePlayFragment: initDataBeforeCreateView ${dataGame?.name}")
    }

    override fun init() {
        gameController?.start()
        setUpBaseCameraView()
        initUI()
        initControllerRecord()
    }

    override fun initObserver() {
        gamePlayViewModel.apply {
            currentGame.collectIn(this@GamePlayScreenFragment) {
                /**
                 * Khi có game data mới =>> render lại game
                 */
                gameData = it
                it?.let { it1 -> renderContent(it1) }
            }
            setUpCamera.collectIn(this@GamePlayScreenFragment) {
                it?.let { config -> handleSetupConfigCamera(config) }
            }
        }
        handleControllerRecord()
    }

    /**
     * Setup UI cơ bản
     */
    fun initUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    fun initControllerRecord() {
        binding.apply {
            btnStart.setOnClickListener {
                gamePlayViewModel.toggleRecording()
            }

        }
    }

    /**
     * Handle controller camera, record
     */
    private fun handleControllerRecord() {
        gamePlayViewModel.apply {
            isRecording.collectIn(this@GamePlayScreenFragment) {recording->
                Timber.d("QuangDVGamePlay $recording")
                binding.btnStart.setImageResource(
                    if (recording) R.drawable.ic_stop_record
                    else R.drawable.ic_video_record
                )
            }
        }

    }

    /**
     * Update config came ra khi có config từ game data
     * - Detect hand, head or full body ,empty
     */
    private fun handleSetupConfigCamera(config: CameraConfig) {
        binding.cameraView.apply {
            when (config.detectType) {
                DetectType.FACE_DETECT.value -> {
                    val newProcessor = FaceAnalyzerCameraView(config.detectedMode) { result, _ ->

                        // update data cho ViewModel để share view model cho các game code bằng fragment
                        gamePlayViewModel.updateFaceData(result)
                        //Update face data cho game sử dụng customview
                        dataController?.updateDataFaceDetect(result)
                    }
                    addFrameProcessor(newProcessor)
                }
            }
        }
    }

    /**
     * Setup camera preview cơ bản , config update sau
     */
    private fun setUpBaseCameraView() {
        val screenSize = getScreenSize(requireContext())
        val screenWidth = screenSize.first
        val screenHeight = screenSize.second

        val dimensWidth = SizeSelectors.minWidth(1000)
        val dimensHeight = SizeSelectors.minHeight(2000)
        val dimensions = SizeSelectors.and(dimensWidth, dimensHeight)
        val ratio = SizeSelectors.aspectRatio(AspectRatio.of(screenWidth, screenHeight), 0f)

        val result = SizeSelectors.or(
            SizeSelectors.and(ratio, dimensions),
            ratio,
            SizeSelectors.biggest()
        )
        binding.cameraView.setPictureSize(result)
        binding.cameraView.setPreviewStreamSize(result)

        binding.cameraView.apply {
            setRequestPermissions(false)
            setLifecycleOwner(viewLifecycleOwner)
            audioBitRate = 320000
            audioCodec = AudioCodec.AAC
            videoBitRate = 5000000
            snapshotMaxHeight = 1080
            snapshotMaxWidth = 720
            post { open() }
        }
    }

    private fun renderContent(gameData: GameData) {
        val container = binding.contentContainer
        clearCurrentContent(container)

        when (val content = GameFactory.create(gameData)) {
            /**
             * Content là fragment
             */
            is DetailsContent.FragmentContent -> {
                val fragment = content.fragment
                // Quan trọng: Gán controller nếu Fragment đó implement interface
                if (fragment is GameController) {
                    gameController = fragment
                }
                if (fragment is DataController) {
                    dataController = fragment
                }
                // Nếu fragment là BaseGameFragmentDetails, gán listener
                if (fragment is BaseGameFragment<*>) {
                    fragment.listener = gameViewListener
                }
                // Fragment có thể giao tiếp qua ViewModel dùng chung (SharedViewModel)
                childFragmentManager.beginTransaction()
                    .replace(R.id.contentContainer, content.fragment)
                    .commit()
            }

            /**
             * Custom view layout
             */
            is DetailsContent.ViewContent -> {
                val view = content.viewFactory(requireContext())
                currentCustomView = view
                container.addView(
                    view,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )

                if (view is BaseGameCustomView) {
                    view.listener = gameViewListener
                    gameController = view
                    dataController = view
                    view.start()
                }
            }
        }
    }

    /**
     * Xóa content hiện tại và game controller
     */
    private fun clearCurrentContent(container: FrameLayout) {
        childFragmentManager.findFragmentById(R.id.contentContainer)?.let {
            childFragmentManager.beginTransaction()
                .remove(it)
                .commitNow()
        }
        gameController?.destroy()
        gameController = null
        currentCustomView = null
        container.removeAllViews()
    }

    override fun onResume() {
        super.onResume()
        gameController?.resume()
    }

    override fun onPause() {
        super.onPause()
        gameController?.pause()
    }

    override fun onDestroyView() {
        gameController?.destroy()
        gameController = null
        currentCustomView = null
        super.onDestroyView()
    }
}