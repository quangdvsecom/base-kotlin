package com.el.mybasekotlin.base

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.multidex.BuildConfig
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.el.mybasekotlin.R
import com.el.mybasekotlin.base.network.NetworkStatusChecker
import com.el.mybasekotlin.data.state.DataState
import com.el.mybasekotlin.data.state.ErrorCode
import com.el.mybasekotlin.data.state.PermissionState
import com.el.mybasekotlin.data.state.State
import com.el.mybasekotlin.ui.customview.SnackBarUtil
import com.el.mybasekotlin.ui.customview.ToastUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by ElChuanmen on 1/13/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */

typealias Inflate<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB

abstract class BaseFragment<VB : ViewBinding>() : Fragment() {
    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    constructor(inflate: Inflate<VB>) : this() {
        this.inflate = inflate
    }

    /* **********************************************************************
     * Variable
     ********************************************************************** */
    protected val TAG: String = this.javaClass.simpleName
    private lateinit var inflate: Inflate<VB>
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    private var mAnimatorHandler: Handler? = null
    private val DELAY_TIME = 400

    // fix lagging animation
    private var isAnimatorEnd: Boolean = false

    @Inject
    lateinit var gson: Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAnimatorHandler = Handler(Looper.getMainLooper())
        initDataBeforeCreateView()
    }

    /* **********************************************************************
     * Function - Lifecycle
     ********************************************************************** */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        Timber.d("Fragment onCreateView")
        return binding.root
    }
    private var isHandlerInvoked = false

    open fun onEnterAnimationComplete() {
        if (isHandlerInvoked || _binding == null) return
        isHandlerInvoked = true

        init()
        initObserver()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWindowFlags()

        isHandlerInvoked = false

        // Giải pháp cho ViewPager và các màn không có Animation:
        // Kiểm tra nếu Fragment nằm trong ViewPager hoặc ViewPager2
        val isInViewPager = parentFragment is androidx.viewpager.widget.ViewPager ||
                parentFragment?.parentFragment is androidx.viewpager2.widget.ViewPager2 ||
                view.parent is android.view.View && (view.parent as View).javaClass.name.contains("ViewPager")

        if (isInViewPager) {
            // Nếu trong ViewPager, gọi ngay lập tức
            onEnterAnimationComplete()
        } else {
            // Đối với Fragment thông thường, nếu sau 500ms mà Animator/Animation
            // vẫn chưa gọi (do nextAnim = 0 hoặc lỗi hệ thống), thì tự kích hoạt.
            view.postDelayed({
                if (!isHandlerInvoked && isAdded) {
                    onEnterAnimationComplete()
                }
            }, 500)
        }
    }

    override fun onDestroyView() {
        if (mAnimatorHandler != null) {
            mAnimatorHandler!!.removeCallbacksAndMessages(null)
        }
        super.onDestroyView()
        _binding = null
    }

    fun navigateTo(
        id: Int, bundle: Bundle? = null, popUpToId: Int? = null, isInclusive: Boolean? = null
    ) {
        val options = NavOptions.Builder().setEnterAnim(R.anim.nav_transition_from_right)
            .setExitAnim(R.anim.nav_transition_to_left)
            .setPopEnterAnim(R.anim.nav_transition_from_left)
            .setPopExitAnim(R.anim.nav_transition_to_right)
        if (popUpToId != null && isInclusive != null) {
            options.setPopUpTo(popUpToId, isInclusive)
        }
        findNavController().navigate(id, bundle, options.build())
//        navigateTo(
//            id = R.id.BFragment,         // Điều hướng đến màn hình B
//            popUpToId = R.id.BFragment,  // Xóa các màn hình sau B (C và D)
//            isInclusive = false          // Không xóa chính BFragment
//        )
    }

    private fun showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
//        val view = requireActivity().findViewById<View>(android.R.id.content)
        SnackBarUtil.showSnackBar(requireContext(), binding.root, message, duration)
    }

    private fun showSnackBar(@StringRes messageId: Int, duration: Int = Snackbar.LENGTH_SHORT) {
//        val view = requireActivity().findViewById<View>(android.R.id.content)
        val message = getString(messageId)
        SnackBarUtil.showSnackBar(requireContext(), binding.root, message, duration)
    }
    private fun setupWindowFlags() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    /**
     * Handle error from api response if you need to show UI global to user. Like
     * - Internet
     * - Token expired
     */
    fun handleErrorMessage(error: DataState.Error) {
        Timber.e("${error.message} ${error.code}")
        when (error.code) {
            ErrorCode.TOKEN_EXPIRED.code -> {
                //Force Logout
//                AppPreferences.clearData()
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
//                (requireActivity() as? MainActivity)?.finish()
            }

            ErrorCode.NO_INTERNET.code -> {
                showSnackBar(getString(R.string.network_error))
            }

            ErrorCode.WRONG_OTP.code -> {
                showSnackBar(getString(R.string.wrong_otp))
            }

            ErrorCode.SIGN_IN_ANOTHER_DEVICE.code -> {
                showSnackBar(R.string.login_another_device_error)
            }

            else -> {
                if (!error.isException) {
                    showSnackBar(error.message)
                } else if (BuildConfig.DEBUG) {
                    showSnackBar(error.message)
                }
            }
        }
    }

    /* **********************************************************************
     * Function - Abstract
     ********************************************************************** */
    /**
     * Function for call setupData before createView
     */
    abstract fun initDataBeforeCreateView()
    /**
     * Function for handling view in fragment
     */
    abstract fun init()

    /**
     * Function for handling UI event observer from view model in fragment
     */
    abstract fun initObserver()


    /**
     *
     * Change tab font
     * - If tab is selected =>> font to Bold else to Regular
     */
    fun changeTabsFontSubCustoms(tabLayout: TabLayout, pos: Int, ctx: Context) {
        val typeRegular: Typeface? = ResourcesCompat.getFont(ctx, R.font.be_vietnam_pro_regular)
        val typeBold: Typeface? = ResourcesCompat.getFont(ctx, R.font.be_vietnam_pro_bold)
        val vg: ViewGroup = tabLayout.getChildAt(0) as ViewGroup
        val tabsCount: Int = vg.getChildCount()
        var typefaceSelected = typeBold;
        for (j in 0 until tabsCount) {
            val vgTab: ViewGroup = vg.getChildAt(j) as ViewGroup
            val tabChildsCount: Int = vgTab.getChildCount()
            if (j == pos) typefaceSelected = typeBold else typefaceSelected = typeRegular
            for (i in 0 until tabChildsCount) {

                val tabViewChild: View = vgTab.getChildAt(i)
                if (tabViewChild is AppCompatTextView) {
                    val viewChild: TextView = tabViewChild as TextView
                    viewChild.setTypeface(typefaceSelected)
                    viewChild.setAllCaps(false)

                }
            }
        }
    }

    /**
     *
     */
    fun Activity.dismissKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isAcceptingText) inputMethodManager.hideSoftInputFromWindow(
            this.currentFocus?.windowToken, /*flags:*/
            0
        )
    }

    /**
     * có thể dùng show toast ở đây hoặc sử dụng  toast extension
     */

    open fun showToastWarning(title: String?) {
        ToastUtil.warning(requireActivity(), title)?.show()
    }

    open fun showToastError(title: String?) {
        ToastUtil.error(requireActivity(), title)?.show()
    }

    open fun showSuccess(title: String?) {
        ToastUtil.success(requireActivity(), title)?.show()
    }

    //Permission
