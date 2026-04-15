package com.el.mybasekotlin.ui.fragment.home

import android.os.Bundle
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.data.model.game.GameDetailsContentType
import com.el.mybasekotlin.data.model.game.GameType
import com.el.mybasekotlin.databinding.AFragmentBinding
import com.el.mybasekotlin.helpers.TestMessage
import com.el.mybasekotlin.helpers.flowbus.busEvent
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListGameFragment : BaseFragment<AFragmentBinding>(AFragmentBinding::inflate) {
    
    override fun initDataBeforeCreateView() {}

    override fun init() {
        // Khởi tạo dataGame với các giá trị mặc định như đã khai báo trong class
        val gameData: GameData = GameData(
            id = 0,
            gameType = GameType.RANKING_FILTER_GAME.value,
            detailsType = GameDetailsContentType.FRAGMENT.value,
            name = "Game Test 1",
            image = "",
            likeCount = "",
            minTimeToPlay = 60000,
            countDownTimer = 0,
            gameResult = "link_video_record",
            pos = 0,
            enable = true,
            tagPos = 0,
            tag = 0,
            gameResourcePath = 0,
            gameContentDataPath = ""
        )

        binding.btClick.setOnClickListener {
            Timber.d("ListGameFragment: Navigating to details with default data")
            val bundle = Bundle().apply {
                putParcelable("KEY_GAME_MODEL", gameData)
            }
            // Điều hướng và truyền bundle dữ liệu
            navigateTo(R.id.gamePlayFragment, bundle)
            busEvent(TestMessage("Navigating from ListGameFragment"))
        }
    }

    override fun initObserver() {}
}