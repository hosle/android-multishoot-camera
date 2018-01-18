package com.hosle.multishootcamera.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.IOException

/**
 * Created by tanjiahao on 2018/1/10
 * Original Project MultiShootCamera
 */

class RotatedBitmapHelper(private val originUrl: String) {
    private val angle: Int

    // 从指定路径下读取图片，并获取其EXIF信息
    // 获取图片的旋转信息
    val bitmapDegree: Int
        get() {
            var degree = 0
            try {
                val exifInterface = ExifInterface(originUrl)
                val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return degree
        }

    init {
        angle = bitmapDegree
    }

    fun rotate(bm: Bitmap): Bitmap? {
        return rotateBitmapByDegree(bm, angle)
    }

    fun rotateBitmapByDegree(bm: Bitmap?, degree: Int): Bitmap? {
        var returnBm: Bitmap? = null

        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }

        if (bm != null && bm != returnBm) {
            bm.recycle()
        }

        return returnBm
    }
}