package com.lucky.ocrplayground

import android.content.Context
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import java.nio.ByteBuffer


class ScreenCapture {
    private lateinit var mMediaProjectionManager: MediaProjectionManager
    private lateinit var mMediaProjection: MediaProjection
    private var mResultCode: Int = 0
    private lateinit var mResultData: Intent
    private var REQUEST_CODE = 1
    private lateinit var mImageReader: ImageReader

    fun init(appContext: Context) {
        mMediaProjectionManager = appContext.getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
        val metrics = appContext.resources.displayMetrics
        mImageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, ImageFormat.JPEG, 2)
        mMediaProjection.createVirtualDisplay("ScreenCapture", metrics.widthPixels, metrics.heightPixels, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.surface, null, null)

    }

    fun GetScreenshot(): Bitmap {
        val image: Image = mImageReader.acquireLatestImage()
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

}