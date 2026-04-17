//package com.example.baseproject.presentation.screen.main.pigeonpop
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.ImageFormat
//import android.graphics.Matrix
//import android.graphics.Rect
//import android.graphics.RectF
//import android.graphics.YuvImage
//import android.util.Log
//import android.view.View
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.core.ImageProxy
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.baseproject.R
//import com.example.baseproject.base.BaseFragment
//import com.example.baseproject.databinding.FragmentPigeonPopBinding
//import com.google.android.filament.Colors
//import com.google.android.filament.Skybox
//import com.google.android.filament.Texture
//import com.google.android.filament.TextureSampler
//import com.google.android.filament.android.TextureHelper.setBitmap
//import io.github.sceneview.model.ModelInstance
//import io.github.sceneview.node.ImageNode
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.ByteArrayOutputStream
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import kotlin.math.min
//import kotlin.math.pow
//import io.github.sceneview.math.Size
//import io.github.sceneview.math.Direction
//class PigeonPopFragment3 :
//    BaseFragment<FragmentPigeonPopBinding>(FragmentPigeonPopBinding::inflate) {
//
//    private var imageCapture: ImageCapture? = null
//    private lateinit var cameraExecutor: ExecutorService
//    private var isCapturing = false
//    private var pigeonModelNode: io.github.sceneview.node.ModelNode? = null
//
//    private var lastTouchX = 0f
//    private var currentRotationY = 0f
//    private var isDraggingModel = false
//    private var ringNode: ImageNode? = null
//    private var TAG1 = "PigeonPop"
//    private fun setupModelDrag() {
//        binding.sceneView.setOnTouchListener { _, motionEvent ->
//            when (motionEvent.actionMasked) {
//                android.view.MotionEvent.ACTION_DOWN -> {
//                    lastTouchX = motionEvent.x
//                    isDraggingModel = true
//                    true
//                }
//
//                android.view.MotionEvent.ACTION_MOVE -> {
//                    if (!isDraggingModel) return@setOnTouchListener false
//
//                    val deltaX = motionEvent.x - lastTouchX
//                    lastTouchX = motionEvent.x
//
//                    val rotationSpeed = 0.35f
//                    currentRotationY += deltaX * rotationSpeed
//
//                    pigeonModelNode?.rotation = io.github.sceneview.math.Rotation(
//                        x = 0f,
//                        y = currentRotationY,
//                        z = 0f
//                    )
//                    true
//                }
//
//                android.view.MotionEvent.ACTION_UP,
//                android.view.MotionEvent.ACTION_CANCEL -> {
//                    isDraggingModel = false
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//
//    override fun FragmentPigeonPopBinding.initView() {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//        prepareInitialUi()
//        checkCameraPermissionAndStart()
//        setup3DModel()
//        setupModelDrag()
//    }
//
//    override fun FragmentPigeonPopBinding.initListener() {
//        ivStartCapture.setOnClickListener {
//            if (!isCapturing) {
//                isCapturing = true
//                startPigeonPopSequence()
//            }
//        }
//
//        ivBack.setOnClickListener {
//            onBack()
//        }
//
//        tvRetry.setOnClickListener {
//            resetToCamera()
//        }
//    }
//
//    override fun initObserver() {}
//
//    override fun onBack() {
//        navigateTo(R.id.homeFragment, inclusive = true)
//    }
//
//    private fun prepareInitialUi() {
//        binding.sceneView.visibility = View.GONE
//        binding.viewFinder.visibility = View.VISIBLE
//        binding.ivBirdOutline.visibility = View.VISIBLE
//        binding.ivStartCapture.visibility = View.VISIBLE
//        binding.tvRetry.visibility = View.GONE
//        binding.layoutCountdown.visibility = View.GONE
//    }
//
//    private fun checkCameraPermissionAndStart() {
//        if (hasCameraPermission()) {
//            startCamera()
//            return
//        }
//
//        doRequestPermission(arrayOf(Manifest.permission.CAMERA), object : IPermissionListener {
//            override fun onAllow() {
//                startCamera()
//            }
//
//            override fun onNeverAskAgain(permission: String) {
//                showDialogRequestPermission(permission)
//            }
//        })
//    }
//
//    private fun hasCameraPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
//        cameraProviderFuture.addListener({
//            try {
//                val cameraProvider = cameraProviderFuture.get()
//
//                val preview = Preview.Builder().build().also {
//                    it.surfaceProvider = binding.viewFinder.surfaceProvider
//                }
//
//                imageCapture = ImageCapture.Builder()
//                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                    .build()
//
//                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//
//                cameraProvider.unbindAll()
//
//                cameraProvider.bindToLifecycle(
//                    viewLifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture
//                )
//            } catch (exc: Exception) {
//                Log.e(TAG1, "Camera start failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(requireContext()))
//    }
//
//    private fun startPigeonPopSequence() {
//        if (!hasCameraPermission()) {
//            isCapturing = false
//            checkCameraPermissionAndStart()
//            return
//        }
//
//        lifecycleScope.launch {
//            binding.layoutCountdown.visibility = View.VISIBLE
//            binding.ivStartCapture.visibility = View.GONE
//
//            binding.circleRing3.visibility = View.GONE
//            binding.circleRing2.visibility = View.GONE
//            binding.circleRing1.visibility = View.GONE
//
//            for (i in 3 downTo 1) {
//                binding.tvCountdown.text = i.toString()
//
//                binding.circleRing3.visibility = if (i == 3) View.VISIBLE else View.GONE
//                binding.circleRing2.visibility = if (i == 2) View.VISIBLE else View.GONE
//                binding.circleRing1.visibility = if (i == 1) View.VISIBLE else View.GONE
//
//                val activeRing = when (i) {
//                    3 -> binding.circleRing3
//                    2 -> binding.circleRing2
//                    else -> binding.circleRing1
//                }
//
//                activeRing.scaleX = 0f
//                activeRing.scaleY = 0f
//                activeRing.alpha = 0f
//                activeRing.animate()
//                    .scaleX(1f).scaleY(1f)
//                    .alpha(1f)
//                    .setDuration(300)
//                    .setInterpolator(android.view.animation.OvershootInterpolator(1.5f))
//                    .start()
//
//                binding.tvCountdown.scaleX = 0.5f
//                binding.tvCountdown.scaleY = 0.5f
//                binding.tvCountdown.animate()
//                    .scaleX(1f).scaleY(1f)
//                    .setDuration(300)
//                    .setInterpolator(android.view.animation.OvershootInterpolator(2f))
//                    .start()
//
//                delay(1000)
//            }
//
//            binding.layoutCountdown.animate()
//                .alpha(0f)
//                .setDuration(200)
//                .withEndAction {
//                    binding.layoutCountdown.visibility = View.GONE
//                    binding.layoutCountdown.alpha = 1f
//                    binding.circleRing3.visibility = View.GONE
//                    binding.circleRing2.visibility = View.GONE
//                    binding.circleRing1.visibility = View.GONE
//                }
//                .start()
//
//            delay(200)
//
//            captureAndProcess()
//        }
//    }
//
//    private fun captureAndProcess() {
//        val imageCapture = imageCapture ?: run {
//            restoreCaptureButton()
//            isCapturing = false
//            return
//        }
//
//        imageCapture.takePicture(
//            ContextCompat.getMainExecutor(requireContext()),
//            object : ImageCapture.OnImageCapturedCallback() {
//                override fun onCaptureSuccess(imageProxy: ImageProxy) {
//                    lifecycleScope.launch {
//                        try {
//                            val bitmap = withContext(Dispatchers.IO) {
//                                imageProxyToBitmap(imageProxy)
//                            }
//
//                            if (bitmap != null) {
//                                val squareBitmap = withContext(Dispatchers.Default) {
//                                    cropBitmapWithOffset(bitmap, cropScale = 1f)
//                                }
//
//                                withContext(Dispatchers.Main) {
//                                    val debugBitmap = squareBitmap.copy(Bitmap.Config.ARGB_8888, true)
//                                    val canvas = android.graphics.Canvas(debugBitmap)
//
//                                    val strokePaint = android.graphics.Paint().apply {
//                                        style = android.graphics.Paint.Style.STROKE
//                                        strokeWidth = 6f
//                                        isAntiAlias = true
//                                    }
//                                    val textPaint = android.graphics.Paint().apply {
//                                        color = android.graphics.Color.WHITE
//                                        textSize = 24f
//                                        isAntiAlias = true
//                                        setShadowLayer(3f, 1f, 1f, android.graphics.Color.BLACK)
//                                    }
//
//                                    val partConfigs = mapOf(
//                                        "head"         to RectF(0.15f, 0.15f, 0.85f, 0.85f),
//                                        "body"         to RectF(0.25f, 0.7f, 0.75f, 1f),
//                                        "tail"         to RectF(0.4f, 0.9f, 0.5f, 1.0f),
//                                        "polySurface7" to RectF(0.55f, 0.15f, 0.65f, 0.2f),
//                                        "polySurface8" to RectF(0.35f, 0.15f, 0.45f, 0.2f),
//                                        "polySurface3" to RectF(0.55f, 0.75f, 0.75f, 1f),
//                                        "polySurface4" to RectF(0.25f, 0.75f, 0.45f, 1f)
//                                    )
//
//                                    val debugColors = mapOf(
//                                        "head"         to android.graphics.Color.RED,
//                                        "body"         to android.graphics.Color.GREEN,
//                                        "tail"         to android.graphics.Color.BLUE,
//                                        "polySurface7" to android.graphics.Color.YELLOW,
//                                        "polySurface8" to android.graphics.Color.MAGENTA,
//                                        "polySurface3" to android.graphics.Color.CYAN,
//                                        "polySurface4" to android.graphics.Color.rgb(255, 128, 0),
//                                    )
//
//                                    partConfigs.forEach { (name, rotRect) ->
//                                        strokePaint.color = debugColors[name] ?: android.graphics.Color.WHITE
//                                        val left   = debugBitmap.width  * minOf(rotRect.left, rotRect.right)
//                                        val top    = debugBitmap.height * minOf(rotRect.top, rotRect.bottom)
//                                        val right  = debugBitmap.width  * maxOf(rotRect.left, rotRect.right)
//                                        val bottom = debugBitmap.height * maxOf(rotRect.top, rotRect.bottom)
//                                        canvas.drawRect(left, top, right, bottom, strokePaint)
//                                        canvas.drawText(name, left + 4f, top + 28f, textPaint)
//                                    }
//
//                                    strokePaint.color = android.graphics.Color.WHITE
//                                    strokePaint.strokeWidth = 30f
//                                    canvas.drawRect(0f, 0f, debugBitmap.width.toFloat(), debugBitmap.height.toFloat(), strokePaint)
//                                    binding.imgCropped.setImageBitmap(squareBitmap)
//                                    binding.imgTest.setImageBitmap(debugBitmap)
//                                }
//                                showResult(squareBitmap)
//                            } else {
//                                Log.e(TAG1, "Capture success but bitmap is null")
//                                restoreCaptureButton()
//                            }
//                        } catch (e: Exception) {
//                            Log.e(TAG1, "Processing failed", e)
//                            restoreCaptureButton()
//                        } finally {
//                            imageProxy.close()
//                            isCapturing = false
//                        }
//                    }
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Log.e(TAG1, "Capture failed: ${exception.message}", exception)
//                    restoreCaptureButton()
//                    isCapturing = false
//                }
//            }
//        )
//    }
//
//    private fun cropBitmapWithOffset(
//        bitmap: Bitmap,
//        cropScale: Float = 1f,
//        offsetXRatio: Float = 0f,
//        offsetYRatio: Float = 0f
//    ): Bitmap {
//        val cropSize = (min(bitmap.width, bitmap.height) * cropScale).toInt()
//
//        val centerX = bitmap.width / 2 + (bitmap.width * offsetXRatio).toInt()
//        val centerY = bitmap.height / 2 + (bitmap.height * offsetYRatio).toInt()
//
//        val left = (centerX - cropSize / 2).coerceIn(0, bitmap.width - cropSize)
//        val top = (centerY - cropSize / 2).coerceIn(0, bitmap.height - cropSize)
//
//        val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, cropSize, cropSize)
//        return croppedBitmap.copy(Bitmap.Config.ARGB_8888, true)
//    }
//
//    private fun restoreCaptureButton() {
//        binding.layoutCountdown.visibility = View.GONE
//        binding.ivStartCapture.visibility = View.VISIBLE
//    }
//
//    private fun setupRingNode() {
//        try {
//            ringNode = ImageNode(
//                materialLoader = binding.sceneView.materialLoader,
//                imageResId = R.drawable.ic_circle_ring2,
//                size = Size(2f, 2f),
//                center = io.github.sceneview.math.Position(0f),
//                normal = Direction(y = 1.0f)
//            ).apply {
//                position = io.github.sceneview.math.Position(
//                    x = 0f,
//                    y = -0.5f,
//                    z = -4.2f
//                )
//                rotation = io.github.sceneview.math.Rotation(
//                    x = 0f,
//                    y = 0f,
//                    z = 0f
//                )
//                isVisible = false
//            }
//
//            binding.sceneView.addChildNode(ringNode!!)
//            Log.d(TAG1, "setupRingNode: created")
//        } catch (e: Exception) {
//            Log.e(TAG1, "setupRingNode failed", e)
//        }
//    }
//
//    private fun setup3DModel() {
//        lifecycleScope.launch {
//            val modelInstance = binding.sceneView.modelLoader.loadModelInstance("cat_ver3.glb")
//            if (modelInstance != null) {
//                pigeonModelNode = io.github.sceneview.node.ModelNode(modelInstance).apply {
//                    position = io.github.sceneview.math.Position(x = 0f, y = -1.0f, z = -3.0f)
//                    scale = io.github.sceneview.math.Scale(4f)
//                    isVisible = false
//                }
//                binding.sceneView.addChildNode(pigeonModelNode!!)
//                setupRingNode()
//            }
//        }
//    }
//
//    private fun applyColorsToMeshes(modelInstance: ModelInstance) {
//        val asset = modelInstance.asset ?: run {
//            Log.e("PigeonPop", "Asset null, không thể tô màu!")
//            return
//        }
//
//        // Màu RGBA (0f–1f) cho từng khối theo thứ tự entity ID
//        val colorMap = mapOf(
//            "tail"         to floatArrayOf(1.0f, 0.2f, 0.2f, 1.0f), // Đỏ
//            "body"         to floatArrayOf(0.2f, 0.8f, 0.2f, 1.0f), // Xanh lá
//            "head"         to floatArrayOf(0.2f, 0.4f, 1.0f, 1.0f), // Xanh dương
//            "polySurface3" to floatArrayOf(1.0f, 0.8f, 0.0f, 1.0f), // Vàng
//            "polySurface4" to floatArrayOf(0.8f, 0.2f, 0.8f, 1.0f), // Tím
//            "polySurface7" to floatArrayOf(0.0f, 0.9f, 0.9f, 1.0f), // Cyan
//            "polySurface8" to floatArrayOf(0.5f, 0.9f, 0.9f, 1.0f), // Xanh ngọc nhạt
//        )
//
//        val engine = binding.sceneView.engine
//        val rm = engine.renderableManager
//
//        colorMap.forEach { (name, color) ->
//            val entityId = asset.getFirstEntityByName(name)
//            if (entityId == 0) {
//                Log.w("PigeonPop", "Không tìm thấy entity: $name")
//                return@forEach
//            }
//
//            // Kiểm tra entity có renderable không
//            if (!rm.hasComponent(entityId)) {
//                Log.w("PigeonPop", "Entity '$name' không có RenderableManager component")
//                return@forEach
//            }
//
//            val instance = rm.getInstance(entityId)
//            val primitiveCount = rm.getPrimitiveCount(instance)
//
//            for (i in 0 until primitiveCount) {
//                val materialInstance = rm.getMaterialInstanceAt(instance, i)
//                try {
//                    // Thử set baseColorFactor (PBR standard)
//                    materialInstance.setParameter(
//                        "baseColorFactor",
//                        Colors.RgbaType.SRGB,
//                        color[0], color[1], color[2], color[3]
//                    )
//                    Log.d("PigeonPop", "✅ Tô màu '$name' primitive[$i] thành công")
//                } catch (e: Exception) {
//                    Log.w("PigeonPop", "⚠️ baseColorFactor thất bại cho '$name'[$i]: ${e.message}")
//                    try {
//                        // Fallback: thử parameter tên khác
//                        materialInstance.setParameter(
//                            "baseColor",
//                            Colors.RgbaType.SRGB,
//                            color[0], color[1], color[2], color[3]
//                        )
//                        Log.d("PigeonPop", "✅ Tô màu '$name' primitive[$i] qua 'baseColor' thành công")
//                    } catch (e2: Exception) {
//                        Log.e("PigeonPop", "❌ Không thể tô màu '$name'[$i]: ${e2.message}")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
//        return try {
//            val rawBitmap = when (imageProxy.format) {
//                ImageFormat.JPEG -> {
//                    val buffer = imageProxy.planes[0].buffer
//                    val bytes = ByteArray(buffer.remaining())
//                    buffer.get(bytes)
//                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                }
//
//                ImageFormat.YUV_420_888 -> {
//                    val nv21 = yuv420888ToNv21(imageProxy)
//                    val yuvImage = YuvImage(
//                        nv21,
//                        ImageFormat.NV21,
//                        imageProxy.width,
//                        imageProxy.height,
//                        null
//                    )
//                    val out = ByteArrayOutputStream()
//                    yuvImage.compressToJpeg(
//                        Rect(0, 0, imageProxy.width, imageProxy.height),
//                        95,
//                        out
//                    )
//                    val jpegBytes = out.toByteArray()
//                    BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)
//                }
//                else -> null
//            }
//
//            rawBitmap?.let { bitmap ->
//                val rotation = imageProxy.imageInfo.rotationDegrees
//                if (rotation == 0) {
//                    bitmap
//                } else {
//                    val matrix = Matrix().apply {
//                        postRotate(rotation.toFloat())
//                    }
//                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private fun yuv420888ToNv21(image: ImageProxy): ByteArray {
//        val yBuffer = image.planes[0].buffer
//        val uBuffer = image.planes[1].buffer
//        val vBuffer = image.planes[2].buffer
//        val ySize = yBuffer.remaining()
//        val uSize = uBuffer.remaining()
//        val vSize = vBuffer.remaining()
//        val nv21 = ByteArray(ySize + uSize + vSize)
//        yBuffer.get(nv21, 0, ySize)
//        vBuffer.get(nv21, ySize, vSize)
//        uBuffer.get(nv21, ySize + vSize, uSize)
//        return nv21
//    }
//
//    private fun centerCropToSquare(bitmap: Bitmap): Bitmap {
//        val size = min(bitmap.width, bitmap.height)
//        val x = (bitmap.width - size) / 2
//        val y = (bitmap.height - size) / 2
//        return Bitmap.createBitmap(bitmap, x, y, size, size).copy(Bitmap.Config.ARGB_8888, true)
//    }
//
//    // ─── Show result ─────────────────────────────────────────────────────────
//    private fun showResult(faceBitmap: Bitmap) {
//        Log.d(TAG1, "showResult() called")
//
//        binding.viewFinder.visibility = View.GONE
//        binding.ivBirdOutline.visibility = View.GONE
//        binding.ivStartCapture.visibility = View.GONE
//        binding.sceneView.visibility = View.VISIBLE
//        binding.tvRetry.visibility = View.VISIBLE
//        binding.layoutResultTop.visibility = View.VISIBLE
//
//        val r = (254f / 255f).toDouble().pow(2.2).toFloat()
//        val g = (136f / 255f).toDouble().pow(2.2).toFloat()
//        val b = (39f  / 255f).toDouble().pow(2.2).toFloat()
//        binding.sceneView.scene.skybox = Skybox.Builder()
//            .color(r, g, b, 1f)
//            .build(binding.sceneView.engine)
//
//        ringNode?.isVisible = true
//        pigeonModelNode?.isVisible = true
//
//        binding.sceneView.post {
//            val engine = binding.sceneView.engine
//            val asset = pigeonModelNode?.modelInstance?.asset ?: return@post
//            val rm = engine.renderableManager
//
//            val partConfigs = mapOf(
//                "head"         to RectF(0.15f, 0.15f, 0.85f, 0.85f),
//                "body"         to RectF(0.25f, 0.7f, 0.75f, 1f),
//                "tail"         to RectF(0.4f, 0.9f, 0.5f, 1.0f),
//                "polySurface7" to RectF(0.55f, 0.15f, 0.65f, 0.2f),
//                "polySurface8" to RectF(0.35f, 0.15f, 0.45f, 0.2f),
//                "polySurface3" to RectF(0.55f, 0.75f, 0.75f, 1f),
//                "polySurface4" to RectF(0.25f, 0.75f, 0.45f, 1f)
//            )
//
//            // zoomScale > 1 = zoom IN  → lấy vùng nhỏ hơn từ ảnh gốc → ảnh to hơn trên model
//            // zoomScale < 1 = zoom OUT → lấy vùng rộng hơn từ ảnh gốc → ảnh nhỏ hơn trên model
//            // zoomScale = 1 = giữ nguyên
//            val zoomScaleMap = mapOf(
//                "head"         to 2.0f,
//                "body"         to 1.0f,
//                "tail"         to 1.0f,
//                "polySurface7" to 1.0f,
//                "polySurface8" to 1.0f,
//                "polySurface3" to 1.0f,
//                "polySurface4" to 1.0f
//            )
//
//            try {
//                partConfigs.forEach { (meshName, cropRect) ->
//                    val entityId = asset.getFirstEntityByName(meshName)
//                    if (entityId == 0 || !rm.hasComponent(entityId)) {
//                        Log.w(TAG1, "Không tìm thấy bộ phận: $meshName")
//                        return@forEach
//                    }
//
//                    val zoom = zoomScaleMap[meshName] ?: 1.0f
//                    val zoomedRect = zoomRect(cropRect, zoom)
//                    val partBitmap = cropBitmapByPercent(faceBitmap, zoomedRect)
//
//                    val texture = Texture.Builder()
//                        .width(partBitmap.width)
//                        .height(partBitmap.height)
//                        .sampler(Texture.Sampler.SAMPLER_2D)
//                        .format(Texture.InternalFormat.SRGB8_A8)
//                        .levels(1)
//                        .build(engine)
//
//                    setBitmap(engine, texture, 0, partBitmap)
//                    val sampler = TextureSampler()
//
//                    val instance = rm.getInstance(entityId)
//                    for (i in 0 until rm.getPrimitiveCount(instance)) {
//                        rm.getMaterialInstanceAt(instance, i)
//                            .setParameter("baseColorMap", texture, sampler)
//                    }
//                    Log.d(TAG1, "Đã gán ảnh cho: $meshName (zoom=$zoom)")
//                }
//
//                pigeonModelNode?.playAnimation(0, loop = true)
//
//            } catch (e: Exception) {
//                Log.e(TAG1, "Lỗi xử lý chia khối: ${e.message}", e)
//            }
//        }
//    }
//
//    /**
//     * Zoom vào/ra trung tâm của rect.
//     *
//     * zoom > 1  →  vùng crop NHỎ hơn (texture TO hơn trên model = zoom IN)
//     * zoom < 1  →  vùng crop LỚN hơn (texture NHỎ hơn trên model = zoom OUT)
//     * zoom = 1  →  giữ nguyên
//     *
//     * offsetX / offsetY: dịch chuyển tâm zoom (tỉ lệ 0..1 của ảnh gốc)
//     *   → dùng để chỉnh khi tâm cropRect không trùng tâm thật của đối tượng
//     *   → offsetX < 0: dịch trái, offsetX > 0: dịch phải
//     *   → offsetY < 0: dịch lên, offsetY > 0: dịch xuống
//     */
//    private fun zoomRect(rect: RectF, zoom: Float, offsetX: Float = 0f, offsetY: Float = 0f): RectF {
//        val left   = minOf(rect.left, rect.right)
//        val top    = minOf(rect.top, rect.bottom)
//        val right  = maxOf(rect.left, rect.right)
//        val bottom = maxOf(rect.top, rect.bottom)
//
//        // Tâm zoom = tâm hình học của rect + offset tinh chỉnh
//        val centerX = (left + right)  / 2f + offsetX
//        val centerY = (top  + bottom) / 2f + offsetY
//        val halfW   = (right  - left)  / 2f / zoom
//        val halfH   = (bottom - top)   / 2f / zoom
//
//        return RectF(
//            centerX - halfW,
//            centerY - halfH,
//            centerX + halfW,
//            centerY + halfH
//        )
//    }
//
//    /**
//     * Cắt Bitmap theo tỉ lệ phần trăm (0.0 đến 1.0), clamp nếu vượt biên.
//     */
//    private fun cropBitmapByPercent(src: Bitmap, rect: RectF): Bitmap {
//        val leftPercent   = minOf(rect.left,   rect.right).coerceIn(0f, 1f)
//        val rightPercent  = maxOf(rect.left,   rect.right).coerceIn(0f, 1f)
//        val topPercent    = minOf(rect.top,    rect.bottom).coerceIn(0f, 1f)
//        val bottomPercent = maxOf(rect.top,    rect.bottom).coerceIn(0f, 1f)
//
//        val left   = (src.width  * leftPercent).toInt().coerceIn(0, src.width  - 1)
//        val top    = (src.height * topPercent).toInt().coerceIn(0, src.height - 1)
//        val width  = (src.width  * (rightPercent  - leftPercent)).toInt().coerceIn(1, src.width  - left)
//        val height = (src.height * (bottomPercent - topPercent)).toInt().coerceIn(1, src.height - top)
//
//        return Bitmap.createBitmap(src, left, top, width, height)
//    }
//
//    private fun resetToCamera() {
//        pigeonModelNode?.stopAnimation(0)
//        pigeonModelNode?.isVisible = false
//        ringNode?.isVisible = false
//
//        binding.sceneView.scene.skybox = null
//
//        binding.tvRetry.visibility = View.GONE
//        binding.sceneView.visibility = View.GONE
//        binding.layoutResultTop.visibility = View.GONE
//
//        binding.viewFinder.visibility = View.VISIBLE
//        binding.ivBirdOutline.visibility = View.VISIBLE
//        binding.ivStartCapture.visibility = View.VISIBLE
//
//        currentRotationY = 0f
//        pigeonModelNode?.rotation = io.github.sceneview.math.Rotation(0f, 0f, 0f)
//        isCapturing = false
//    }
//
//    override fun onDestroyView() {
//        cameraExecutor.shutdown()
//        super.onDestroyView()
//    }
//}
