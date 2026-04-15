package com.el.mybasekotlin.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by ElChuanmen on 12/9/2024.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */


@Serializable
@Parcelize
data class Setting(
    @SerializedName("event_id") val eventId: String? = "",
    @SerializedName("data") val data: DataSetting
) : Parcelable {
}

@Serializable
@Parcelize
data class DataSetting(
    @SerializedName("main") val main: MainSetting,
//    @SerializedName("event") val event: EventSetting,
    @SerializedName("tabSetting") val tabSetting: MutableList<TabSetting>,
//    @SerializedName("header") val header: HeaderSetting,
    @SerializedName("pushMessage") val pushMessage: PushMessageSetting,
    @SerializedName("zone") val zone: ZoneSetting,
    @SerializedName("news") val news: NewsSetting,
    @SerializedName("giftCode") val giftCode: GiftCodeSetting,
    @SerializedName("activity") val activity: ActivitySetting,
) : Parcelable {
}

@Serializable
@Parcelize
data class MainSetting(
    @SerializedName("bgEventMainColor") val bgEventMainColor: String? = "",
    @SerializedName("bgEventSubColor") val bgEventSubColor: String? = "",
    @SerializedName("bgMainTabColor") val bgMainTabColor: String? = "",
    @SerializedName("mainTabActiveColor") val mainTabActiveColor: String? = "",
    @SerializedName("nainTabInactiveColor") val nainTabInactiveColor: String? = "",
    @SerializedName("textDefaultColor") val textDefaultColor: String? = "",
    @SerializedName("textHightlightColor") val textHightlightColor: String? = "",
    @SerializedName("textSubColor") val textSubColor: String? = "",
    @SerializedName("bgBoxContentColor") val bgBoxContentColor: String? = "",
    @SerializedName("bgBoxNotiColor") val bgBoxNotiColor: String? = "",
    @SerializedName("bgGameColor") val bgGameColor: String? = "",
    @SerializedName("dialogColor") val dialogColor: String? = "",
    @SerializedName("bgSubTabColor") val bgSubTabColor: String? = "",
    @SerializedName("textSubTabColor") val textSubTabColor: String? = "",
    @SerializedName("bgSubTabActiveColor") val bgSubTabActiveColor: String? = "",
    @SerializedName("textSubTabActiveColor") val textSubTabActiveColor: String? = "",
    @SerializedName("bgInputColor") val bgInputColor: String? = "",
    @SerializedName("textInputColor") val textInputColor: String? = "",
    @SerializedName("bgLabelColor") val bgLabelColor: String? = "",
    @SerializedName("textLabelColor") val textLabelColor: String? = "",
    @SerializedName("bgTagColor") val bgTagColor: String? = "",
    @SerializedName("textTagColor") val textTagColor: String? = "",
    @SerializedName("numberSmallColor") val numberSmallColor: String? = "",
    @SerializedName("bgVoucherColor") val bgVoucherColor: String? = "",
    @SerializedName("bgVoucherUsedColor") val bgVoucherUsedColor: String? = "",
    @SerializedName("textVoucherUsedColor") val textVoucherUsedColor: String? = "",
    @SerializedName("bgVoucherExpiredColor") val bgVoucherExpiredColor: String? = "",
    @SerializedName("textVoucherExpiredColor") val textVoucherExpiredColor: String? = "",
    @SerializedName("iconVoucherExpiredColor") val iconVoucherExpiredColor: String? = "",
    @SerializedName("placeholderInputLogin") val holderInputLogin: String? = "",
    @SerializedName("login_method") val loginMethod: String? = "0",
    @SerializedName("enable_provider_login") val enableProviderLogin: String? = "0",
) : Parcelable {
    fun isLoginProviderEnabled() = enableProviderLogin == "1"
}

@Serializable
@Parcelize
data class ActivitySetting(
    @SerializedName("background") val background: String? = "",
    @SerializedName("textTitleColor") val textTitleColor: String? = "",
    @SerializedName("textEventNameColor") val textEventNameColor: String? = "",
    @SerializedName("textMonthColor") val textMonthColor: String? = "",
    @SerializedName("textDayColor") val textDayColor: String? = "",
    @SerializedName("dotTimeLineColor") val dotTimeLineColor: String? = "",
    @SerializedName("textTitleEventColor") val textTitleEventColor: String? = "",
    @SerializedName("iconTimeColor") val iconTimeColor: String? = "",
    @SerializedName("textInfoColor") val textInfoColor: String? = "",
    @SerializedName("overlay") val overlay: Overlay?
) : Parcelable {
}

@Serializable
@Parcelize
data class PushMessageSetting(
    @SerializedName("background") val background: String? = "",
    @SerializedName("titleColor") val titleColor: String? = "",
    @SerializedName("inputBackground") val inputBackground: String? = "",
    @SerializedName("textInput") val textInput: String? = "",
) : Parcelable {
}

@Serializable
@Parcelize
data class ZoneSetting(
    @SerializedName("background") val background: String? = "",
    @SerializedName("backgroundItemZone") val backgroundItemZone: String? = "",
    @SerializedName("enableQrCode") val enableQrCode: Int? = 1,
    @SerializedName("titleColor") val titleColor: String? = "",
    @SerializedName("voucherIconColor") val voucherIconColor: String? = "",
    @SerializedName("overlay") val overlay: Overlay?,
) : Parcelable {
}

@Serializable
@Parcelize
data class NewsSetting(
    @SerializedName("background") val background: String? = ""
) : Parcelable {
}

@Serializable
@Parcelize
data class GiftCodeSetting(
    @SerializedName("textTitleColor") val textTitleColor: String? = "",
    @SerializedName("buttonColor") val buttonColor: String? = "",
    @SerializedName("textButtonColor") val textButtonColor: String? = "",
    @SerializedName("textInputColor") val textInputColor: String? = "",
    @SerializedName("backgroundInputColor") val backgroundInputColor: String? = "",
) : Parcelable {
}

@Serializable
@Parcelize
data class Overlay(
    @SerializedName("startColor") val startColor: String? = "",
    @SerializedName("endColor") val endColor: String? = ""
) : Parcelable {
}

@Serializable
@Parcelize
data class TabSetting(
    @SerializedName("id") val id: String? = "",
    @SerializedName("name") val name: String? = ""
) : Parcelable {
}

@Serializable
@Parcelize
data class ButtonSetting(
    @SerializedName("id") val id: String? = "",
    @SerializedName("image") val image: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("text_color") val textColor: String? = "",
    @SerializedName("gradient") val gradient: Gradient?
) : Parcelable {
}


@Serializable
@Parcelize
data class Gradient(
    @SerializedName("startColor") val startColor: String? = "",
    @SerializedName("endColor") val endColor: String? = "",
    @SerializedName("startPoint") val startPoint: PointDraw?,
    @SerializedName("endPoint") val endPoint: PointDraw?
) : Parcelable {
}

@Serializable
@Parcelize
data class PointDraw(
    @SerializedName("x") val x: Int? = 0,
    @SerializedName("y") val y: Int? = 0,
) : Parcelable {
}

@Serializable
@Parcelize
data class LogoEvent(
    @SerializedName("icon") val icon: String = "",
    @SerializedName("width") val width: String = "",
    @SerializedName("height") val height: String = "",
) : Parcelable {
}