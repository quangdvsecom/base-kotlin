package com.el.mybasekotlin.utils.extension

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Created by ElChuanmen on 12/2/2022.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */

fun EditText.showPassWord() {
    this.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    this.setSelection(this.text.length)
}

fun EditText.hidePassWord() {
    this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    this.setSelection(this.text.length)
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    })
}

class PasswordTextWatcher(editText: EditText?, val afterPasswordChanged: (String) -> Unit) :
    TextWatcher {
    private val editTextWeakReference: WeakReference<EditText> = WeakReference<EditText>(editText)

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable) {
        val editText: EditText = editTextWeakReference.get() ?: return
        val s: String = editable.toString()
        if (s.isEmpty()) return
        editText.removeTextChangedListener(this)
        val result = s.trim()
        editText.setText(result)
        editText.setSelection(result.length)
        editText.addTextChangedListener(this)
        afterPasswordChanged.invoke(result)
    }
}


/**
 * Valid number
 */
val headerPhoneNumber = arrayOf(
    "032",
    "033",
    "034",
    "035",
    "036",
    "037",
    "038",
    "039",
    "097",
    "098",
    "096",
    "070",
    "079",
    "077",
    "076",
    "078",
    "090",
    "093",
    "083",
    "084",
    "085",
    "081",
    "082",
    "088",
    "091",
    "094",
    "056",
    "058",
    "059",
    "092",
    "099"
)

fun isValidNumber(data: String): Boolean {
    var isValid = false
    var phone = ""
    if (data.substring(0, 2).equals("84")) {
        phone = "0" + data.substring(2, data.length)
    } else phone = data
    Timber.d("PhoneNumber : $phone")
    val headerPhone = phone.substring(0, 3)
    if (headerPhone in headerPhoneNumber) isValid = true
    return isValid
}

//const val INVALID_PASSWORD_LENGTH = 1
//const val INVALID_PASSWORD_SPACE = 2
//fun isValidPassword(data: String, context: Context): com.el.mybasekotlin.utils.extension.ValidData {
//    if (data.length < 6) {
//        return com.el.mybasekotlin.utils.extension.ValidData(
//            false, INVALID_PASSWORD_LENGTH, context.getString(R.string.invalid_password_length)
//        )
//    } else if (data.substring(0, 1).equals(" ") || data.substring(data.length - 1).equals(" ")) {
//        return com.el.mybasekotlin.utils.extension.ValidData(
//            false, INVALID_PASSWORD_SPACE, context.getString(R.string.invalid_password_space)
//        )
//    }
//    return com.el.mybasekotlin.utils.extension.ValidData(true, 0, "msg")
//}

//Class data to valid
data class ValidData(var isValid: Boolean, var code: Int, var msg: String)