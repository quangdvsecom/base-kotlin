package com.el.mybasekotlin.data.model

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("status") var status: String = "",
    @SerializedName("msg") var msg: String? = "",
    @SerializedName("allow_share_fb") var allowShareFb: String = "",
    @SerializedName("allow_show_breaking_news") var allowShowBreakingNews: String = "",
    @SerializedName("force_clear_cache") var forceClearCache: String = "",
    @SerializedName("live_darkmode") var liveDarkMode: String = "",
    @SerializedName("isGift") var isGift: String = "",
    @SerializedName("desGift") var desGift: String = "",
    @SerializedName("logo") var logo: String = "",
    @SerializedName("time_thumnail")
    var timeThumnail: String? = null,
    @SerializedName("result")
    var results: ArrayList<Result>? = arrayListOf(),
    @SerializedName("objectAds")
    var objectAds: String? = "",
    @SerializedName("update")
    var update: Update = Update(),
)

data class Update(
    @SerializedName("version")
    var version: String = "",

    @SerializedName("update_url")
    var updateUrl: String = "",

    @SerializedName("is_forced_update")
    var isForcedUpdate: String? = null,

    @SerializedName("remind_day")
    var remindDay: String? = null,

    @SerializedName("versionShop")
    var versionShop: String? = null,

    @SerializedName("titleShop")
    var titleShop: String? = null,

    @SerializedName("titleUpdate")
    var titleUpdate: String? = null,

    @SerializedName("contentUpdate")
    var contentUpdate: String? = null,

    @SerializedName("contentShopUpdate")
    var contentShopUpdate: String? = null,
)

data class Result(
    @SerializedName("childs")
    var childses: ArrayList<Childs>? = null
)

data class Childs(
    @SerializedName("url")
    var url: String? = null,

    @SerializedName("icon")
    var icon: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("package_name")
    var package_name: String? = null,

    @SerializedName("isOtherApp")
    var isOtherApp: String? = null,

    @SerializedName("detailTitle")
    var detailTitle: String? = null,
)


data class ObjectAds(
    @SerializedName("UpdateApp")
    var updateApp: UpdateApp? ,
)

data class UpdateApp(
    @SerializedName("listForceUpdate")
    val listForceUpdate: String? = "0",
    @SerializedName("listSuggetUpdate")
    val listSuggetUpdate: String? = "0",
    @SerializedName("link_app")
    val linkApp: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("content")
    val content: String? = "",
)