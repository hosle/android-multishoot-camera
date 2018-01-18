package com.hosle.multishootcamera.helper

import com.bumptech.glide.request.target.Target

/**
 * Created by tanjiahao on 2018/1/18
 * Original Project HoMultiShootCamera
 */

class DisplayOption {
    /**
     * 加载中的资源id
     */
    var loadingResId: Int = 0

    /**
     * 加载失败的资源id
     */
    var loadErrorResId: Int = 0

    /**
     * 默认不显示缩略图
     */
    var thumbnail = 1.0f

    var isPreFitXY = true

    var isCenterCrop = false

    var asBitmap = false

    var width = Target.SIZE_ORIGINAL

    var height = Target.SIZE_ORIGINAL


    fun setAsBitmap(asBitmap: Boolean): DisplayOption {

        this.asBitmap = asBitmap

        return this

    }

    fun setThumbnail(thumbnail: Float): DisplayOption {


        this.thumbnail = thumbnail

        return this

    }

    fun setCenterCrop(isCenterCrop: Boolean): DisplayOption {

        this.isCenterCrop = isCenterCrop

        return this

    }

    fun setSize(width: Int, height: Int): DisplayOption {

        this.width = width

        this.height = height

        return this

    }
}


fun createTransparentOption(): DisplayOption {

    return DisplayOption()
            .apply {
                loadErrorResId = 0
                loadingResId = 0
            }
}