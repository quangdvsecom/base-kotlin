package com.el.mybasekotlin.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.databinding.AFragmentBinding
import com.el.mybasekotlin.helpers.TestMessage
import com.el.mybasekotlin.helpers.flowbus.busEvent
import com.el.mybasekotlin.ui.fragment.MainViewModel
import com.el.mybasekotlin.utils.extension.collectIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

@AndroidEntryPoint
class ListGameFragment : BaseFragment<AFragmentBinding>(AFragmentBinding::inflate) {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val gameAdapter: GameAdapter by lazy {
        GameAdapter { gameData ->
            Timber.d("ListGameFragment: Navigating to details for ${gameData.name}")
            val bundle = Bundle().apply {
                putParcelable("KEY_GAME_MODEL", gameData)
            }
            navigateTo(R.id.gamePlayFragment, bundle)
        }
    }

    override fun initDataBeforeCreateView() {

    }

    override fun init() {
//        busEvent(TestMessage("test Msg from AFragment"))
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        if (binding.rvGames.adapter == null) {
            binding.rvGames.adapter = gameAdapter
        }
    }

    override fun initObserver() {
        mainViewModel.apply {
            dataAllGame.collectIn(this@ListGameFragment) { listGame ->
                    Timber.d("${TAG}QuangDV: list game size ${listGame.size}")
                if (!isAddAdapter){
                    gameAdapter.submitList(listGame)
                    isAddAdapter=true
                }
                }
        }
    }

}
