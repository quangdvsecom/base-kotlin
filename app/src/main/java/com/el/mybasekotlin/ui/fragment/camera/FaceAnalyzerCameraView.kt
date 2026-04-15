package com.el.mybasekotlin.ui.fragment.camera

import android.graphics.PointF
import android.media.Image
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor

class FaceAnalyzerCameraView(
    private val detectionMode: DetectionMode,
    private var onResult: (FaceStateResult, Frame) -> Unit
) : FrameProcessor {

    private val detector = FaceDetection.getClient(
        getFaceDetectorOptions(detectionMode)
    )

    override fun process(frame: Frame) {
        try {
            val frameWidth = frame.size.width
            val frameHeight = frame.size.height
            val rotation = frame.rotationToView
            val mediaImage = frame.getData<Image>() ?: return
            // Copy data ra NV21 byte array trước khi frame bị recycle
            val nv21 = yuv420ToNv21(mediaImage, frameWidth, frameHeight)
            val image = InputImage.fromByteArray(
                nv21, frameWidth, frameHeight, rotation,
                InputImage.IMAGE_FORMAT_NV21
            )

            detector.process(image)
                .addOnSuccessListener { faces ->
                    timber.log.Timber.d("FaceAnalyzer: faces detected = ${faces.size}")
                    faces.firstOrNull()?.let { face ->
                    val headEulerX = face.headEulerAngleX
                    val headEulerY = face.headEulerAngleY
                    val headEulerZ = face.headEulerAngleZ
                    val directions = mutableListOf<HeadDirection>()

                    if (headEulerY > 15) directions.add(HeadDirection.RIGHT)
                    else if (headEulerY < -15) directions.add(HeadDirection.LEFT)

                    if (headEulerX > 10) directions.add(HeadDirection.DOWN)
                    else if (headEulerX < -10) directions.add(HeadDirection.UP)

                    if (headEulerZ > 10) directions.add(HeadDirection.TILT_LEFT)
                    else if (headEulerZ < -10) directions.add(HeadDirection.TILT_RIGHT)

                    if (directions.isEmpty()) directions.add(HeadDirection.CENTER)

                    val isLeftEyeOpen = face.leftEyeOpenProbability?.let { it > 0.5 } ?: true
                    val isRightEyeOpen = face.rightEyeOpenProbability?.let { it > 0.5 } ?: true
                    val eyeState = when {
                        !isLeftEyeOpen && !isRightEyeOpen -> EyeState.BOTH_CLOSED
                        !isLeftEyeOpen -> EyeState.LEFT_CLOSED
                        !isRightEyeOpen -> EyeState.RIGHT_CLOSED
                        else -> EyeState.BOTH_OPEN
                    }

                    val isSmiling = face.smilingProbability?.let { it > 0.5 } ?: false
                    val mouthState = if (isSmiling) MouthState.OPEN_LAUGH else MouthState.CLOSE

//                    leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
//                    rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
                    val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)?.position?.let {
                        PointF(
                            it.x,
                            it.y
                        )
                    }
                    val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position?.let {
                        PointF(
                            it.x,
                            it.y
                        )
                    }

                    val noseBase = face.getLandmark(FaceLandmark.NOSE_BASE)?.position
                    val mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT)?.position
                    val mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
                    val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)?.position
                    val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)?.position

                    onResult.invoke(
                        FaceStateResult(
                            directions = directions,
                            angleX = headEulerX,
                            angleY = headEulerY,
                            angleZ = headEulerZ,
                            eyeState = eyeState,
                            mouthState = mouthState,
                            leftEyePosition = leftEye,
                            rightEyePosition = rightEye,
                            noseBasePosition = noseBase,
                            mouthLeftPosition = mouthLeft,
                            mouthRightPosition = mouthRight,
                            mouthBottomPosition = mouthBottom,
                            leftEarPosition = leftEar,
                            rightEarPosition = rightEar,
                            frameWidth = frameWidth,       // copy số nguyên ra
                            frameHeight = frameHeight,
                            rotation = rotation,
                            face = face
                        ), frame
                    )


                }
            }
            .addOnFailureListener { e ->
                timber.log.Timber.e(e, "FaceAnalyzerCameraView: Face detection failed")
            }
        } catch (e: Exception) {
            timber.log.Timber.e(e, "FaceAnalyzer: process() crashed")
        }
    }

    /**
     * Convert YUV_420_888 Image sang NV21 byte array (copy synchronous, an toàn cho async processing)
     */
    private fun yuv420ToNv21(image: Image, width: Int, height: Int): ByteArray {
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]

        val ySize = width * height
        val nv21 = ByteArray(ySize + ySize / 2)

        // Copy Y plane
        val yBuffer = yPlane.buffer
        val yRowStride = yPlane.rowStride
        var pos = 0
        for (row in 0 until height) {
            yBuffer.position(row * yRowStride)
            yBuffer.get(nv21, pos, width)
            pos += width
        }

        // Copy VU interleaved
        val vBuffer = vPlane.buffer
        val uBuffer = uPlane.buffer
        val uvRowStride = vPlane.rowStride
        val uvPixelStride = vPlane.pixelStride

        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val uvIndex = row * uvRowStride + col * uvPixelStride
                nv21[pos++] = vBuffer.get(uvIndex)
                nv21[pos++] = uBuffer.get(uvIndex)
            }
        }
        return nv21
    }
}

