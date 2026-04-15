package com.el.mybasekotlin.data.model.game

import android.content.Context
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.el.mybasekotlin.ui.fragment.camera.DetectType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize



/**
 * Thể loại game ,  1 game có nhiều biến thể
 *
 */
enum class GameType(
    val value: Int,
    val description: String // mô tả game cho dev và design dễ hiểu.
) {
    RANKING_FILTER_GAME(1, "Đây là game Ranking - chọn hình theo yêu cầu"),//dang tắt
    MATH_RUN_GAME(2, "Đây là game Math Run - chạy và giải toán");

    companion object {
        fun from(value: Int): GameType =
            GameType.entries.find { it.value == value } ?: GameType.RANKING_FILTER_GAME
    }
}

/**
 * Xác định content game là customview hay fragment
 */
enum class GameDetailsContentType(
    val value: Int,
    val description: String // mô tả game cho dev và design dễ hiểu.
) {
    FRAGMENT(1, "Details game được xây bằng fragment"),//dang tắt
    CUSTOMVIEW(2, "Details Game được xây bằng customview");

    companion object {
        fun from(value: Int): GameDetailsContentType =
            GameDetailsContentType.entries.find { it.value == value } ?: GameDetailsContentType.CUSTOMVIEW
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
    val id: Int = 0,  // id game,id filter
    @SerializedName("categorizeID") // filter by category
    val gameType: Int = GameType.RANKING_FILTER_GAME.value,
    @SerializedName("detailsType") // filter by category
    val detailsType: Int = GameDetailsContentType.CUSTOMVIEW.value,
    @SerializedName("name")
    val name: String = "", // game name, tên hiển thị ở ngoài
    @SerializedName("image") //thumb
    val image: String = "", //  imageName =>> get file từ asset
    @SerializedName("likeCount")
    val likeCount: String = "", // số lượt thích
    @SerializedName("minTimeToPlay")
    val minTimeToPlay: Long = 60000,  // min time  để chơi game ==>> config  này dùng để set mặc định cho record
    @SerializedName("count_down_time")
    val countDownTimer: Long = 0,  //đếm ngược để chơi game, không có đếm thì =0 (một số game có đếm ngược, 1 số game không có)
    @SerializedName("gameResult")
    val gameResult: String = "link_video_record", //Link video record game (dùng cho tab My Video)
    @SerializedName("pos")
    val pos: Int, // vị trí game trên list
    @SerializedName("enable")
    val enable: Boolean, // tắt bật game theo config
    @SerializedName("tagPos")
    val tagPos: Int,
    @SerializedName("tag")
    val tag: Int,
    @SerializedName("gameResourcePath") // tài nguyên game trong asset  nên bằng id game vd : 101,102,223
    val gameResourcePath: Int, // ID game resource mapping với list data game , theo [GameType] . (Ranking game, Tap, MathRun), vd: nếu id  data

    /**
     * Data từng game có 3 giải pháp
     * 1. dùng id để mapping lấy ra từ list data
     * 2. Dùng json string rồi convert sang object sau. phần convert tự xử lý trong từng viewModel của từng game
     * 3.
     */
    @SerializedName("gameContentDataPath") // tài nguyên game trong asset nên bằng id game vd : 101,102,223
    val gameContentDataPath: String = "",// Lưu dưới dạng JsonElement chung
    @SerializedName("cameraDetectType")
    val cameraDetectType: Int = DetectType.FACE_DETECT.value,  // setup camera cho game ,


    ) : Parcelable {
    /**
     * Hàm tiện ích giúp parse gameData ra một đối tượng cụ thể (GameData1, GameData2...)
     * ở trên tầng ViewModel hoặc UseCase.
     */
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