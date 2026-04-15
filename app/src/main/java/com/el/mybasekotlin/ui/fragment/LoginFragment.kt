package com.el.mybasekotlin.ui.fragment

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.databinding.LoginFragmentBinding
import com.el.mybasekotlin.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>(LoginFragmentBinding::inflate) {
    override fun initDataBeforeCreateView() {
    }

    override fun init() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Để trống để vô hiệu hóa nút Back
                    // Nếu cần, bạn có thể hiển thị một thông báo hoặc thực hiện hành động khác
                    Toast.makeText(
                        requireContext(),
                        "Cannot go back from this screen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        binding.btClick.setOnClickListener {
            navigateTo(R.id.action_to_main)
        }
        binding.btBack.setOnClickListener { findNavController().popBackStack() }
    }

    override fun initObserver() {

    }

}