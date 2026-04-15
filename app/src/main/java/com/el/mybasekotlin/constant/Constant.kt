package vn.chayluoi.stream.app.constant

import android.content.res.Resources
import com.el.mybasekotlin.utils.extension.dpToPx


object Constant {
    const val AnimDuration: Long = 200
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val density = Resources.getSystem().displayMetrics.density
    val bottomBarHeight = density * 88f
    val pipWidth = (screenWidth * 0.6).toInt()
    val pipHeight = pipWidth * 9 / 16
    const val miniPlayerPadding = 20f
    const val NOTIFICATION_CHANNEL_ID = "ticket_channel_id"
    val spacingVerticalVideo = "24".dpToPx()
    val spacingHorizontal = "9".dpToPx()
    const val AutoBitRate = 44000000
    const val DEFAULT_BIRTH_DAY = "10/01/2000"

    enum class ObjType(val value: Int) : java.io.Serializable {
        LiveStream(1), User(2), Video(3), Category(4), Report(5), Comment(6), Follow(7), Marketing(8), Gift(
            9
        ),
        Transaction(10);

        companion object {
            fun fromValue(value: Int) = values().firstOrNull { it.value == value }
        }
    }

    enum class SupportType(val value: String) {
        TERM_OF_SERVICE("terms"), PRIVACY_POLICY("privacy"), ABOUT_US("aboutus"), CONTACT_METHOD("contact"), DISCLAIMER(
            "indemnity"
        )
    }

    enum class StreamStatus(val value: Int) {
        LiveStream(1), OffStream(0)
    }


    /**
     * No internet
     */
    const val NO_INTERNET = "NO_INTERNET"

    /**
     * Auth
     */
    const val INVALID_PASSWORD = 6
    const val PHONE_NUMBER_IS_REGISTERED = 1
    const val PHONE_NUMBER_IS_NOT_VALID = 1
    const val PHONE_NUMBER_IS_UNREGISTER = 2
    const val PHONE_NUMBER_IS_NOT_VALID_TO_REGISTER = 2
    const val ENTERED_THE_WRONG_PASSORD = 155

    /**
     * Profile
     */
    enum class Gender(val value: Int) {
        Male(1), Female(2), Other(3);

        companion object {
            fun fromValue(value: Int) = values().firstOrNull { it.value == value }
        }
    }

    const val MAX_USER_PHOTO = 5

    enum class NotiRefType(val value: String) {
        NEWS("news"),
        EVENT("event"),
        GAME("game"),
    }

    //Firebase Notice
    const val FIREBASE_NEW_TOKEN = "MyFirebaseMessageNewToken"
    const val FIREBASE_NEW_NOTICE = "MyFirebaseNewNotice"

    /*
    * KAM
    * */
    const val DEFAULT_KAM_URL = "https://xensear.vn/kam/homescreen?kamExperienceId=KAMGiftWYF"

}