package com.el.mybasekotlin.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.databinding.AFragmentBinding
import com.el.mybasekotlin.databinding.BFragmentBinding
import com.el.mybasekotlin.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@AndroidEntryPoint
class BFragment : BaseFragment<BFragmentBinding>(BFragmentBinding::inflate){
    override fun initDataBeforeCreateView() {
    }

    override fun init() {
        Timber.d("BFragment initView")
        binding.btClick.setOnClickListener {
//            navigateTo(R.id.action_to_A_screen,     null,null,false)
            findNavController().popBackStack()
        }

    }

    override fun initObserver() {

    }


}