package com.github.carloslimaborges.barcodescanner

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class BarcodeScannerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VALUE = "barcode_value"
        const val EXTRA_FORMAT = "barcode_format"
        private const val TAG = "MpdftBarcodeScanner"
        private val ZOOM_PRESETS = listOf(0.6f to "0.6×", 1.0f to "1×", 2.0f to "2×")
    }

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var found = false
    private var camera: Camera? = null
    private var isTorchOn = false
    private var currentZoom = 1.0f

    private lateinit var torchBtn: ImageButton
    private val zoomButtons = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this)

        val previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        root.addView(previewView)
        root.addView(buildCancelButton())
        root.addView(buildBottomBar())

        setContentView(root)
        startCamera(previewView)
    }

    private fun buildCancelButton(): ImageButton {
        val size = dp(40)
        val margin = dp(16)
        return ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(Color.WHITE)
            setBackgroundColor(Color.argb(140, 0, 0, 0))
            layoutParams = FrameLayout.LayoutParams(size, size).also {
                it.gravity = Gravity.TOP or Gravity.END
                it.topMargin = margin
                it.rightMargin = margin
            }
            setOnClickListener { cancelar() }
        }
    }

    private fun buildBottomBar(): LinearLayout {
        val barHeight = dp(56)
        val margin = dp(16)

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.argb(160, 0, 0, 0))
            setPadding(dp(12), 0, dp(12), 0)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                barHeight
            ).also {
                it.gravity = Gravity.BOTTOM
                it.bottomMargin = margin
                it.leftMargin = margin
                it.rightMargin = margin
            }
        }
        bar.outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
        bar.clipToOutline = true
        bar.elevation = 4f

        val zoomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        ZOOM_PRESETS.forEach { (ratio, label) ->
            val btn = buildZoomButton(label, ratio)
            zoomRow.addView(btn)
            zoomButtons.add(btn)
        }
        updateZoomButtonStates()

        torchBtn = buildTorchButton()

        bar.addView(zoomRow)
        bar.addView(torchBtn)
        return bar
    }

    private fun buildZoomButton(label: String, ratio: Float): TextView {
        return TextView(this).apply {
            text = label
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            tag = ratio
            val size = dp(42)
            layoutParams = LinearLayout.LayoutParams(size, dp(34)).also {
                it.marginEnd = dp(4)
            }
            setBackgroundColor(Color.argb(46, 255, 255, 255))
            setOnClickListener { setZoom(ratio) }
        }
    }

    private fun buildTorchButton(): ImageButton {
        val size = dp(40)
        return ImageButton(this).apply {
            setImageResource(android.R.drawable.btn_star_big_off)
            setColorFilter(Color.WHITE)
            setBackgroundColor(Color.argb(46, 255, 255, 255))
            layoutParams = LinearLayout.LayoutParams(size, size).also {
                it.gravity = Gravity.CENTER_VERTICAL
                it.marginStart = dp(8)
            }
            setOnClickListener { toggleTorch() }
        }
    }

    private fun updateZoomButtonStates() {
        zoomButtons.forEach { btn ->
            val ratio = btn.tag as Float
            val selected = ratio == currentZoom
            btn.setBackgroundColor(
                if (selected) Color.argb(230, 255, 255, 255)
                else Color.argb(46, 255, 255, 255)
            )
            btn.setTextColor(if (selected) Color.BLACK else Color.WHITE)
        }
    }

    private fun updateTorchButtonState() {
        if (isTorchOn) {
            torchBtn.setColorFilter(Color.BLACK)
            torchBtn.setBackgroundColor(Color.argb(230, 255, 217, 0))
        } else {
            torchBtn.setColorFilter(Color.WHITE)
            torchBtn.setBackgroundColor(Color.argb(46, 255, 255, 255))
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera(previewView: PreviewView) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_ITF,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)

        ProcessCameraProvider.getInstance(this).addListener({
            try {
                val cameraProvider = ProcessCameraProvider.getInstance(this).get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage == null || found) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    barcodes.firstOrNull { it.rawValue != null }?.let { barcode ->
                                        if (!found) {
                                            found = true
                                            Log.d(TAG, "Scanned: format=${formatToString(barcode.format)} value=${barcode.rawValue}")
                                            devolverResultado(
                                                barcode.rawValue ?: "",
                                                formatToString(barcode.format)
                                            )
                                        }
                                    }
                                }
                                .addOnCompleteListener { imageProxy.close() }
                        }
                    }

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
                camera?.cameraControl?.setZoomRatio(currentZoom)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting camera", e)
                cancelar()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setZoom(ratio: Float) {
        currentZoom = ratio
        camera?.cameraControl?.setZoomRatio(ratio)
        updateZoomButtonStates()
    }

    private fun toggleTorch() {
        isTorchOn = !isTorchOn
        camera?.cameraControl?.enableTorch(isTorchOn)
        updateTorchButtonState()
    }

    private fun devolverResultado(value: String, format: String) {
        val intent = Intent().apply {
            putExtra(EXTRA_VALUE, value)
            putExtra(EXTRA_FORMAT, format)
        }
        runOnUiThread {
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun cancelar() {
        camera?.cameraControl?.enableTorch(false)
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun formatToString(format: Int): String = when (format) {
        Barcode.FORMAT_CODE_128    -> "CODE_128"
        Barcode.FORMAT_ITF         -> "ITF"
        Barcode.FORMAT_QR_CODE     -> "QR_CODE"
        Barcode.FORMAT_EAN_13      -> "EAN_13"
        Barcode.FORMAT_EAN_8       -> "EAN_8"
        Barcode.FORMAT_CODE_39     -> "CODE_39"
        Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
        else                       -> "UNKNOWN"
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
}
