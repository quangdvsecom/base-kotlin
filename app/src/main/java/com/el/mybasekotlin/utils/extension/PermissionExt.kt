package com.el.mybasekotlin.utils.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import timber.log.Timber

/**
 * Created by ElChuanmen on 1/15/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
fun checkPerNotice(app: Context): Boolean {
    return !NotificationManagerCompat.from(app)
        .areNotificationsEnabled()
}

/**
 * call this method after showing a rationale message to the user
 */
fun requestPermission(context: ComponentActivity, permission: String, callBack: () -> Unit) {
    val isPermissionDenied = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_DENIED
    val shouldShowRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            permission
        )

    val permissionLauncher = context.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
//            isGranted -> onGranted() // Người dùng cấp phép
//            shouldShowRationale-> onDenied?.invoke() // Người dùng từ chối quyền
//            else -> onNeverAskAgain?.invoke() // Người dùng chọn "Không hỏi lại"
        }
    }

    if (isPermissionDenied && shouldShowRationale) {
        Timber.d("Bị từ chối quyền  asd a nên giải thích lại và request quyền")
        //Request permission again
    } else if (isPermissionDenied) {
        Timber.d("Bị từ chối quyền vĩnh viễn, vào cài đặt")
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        startActivity(context, intent, null)
    } else {
        permissionLauncher.launch(permission)

    }

}


fun Fragment.requestPermission(
    permission: String,
    rationaleMessage: String,
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onNeverAskAgain: (() -> Unit)? = null
) {
    // Kiểm tra xem quyền đã được cấp hay chưa
    if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
        onGranted() // Gọi callback khi quyền đã được cấp
        return
    }

    // Kiểm tra xem người dùng đã từ chối quyền trước đó hay chưa
    val shouldShowRationale = shouldShowRequestPermissionRationale(permission)

    // Đăng ký ActivityResultLauncher
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onGranted() // Người dùng cấp phép
            shouldShowRationale-> onDenied?.invoke() // Người dùng từ chối quyền
            else -> onNeverAskAgain?.invoke() // Người dùng chọn "Không hỏi lại"
        }
    }

    // Hiển thị thông báo giải thích nếu cần
    if (shouldShowRationale) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission required")
            .setMessage(rationaleMessage)
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(permission) // Yêu cầu quyền
            }
            .setNegativeButton("Cancel") { _, _ ->
                onDenied?.invoke() // Người dùng từ chối yêu cầu
            }
            .show()
    } else {
        // Nếu không cần giải thích, yêu cầu quyền ngay lập tức
        permissionLauncher.launch(permission)
    }
}