// List quyền cần yêu cầu
//    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//    } else {
//        arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//    }

    private val _permissionCallbackData = MutableStateFlow<ArrayList<PermissionState>>(arrayListOf())
    val callBackPermissionData: StateFlow<ArrayList<PermissionState>> = _permissionCallbackData.asStateFlow()

    val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var callBackList :ArrayList<PermissionState> = arrayListOf()
            val totalPermissions = permissions.size
            var processedPermissions = 0
            permissions.entries.forEach { entry ->

                val permission = entry.key
                val isGranted = entry.value
                val isPermissionDenied = ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_DENIED

                // Kiểm tra xem người dùng đã từ chối quyền trước đó hay chưa
                val shouldShowRationale = shouldShowRequestPermissionRationale(permission)
                if (isGranted) {
                    Toast.makeText(requireContext(), "$permission granted", Toast.LENGTH_SHORT).show()
                    Timber.d("$permission dc chap nhan")
                }else if (isPermissionDenied && shouldShowRationale) {
                    Timber.d("Bị từ chối quyền  asd a nên giải thích lại và request quyền")
                    callBackList.add(PermissionState(permission, state = State.DENIED))
                }else {
                    Timber.d("Bị từ chối quyền vĩnh viễn, vào cài đặt")
                    Toast.makeText(requireContext(), "$permission denied", Toast.LENGTH_SHORT).show()
                    callBackList.add(PermissionState(permission, state = State.PERMANENTLY_DENIED))
                }
                processedPermissions++
                if (processedPermissions == totalPermissions) {
                    _permissionCallbackData.value=callBackList
                }
            }
        }

    fun openAppSettings() {
        try {
            if (context == null) return
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context?.packageName, null)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Hàm kiểm tra xem đã cấp tất cả quyền chưa
    /**
     * @param permissions List quyền cần kiểm tra
     * vd:   Manifest.permission.READ_MEDIA_IMAGES,
     *                     Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
     *                     Manifest.permission.CAMERA
     */
    fun handlePermission(permissions:Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Function for handling view after transition end
     * Override this function to call api or something
     */


    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        // Trường hợp KHÔNG CÓ ANIMATION (Dành cho Splash hoặc màn đầu tiên)
        if (nextAnim == 0) {
            if (enter && !isHandlerInvoked) {
                onEnterAnimationComplete()
            }
            return null
        }
        try {
            val animator = AnimatorInflater.loadAnimator(requireContext(), nextAnim)
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // Animation has started
                    Timber.d("QuangDV onCreateAnimator : onAnimationStart")
                }

                override fun onAnimationEnd(animation: Animator) {
                    // Animation has ended
                    Timber.d("QuangDV onCreateAnimator: onAnimationEnd")
                    if (enter) onEnterAnimationComplete()
                }

                override fun onAnimationCancel(animation: Animator) {
                    // Animation was canceled
                    Timber.d("QuangDV onCreateAnimator: onAnimationCancel")
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // Animation is repeating
                    Timber.d("QuangDV onCreateAnimator: onAnimationRepeat")
                }
            })
            return animator
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim == 0) {
            if (enter && !isHandlerInvoked) {
                onEnterAnimationComplete()
            }
            return null
        }
        try {
            val animation = AnimationUtils.loadAnimation(context, nextAnim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    // Animation has started
                    Timber.d("QuangDV onCreateAnimation2: onAnimationStart")
                }

                override fun onAnimationEnd(animation: Animation) {
                    // Animation has ended

                    if (enter) {
                        Timber.d("QuangDV onCreateAnimation2: onAnimationEnd")
                        if (enter) onEnterAnimationComplete()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // Animation is repeating
                    Timber.d("QuangDV onCreateAnimation2: onAnimationRepeat")
                }
            })
            return animation
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    /**
     * End update
     */
}