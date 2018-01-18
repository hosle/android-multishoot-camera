package com.sfexpress.multishootcamera

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by tanjiahao on 2018/1/5
 * Original Project MultiShootCamera
 */

class CameraPreview @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    lateinit var camera : Camera

    init {
        holder.apply {
            addCallback(this@CameraPreview)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }




    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        holder?.surface?:return

        camera.apply {
            try {
                stopPreview()
            } catch (e: Exception) {
            }

            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (e: Exception) {
                Log.e( "Camera","Error starting camera preview: " + e.message);
            }
            setPreviewDisplay(holder)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        }catch (e:Exception){

        }
    }

}