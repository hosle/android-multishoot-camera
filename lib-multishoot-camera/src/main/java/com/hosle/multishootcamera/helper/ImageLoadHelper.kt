package com.hosle.multishootcamera.helper

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.hosle.multishootcamera.DRAWABLE
import com.hosle.multishootcamera.FILE
import java.io.File

/**
 * Created by tanjiahao on 2018/1/18
 * Original Project HoMultiShootCamera
 */
class ImageLoadHelper(context: Context) {
    private var mGlide: Glide? = null

    private var mManager: RequestManager? = null

    init {
        mGlide = Glide.get(context)

        mManager = Glide.with(context)
    }

    fun displayImage(imageView: ImageView, imageUrl: String, option: DisplayOption) {

        if (TextUtils.isEmpty(imageUrl)) {

            imageView.setImageResource(option.loadingResId)

            return

        }

        if (imageUrl.startsWith(DRAWABLE)) {//读取资源文件

            val drawableRes = imageUrl.replace(DRAWABLE, "")

            val resId = Integer.valueOf(drawableRes)

            operateDisplay(imageView, resId, imageUrl, option)

        } else if (imageUrl.startsWith("file://")) {//读取本地文件

            val fileRes = imageUrl.replace(FILE, "")

            val file = File(fileRes)

            if (file.exists()) {

                operateDisplay(imageView, file, imageUrl, option)

            }

        } else if (imageUrl.startsWith("http")) {//读取网络图片

            operateDisplay(imageView, imageUrl, imageUrl, option)

        }

    }

    private fun <T> operateDisplay(imageView: ImageView,
                                   t: T?,
                                   imageUrl: String,
                                   option: DisplayOption?) {
        var optionLocal = option

        if (optionLocal == null) {

            optionLocal = createTransparentOption()

        }

        val options = RequestOptions.placeholderOf(optionLocal.loadingResId)
                .error(optionLocal.loadErrorResId)

        if (optionLocal.isCenterCrop) {

            options.centerCrop()

        }

        mManager!!.applyDefaultRequestOptions(options)

        val builder = mManager!!.load(t)

        if (option!!.thumbnail < 1f && option!!.thumbnail > 0) {

            builder.thumbnail(option!!.thumbnail)

        }

        if (option!!.isPreFitXY) {

            imageView.scaleType = ImageView.ScaleType.FIT_XY

        }
        builder.into(imageView)

    }

    fun loadImage(width: Int, height: Int, imageUrl: String, option: DisplayOption) {

        mManager!!.asBitmap()
                .load(imageUrl)

    }
}