package com.el.mybasekotlin.ui.fragment.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.BaseFragment
import com.el.mybasekotlin.databinding.HomeFragmentBinding
import com.el.mybasekotlin.ui.fragment.home.ListGameFragment
import com.el.mybasekotlin.ui.fragment.BFragment
import com.el.mybasekotlin.ui.fragment.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ElChuanmen on 1/14/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(HomeFragmentBinding::inflate) {
    override fun initDataBeforeCreateView() {
    }

    override fun init() {
        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        val adapter = HomePagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit=2
        binding.viewPager.isUserInputEnabled = true // Tắt vuốt nếu chỉ muốn dùng bottom nav
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.nav_dashboard -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.nav_notifications -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun initObserver() {}

    private inner class HomePagerAdapter(fragment: FragmentManager,
                                         lifecycle: Lifecycle) : FragmentStateAdapter(fragment,lifecycle) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ListGameFragment()
                1 -> BFragment()
                2 -> LoginFragment() // Thay bằng CFragment nếu có
                else -> ListGameFragment()
            }
        }
    }
}