fun ComponentActivity.requestPermission(
    permission: String,
    rationaleMessage: String,
    onGranted: () -> Unit,
    onDenied: (() -> Unit)? = null,
    onNeverAskAgain: (() -> Unit)? = null
) {
    // Kiểm tra xem quyền đã được cấp hay chưa
    if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
        onGranted() // Gọi callback khi quyền đã được cấp
        return
    }

    // Kiểm tra xem người dùng đã từ chối quyền trước đó hay chưa
    val shouldShowRationale = shouldShowRequestPermissionRationale(permission)

    // Đăng ký ActivityResultLauncher
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onGranted() // Người dùng cấp phép
            shouldShowRationale-> onDenied?.invoke() // Người dùng từ chối quyền
            else -> onNeverAskAgain?.invoke() // Người dùng chọn "Không hỏi lại"
        }
    }

    // Hiển thị thông báo giải thích nếu cần
    if (shouldShowRationale) {
        AlertDialog.Builder(this)
            .setTitle("Permission required")
            .setMessage(rationaleMessage)
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(permission) // Yêu cầu quyền
            }
            .setNegativeButton("Cancel") { _, _ ->
                onDenied?.invoke() // Người dùng từ chối yêu cầu
            }
            .show()
    } else {
        // Nếu không cần giải thích, yêu cầu quyền ngay lập tức
        permissionLauncher.launch(permission)
    }
}
fun ComponentActivity.requestPermissionsList(
    permissions: List<String>,
    rationaleMessage: String,
    onAllGranted: () -> Unit,
    onPartialGranted: ((deniedPermissions: List<String>) -> Unit)? = null,
    onNeverAskAgain: ((neverAskPermissions: List<String>) -> Unit)? = null
) {
    // Kiểm tra những quyền đã được cấp và chưa được cấp
    val notGrantedPermissions = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

    if (notGrantedPermissions.isEmpty()) {
        // Nếu tất cả quyền đã được cấp, gọi callback
        onAllGranted()
        return
    }

    // Kiểm tra xem có cần hiển thị giải thích không
    val shouldShowRationale = notGrantedPermissions.any {
        shouldShowRequestPermissionRationale(it)
    }

    // Đăng ký ActivityResultLauncher
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val deniedPermissions = result.filterValues { !it }.keys.toList()
        val neverAskPermissions = deniedPermissions.filter { !shouldShowRequestPermissionRationale(it) }

        when {
            deniedPermissions.isEmpty() -> onAllGranted() // Tất cả quyền được cấp
            neverAskPermissions.isNotEmpty() -> onNeverAskAgain?.invoke(neverAskPermissions) // Người dùng chọn "Không hỏi lại"
            else -> onPartialGranted?.invoke(deniedPermissions) // Một số quyền bị từ chối
        }
    }

    // Hiển thị thông báo giải thích nếu cần
    if (shouldShowRationale) {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage(rationaleMessage)
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(notGrantedPermissions.toTypedArray())
            }
            .setNegativeButton("Cancel") { _, _ ->
                onPartialGranted?.invoke(notGrantedPermissions) // Từ chối tất cả
            }
            .show()
    } else {
        // Yêu cầu quyền ngay lập tức
        permissionLauncher.launch(notGrantedPermissions.toTypedArray())
    }
}
fun Fragment.requestPermissionsList(
    permissions: List<String>,
    rationaleMessage: String,
    onAllGranted: () -> Unit,
    onPartialGranted: ((deniedPermissions: List<String>) -> Unit)? = null,
    onNeverAskAgain: ((neverAskPermissions: List<String>) -> Unit)? = null
) {
    // Kiểm tra những quyền đã được cấp và chưa được cấp
    val notGrantedPermissions = permissions.filter {
        ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
    }

    if (notGrantedPermissions.isEmpty()) {
        // Nếu tất cả quyền đã được cấp, gọi callback
        onAllGranted()
        return
    }

    // Kiểm tra xem có cần hiển thị giải thích không
    val shouldShowRationale = notGrantedPermissions.any {
        shouldShowRequestPermissionRationale(it)
    }

    // Đăng ký ActivityResultLauncher
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val deniedPermissions = result.filterValues { !it }.keys.toList()
        val neverAskPermissions = deniedPermissions.filter { !shouldShowRequestPermissionRationale(it) }

        when {
            deniedPermissions.isEmpty() -> onAllGranted() // Tất cả quyền được cấp
            neverAskPermissions.isNotEmpty() -> onNeverAskAgain?.invoke(neverAskPermissions) // Người dùng chọn "Không hỏi lại"
            else -> onPartialGranted?.invoke(deniedPermissions) // Một số quyền bị từ chối
        }
    }

    // Hiển thị thông báo giải thích nếu cần
    if (shouldShowRationale) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permissions required")
            .setMessage(rationaleMessage)
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(notGrantedPermissions.toTypedArray())
            }
            .setNegativeButton("Cancel") { _, _ ->
                onPartialGranted?.invoke(notGrantedPermissions) // Từ chối tất cả
            }
            .show()
    } else {
        // Yêu cầu quyền ngay lập tức
        permissionLauncher.launch(notGrantedPermissions.toTypedArray())
    }
}