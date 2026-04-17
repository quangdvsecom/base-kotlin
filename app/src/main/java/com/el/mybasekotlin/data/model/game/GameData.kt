package com.el.mybasekotlin.data.model.game

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.el.mybasekotlin.ui.fragment.camera.DetectType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Thể loại game ,  1 game có nhiều biến thể
 */
enum class GameType(
    val value: Int,
    val description: String 
) {
    RANKING_FILTER_GAME(1, "Đây là game Ranking - chọn hình theo yêu cầu"),
    MATH_RUN_GAME(2, "Đây là game Math Run - chạy và giải toán");

    companion object {
        fun from(value: Int): GameType =
            GameType.entries.find { it.value == value } ?: RANKING_FILTER_GAME
    }
}

/**
 * Xác định content game là customview hay fragment
 */
enum class GameDetailsContentType(
    val value: Int,
    val description: String 
) {
    FRAGMENT(1, "Details game được xây bằng fragment"),
    CUSTOMVIEW(2, "Details Game được xây bằng customview");

    companion object {
        fun from(value: Int): GameDetailsContentType =
            GameDetailsContentType.entries.find { it.value == value } ?: CUSTOMVIEW
    }
}

/**
 * Base Data
 */
@Entity
@Parcelize
data class GameData(
    @PrimaryKey
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("categorizeID")
    val gameType: Int = GameType.RANKING_FILTER_GAME.value,
    @SerializedName("detailsType")
    val detailsType: Int = GameDetailsContentType.CUSTOMVIEW.value,
    @SerializedName("name")
    val name: String? = "", 
    @SerializedName("image")
    val image: String? = "", 
    @SerializedName("likeCount")
    val likeCount: String? = "", 
    @SerializedName("minTimeToPlay")
    val minTimeToPlay: Long = 60000,
    @SerializedName("count_down_time")
    val countDownTimer: Long = 0,
    @SerializedName("gameResult")
    val gameResult: String? = "link_video_record",
    @SerializedName("pos")
    val pos: Int = 0,
    @SerializedName("enable")
    val enable: Boolean = true,
    @SerializedName("tagPos")
    val tagPos: Int = 0,
    @SerializedName("tag")
    val tag: Int = 0,
    @SerializedName("gameResourcePath")
    val gameResourcePath: Int = 0,
    @SerializedName("gameContentDataPath")
    val gameContentDataPath: String? = "",
    @SerializedName("cameraDetectType")
    val cameraDetectType: Int = DetectType.FACE_DETECT.value,
) : Parcelable {
    inline fun <reified T> getParsedGameData(): T? {
        return try {
            gameContentDataPath?.let { com.google.gson.Gson().fromJson(it, T::class.java) }
        } catch (e: Exception) {
            null
        }
    }
}

@Entity
data class GameData1(
    @PrimaryKey
    @SerializedName("imgName")
    val image: String = "imageName",
    @SerializedName("imgURL")
    val imgURL: String = "imgURL",
)

@Entity
data class GameData2(
    @PrimaryKey
    @SerializedName("video")
    val video: String = "video",
    @SerializedName("videoURL")
    val videoURL: String = "videoURL",
)

@Entity
data class ImageData(
    @PrimaryKey
    @SerializedName("imgName")
    val image: String = "imageName",
    @SerializedName("imgURL")
    val imgURL: String = "imgURL",
    @SerializedName("thumb")
    val thumb: String = "thumb",
    @SerializedName("imageWidth")
    val imageWidth: String = "imageWidth",
    @SerializedName("imageHeight")
    val imageHeight: String = "imageHeight"
)