fun getFaceDetectorOptions(mode: DetectionMode): FaceDetectorOptions {
    val basicFaceOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // Ưu tiên tốc độ
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)        // Không cần điểm chi tiết
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // Không phân loại mắt, miệng
        .enableTracking() // Vẫn nên giữ nếu cần trackingId
        .build()
    val fullDetailFaceOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) // Cần cho landmark
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)            // Để lấy tọa độ mắt, mũi, miệng, tai
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // Trạng thái mắt, miệng
        .enableTracking()
        .build()
    return when (mode) {
        DetectionMode.BASIC -> basicFaceOptions
        DetectionMode.FULL -> fullDetailFaceOptions
        DetectionMode.NONE -> basicFaceOptions
    }
}

data class FaceStateResult(
    val directions: List<HeadDirection>,
    val angleX: Float,
    val angleY: Float,
    val angleZ: Float,
    val eyeState: EyeState,
    val mouthState: MouthState,
    val leftEyePosition: PointF? = null,
    val rightEyePosition: PointF? = null,
    val noseBasePosition: PointF? = null,
    val mouthLeftPosition: PointF? = null,
    val mouthRightPosition: PointF? = null,
    val mouthBottomPosition: PointF? = null,
    val leftEarPosition: PointF? = null,
    val rightEarPosition: PointF? = null,
    val frameWidth: Int,
    val frameHeight: Int,
    val rotation: Int,
    val face: Face? = null,
)

//
//enum class FaceDetectionMode {
//    BASIC,    // Chỉ lấy góc đầu
//    FULL,// Lấy đủ landmark & trạng thái
//    NONE
//}


enum class HeadDirection(val label: String, val icon: String) {
    LEFT("Nhìn trái", "⬅️"),
    RIGHT("Nhìn phải", "➡️"),
    UP("Ngẩng đầu", "⬆️"),
    DOWN("Cúi đầu", "⬇️"),
    TILT_LEFT("Nghiêng trái", "↙️"),
    TILT_RIGHT("Nghiêng phải", "↘️"),
    CENTER("Đầu thẳng", "🔲")
}

enum class MouthState(val label: String, val emoji: String) {
    OPEN_LAUGH("Há miệng / Cười", "😄"),
    CLOSE("Không cười / Không há miệng", "😐")
}

enum class EyeState(val label: String, val emoji: String) {
    BOTH_CLOSED("Nhắm cả 2 mắt", "😴"),
    LEFT_CLOSED("Nhắm mắt trái", "👁️"),
    RIGHT_CLOSED("Nhắm mắt phải", "👁️"),
    BOTH_OPEN("Mở cả 2 mắt", "👀")
}
