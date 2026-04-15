package com.el.mybasekotlin.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by ElChuanmen on 7/24/2024.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@Serializable
@Parcelize
data class Notice(
    @SerializedName("id") val id: String? = "-1",
    @SerializedName("image") val image: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("content") val content: String? = "",
    @SerializedName("ref_type") val refType: String? = "",
    @SerializedName("ref_id") val refId: String? = "",
    @SerializedName("refAction") val refAction: String? = "",
    @SerializedName("created_at") val created_at: String? = "",
    @SerializedName("createdAt_ts") val createdAt_ts: String? = "",
    @SerializedName("createdAt") val createdAt: String? = "",
    @SerializedName("time") val time: String? = "",
    @SerializedName("is_read") var isRead: Int? = 0,
    @SerializedName("images") val images: ArrayList<String> = arrayListOf()
) : Parcelable

open class Animal { // Nếu không có 'open', class này sẽ không thể kế thừa
    open fun makeSound() {
        println("Animal makes a sound")
    }
}
class Dog : Animal(){
    override fun makeSound() {
        super.makeSound()
    }
}