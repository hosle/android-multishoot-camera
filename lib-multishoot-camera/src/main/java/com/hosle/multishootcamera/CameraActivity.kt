package com.hosle.multishootcamera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.hosle.multishootcamera.cameraview.CameraView
import com.hosle.multishootcamera.helper.ImageLoadHelper
import com.hosle.multishootcamera.helper.createTransparentOption
import com.hosle.multishootcamera.model.PicUrlData
import com.hosle.multishootcamera.model.add
import com.hosle.multishootcamera.model.remove
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.custom_view_thumbnail_camera.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by tanjiahao on 2018/1/18
 * Original Project HoMultiShootCamera
 */

class CameraActivity : AppCompatActivity() {

    lateinit var thumbnailLayoutParams: LinearLayout.LayoutParams

    private var backgroundHandler: Handler? = null

    private val picUrlDataList = LinkedList<PicUrlData>()

    private var shootCountRequired = 0

    private lateinit var previewOverCam: ImageView
    private lateinit var cameraView: CameraView
    private var btnCapture: Button? = null
    private var containerThumbnail: LinearLayout? = null

    private var previewCamUrl: String? = null

    private var from: Int by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)

        shootCountRequired = intent?.getIntExtra(SHOOT_COUNT_REQUIRED, 0) ?: 0

        intent?.getParcelableArrayListExtra<PicUrlData>(EXIST_PIC)?.apply {
            picUrlDataList.addAll(this)
        }

        from = intent?.getIntExtra(EXTRA_REQUEST_CODE, -1) ?: -1

        initView()

    }


    private fun initView() {
        previewOverCam = preview_over_cam
        cameraView = camera_view
        btnCapture = btn_capture
        containerThumbnail = container_thumbnail_shoot

        camera_view?.apply {
            addCallback(cameraCallback)
            facing = CameraView.FACING_BACK
            flash = CameraView.FLASH_AUTO
            autoFocus = true
            adjustViewBounds = true

        }

        thumbnailLayoutParams = LinearLayout.LayoutParams(dp2px(75f), dp2px(75f))

        btnCapture?.apply {

            isActivated = true

            setOnClickListener {
                btnCapture?.isClickable = false

                when {
                    previewOverCam.drawable != null -> {
                        showCamera()
                        enableBtnCapture()
                    }

                    getLeftToShoot() > 0 -> {
                        Log.i("uploadpic", "takePicture click")
//                        backgroundHandler?.run { cameraView.takePicture() }
                        cameraView.takePicture()
                    }

                    else -> {
                        showToast("已拍摄足够张数")
                        enableBtnCapture()
                    }
                }
            }
        }

        btn_done?.setOnClickListener {

            if (from == UPLOAD_TO_CAMERA_FOR_RESULT) {
                val intent = Intent()
                intent.putExtra("picuri", picUrlDataList)
                setResult(Activity.RESULT_OK, intent)
                finish()

            } else {
                navigateUploadActivity(this, picUrlDataList)
                finish()
            }
        }

        btn_close?.setOnClickListener { onBackPressed() }

        container_thumbnail_shoot?.removeAllViews()

        addExistThumbnail()

    }

    private fun enableBtnCapture() =
        Handler().postDelayed({ btnCapture?.isClickable = true }, 300)


    private fun showCamera() {
        previewOverCam.setImageDrawable(null)

        changeSelectedBg(null)

        notifyBtnCapture()
    }

    private fun addExistThumbnail() {

        for (urlData in picUrlDataList) {
            addThumbnailFromFile(urlData.local)
        }

        if (picUrlDataList.size > 0) {
            notifyBtnCapture()
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            camera_view.start()

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CAMERA_PERMISSION,
                        R.string.camera_permission_not_granted)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
        }
    }

    override fun onPause() {
        camera_view.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        backgroundHandler?.run {
            looper?.quit()
            null
        }
    }

    private fun getBackgroundHandler(): Handler =
            backgroundHandler ?: with(HandlerThread("background")) {
                this.start()
                Handler(looper)
            }


    private fun notifyPreviewOverCam(tag: String) {

        if (previewOverCam.drawable == null)
            return
        else if (tag == previewCamUrl) {
            showCamera()
        }
    }

    @SuppressLint("InflateParams")
    private fun addThumbnailFromFile(absUrl: String) {

        btnCapture?.isClickable = true

        with(layoutInflater.inflate(R.layout.custom_view_thumbnail_camera, null)) {

            tag = absUrl

            ImageLoadHelper(this@CameraActivity)
                    .displayImage(this.img_content_camera, "file://" + absUrl, createTransparentOption()
                            .apply {
                                isCenterCrop = true
                                isPreFitXY = false
                            })

            btn_delete.setOnClickListener {

                img_content_camera.setImageBitmap(null)

                containerThumbnail?.removeView(this)

                picUrlDataList.remove(tag as String)

                notifyBtnCapture()
                notifyPreviewOverCam(tag as String)
            }

            setOnClickListener(thumbnailClickListener)

            containerThumbnail?.addView(this, thumbnailLayoutParams)
        }
    }

    private fun changeSelectedBg(v: View?) {

        for (i in 0 until containerThumbnail!!.childCount) {
            val childView = containerThumbnail!!.getChildAt(i)
            if (v == childView) {
                childView.inner_container.setBackgroundColor(resources.getColor(R.color.bg_thumbnail_shoot_selected))
            } else {
                childView.inner_container.setBackgroundColor(resources.getColor(R.color.bg_thumbnail_shoot_unselected))
            }
        }
    }

    private val thumbnailClickListener = View.OnClickListener { v: View ->
        run {

            btnCapture?.setText(null)

            val url = v.tag as String

            previewCamUrl = url

            with(previewOverCam) {

                ImageLoadHelper(this@CameraActivity)
                        .displayImage(this, "file://" + url, createTransparentOption()
                                .apply {
                                    isPreFitXY = false
                                })

            }

            changeSelectedBg(v)
        }
    }

    private val cameraCallback = object : CameraView.Callback() {

        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {

            if (getLeftToShoot() <= 0) return


            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val fileName = "/sf_shoot_" + System.currentTimeMillis() % 100000000 + ".jpg"
            val absoluteUrl = dir.toString() + fileName

            picUrlDataList.add(absoluteUrl, null)

            getBackgroundHandler().post({

                val file = File(dir, fileName)

                val os: OutputStream? = try {
                    FileOutputStream(file).apply {
                        write(data)
                        close()
                    }
                } catch (e: IOException) {
                    Log.w("multiShoot", "Cannot write to " + file, e)
                    null
                }

                os?.close()

//                runOnUiThread { displayPreview(data, absoluteUrl) }

                if (null != data) {
//                    val bitOpt = BitmapFactory.Options().apply {
//                        inSampleSize = 8
//                        inScaled = true
//                        inPurgeable = true
//                    }
//
//                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, bitOpt)
//
//                    val rotatedHelper = RotatedBitmapHelper(absoluteUrl)

//                    runOnUiThread { displayNewThumbnail(rotatedHelper.rotate(bitmap), absoluteUrl) }
                    runOnUiThread { addThumbnailFromFile(absoluteUrl) }
                }
            })

            notifyBtnCapture()
        }
    }

    private fun notifyBtnCapture() =
            with(getLeftToShoot()) {
                when {
                    this >= shootCountRequired -> {
                        btn_capture.text = null
                        btn_capture.isActivated = true
                        btn_done.isEnabled = false
                    }
                    this > 0 -> {
                        btn_capture.text = this.toString()
                        btn_capture.isActivated = true
                        btn_done.isEnabled = false
                    }
                    else -> {
                        btn_capture.text = null
                        btn_capture.isActivated = false
                        btn_done.isEnabled = true
                    }
                }
            }


    private fun getLeftToShoot() = shootCountRequired - picUrlDataList.size

}


