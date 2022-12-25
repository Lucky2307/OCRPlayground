package com.lucky.ocrplayground

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    private lateinit var recognizer: TextRecognizer
    private lateinit var button: Button
    private lateinit var scanButton: Button
    private lateinit var ssView: ImageView

    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mMediaProjection: MediaProjection
    private var mResultCode: Int = 0
    private lateinit var mResultData: Intent
    private var REQUEST_CODE = 1
    private lateinit var mImageReader: ImageReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE)

        recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())


        ssView = findViewById(R.id.ssView)
        button = findViewById(R.id.ssButton)
        button.setOnClickListener {
            val v = this.window.decorView.findViewById<View>(android.R.id.content)
            val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            v.draw(canvas)
            ssView.setImageBitmap(bitmap)
        }

        scanButton = findViewById(R.id.scanButton)
        scanButton.setOnClickListener{
            val img = getScreenshot()
            ssView.setImageBitmap(img)
        }

        val initButton = findViewById<Button>(R.id.initButton)
        initButton.setOnClickListener {
            initMedia()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("DEBUGPRINT", "INIT")
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("DEBUGPRINT", "INIT")
        if (requestCode == REQUEST_CODE){
            if (resultCode !== RESULT_OK) {
                Log.e("DEBUGPRINT", "User cancelled")
                return
            }
            mResultCode = resultCode
            Log.e("DEBUGPRINT", "INIT")
            if (data != null) {
                mResultData = data
            }
        }
    }

    private fun getScreenshot(): Bitmap {
        val image: Image = mImageReader.acquireLatestImage()
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
        return bitmap
    }

    private fun initMedia() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
        val metrics = resources.displayMetrics
        mImageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2)
        mMediaProjection.createVirtualDisplay("ScreenCapture", metrics.widthPixels, metrics.heightPixels, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.surface, null, null)
    }
}