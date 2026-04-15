package com.el.mybasekotlin.ui.fragment.camera

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Cấu hình cho từng loại Detect
 */
@Parcelize
data class CameraConfig(
    @SerializedName("detectType")
    val detectType: Int = DetectType.NONE.value,
    @SerializedName("detectedMode")
    val detectedMode: DetectionMode = DetectionMode.BASIC,
    @SerializedName("isFrontCamera")
    val isFrontCamera: Boolean=true
) : Parcelable

enum class DetectType(
    val value: Int,
    val description: String // mô tả game cho dev và design dễ hiểu.
) {
    NONE(0, "Không detect, chỉ cần camera preview"),//dang tắt
    FACE_DETECT(1, "Detect đầu"),
    HAND_DETECT(2, "Detect đầu"),
    BODY_DETECT(3, "BODY"),
    FULL_BODY(4, "FULL BODY"),
    FACE_HAND_DETECT(5, "Detect đầu + tay");

    companion object {
        fun from(value: Int): DetectType =
            DetectType.entries.find { it.value == value } ?: DetectType.NONE
    }
}
enum class DetectionMode {
    BASIC,    // Chỉ lấy góc đầu
    FULL  ,// Lấy đủ landmark & trạng thái
    NONE  // NOne = base
}