const val SHOOT_COUNT_REQUIRED = "shoot_count_required"
const val EXIST_PIC = "exist_pic"
const val EXTRA_REQUEST_CODE = "requestCode"

const val UPLOAD_TO_CAMERA_FOR_RESULT = 1

fun navigateCameraForResult(activity: Activity, shootCounts: Int, picUrl: LinkedList<PicUrlData>?) {
    if (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra(SHOOT_COUNT_REQUIRED, shootCounts)
        intent.putExtra(EXTRA_REQUEST_CODE, UPLOAD_TO_CAMERA_FOR_RESULT)
        picUrl?.apply {
            intent.putExtra(EXIST_PIC, picUrl)
        }
        activity.startActivityForResult(intent, UPLOAD_TO_CAMERA_FOR_RESULT)
    } else {
        activity.showToast("无法获取照相机")
    }
}

fun navigateCameraActivity(activity: Activity, shootCounts: Int, picUrl: LinkedList<PicUrlData>?) {
    if (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra(SHOOT_COUNT_REQUIRED, shootCounts)
        picUrl?.apply {
            intent.putExtra(EXIST_PIC, picUrl)
        }
        activity.startActivity(intent)
    } else {
        activity.showToast("无法获取照相机")
    }
}


fun navigateCameraActivity(activity: Activity, shootCounts: Int) {
    navigateCameraActivity(activity, shootCounts, null)
}
