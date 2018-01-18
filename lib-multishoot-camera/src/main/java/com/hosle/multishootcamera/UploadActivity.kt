package com.hosle.multishootcamera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.hosle.multishootcamera.helper.ImageLoadHelper
import com.hosle.multishootcamera.helper.createTransparentOption
import com.hosle.multishootcamera.model.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.custom_view_thumbnail_upload.view.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by tanjiahao on 2018/1/9
 * Original Project MultiShootCamera
 */


class UploadActivity : AppCompatActivity() {

    private var picUrlDataList: LinkedList<PicUrlData> by Delegates.notNull()

    private var shootCountRequired = 0

    private var emptyViewForShoot: View? = null

    private lateinit var cellLayoutParams: RelativeLayout.LayoutParams


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initPicUrlDataList()
        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPLOAD_TO_CAMERA_FOR_RESULT && resultCode == Activity.RESULT_OK) {
            intent = data

            picUrlDataList.clear()

            emptyViewForShoot = null

            initPicUrlDataList()

            tv_desc.text = null
        }
    }

    private fun initPicUrlDataList() {
        picUrlDataList = LinkedList(intent?.getParcelableArrayListExtra<PicUrlData>("picuri"))
                .also { processPic(it) }
    }


    private fun initViews() {
        btn_submit.setOnClickListener {
            MultiShootCameraUploadHelper.submit(picUrlDataList.getRemoteUrlArray(), object : SubmitSubscriber {
                override fun onSuccess() {
                    finish()
                }
            })
        }

        btn_close.setOnClickListener { onBackPressed() }
    }


    private fun processPic(picUris: LinkedList<PicUrlData>) {

        shootCountRequired = picUris.size

        Log.i("uploadpic", "size : " + shootCountRequired)

        container_thumbnail.removeAllViews()

        for (uri in picUris) {

            with(uri) {
                displayPics(this)
                        .also { upload(this, it) }
                Log.i("uploadpic", this.local)
            }
        }
    }


    @SuppressLint("InflateParams")
    private fun displayPics(picUrlData: PicUrlData): RelativeLayout {

        val picUri = picUrlData.local
        val thumbnail = layoutInflater.inflate(R.layout.custom_view_thumbnail_upload, null) as RelativeLayout

        ImageLoadHelper(this)
                .displayImage(thumbnail.img_content, "file://" + picUri, createTransparentOption()
                        .apply {
                            isPreFitXY = false
                        })

        cellLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
                .apply {
                    val marginValue = dp2px(5f)
                    leftMargin = marginValue
                    rightMargin = marginValue
                    topMargin = marginValue
                    bottomMargin = marginValue
                }

        with(thumbnail) {

            tag = picUri

            btn_delete_upload.setOnClickListener {

                img_content.setImageDrawable(null)
                container_thumbnail.removeView(this)

                picUrlDataList.remove(tag as String)

                addEmptyView()

                showDesc()

                btn_submit.isEnabled = false
            }

            tv_upload_status.apply {
                setOnClickListener { upload(picUrlData, this@with) }
            }

            container_thumbnail.addView(this, cellLayoutParams)
        }

        return thumbnail

    }


    private fun showDesc() {

        val prefix = "还需要拍摄 "
        val rawContent = prefix + getLeftToShoot() + " 张照片"
        val countStringLength = getLeftToShoot().toString().length

        val content = SpannableString(rawContent).apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#FF2D4B")),
                    prefix.length, prefix.length + countStringLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        tv_desc.text = content
    }


    @SuppressLint("InflateParams")
    private fun addEmptyView() {

        if (null == emptyViewForShoot) {

            emptyViewForShoot = layoutInflater.inflate(R.layout.custom_view_empty_for_shoot, null) as RelativeLayout

            emptyViewForShoot!!.apply {

                setOnClickListener {
                    navigateCameraForResult(this@UploadActivity, shootCountRequired, picUrlDataList)
                }
            }

            container_thumbnail.addView(emptyViewForShoot, cellLayoutParams)
        }

    }


    private fun upload(picUrlData: PicUrlData, thumbnailView: RelativeLayout) {

        val picUri = picUrlData.local

        picUrlData.remote ?: MultiShootCameraUploadHelper.upload(picUri, object : UploadSubscriber {
            override fun onStart() {

                thumbnailView.apply {
                    tv_upload_status
                            .apply {
                                text = "正在上传"
                                visibility = View.VISIBLE
                            }

                    progress_bar.visibility = View.VISIBLE

                    tv_upload_status.isClickable = false

                    img_content.isClickable = false

                }


            }

            override fun onFinish() {

            }

            @SuppressLint("SetTextI18n")
            override fun onFail() {

                thumbnailView.tv_upload_status.apply {
                    text = "上传失败\n点击重试"
                    isClickable = true
                }
            }

            override fun onSuccess(path: String) {

                if (!isViewAppeared(thumbnailView)) return

                picUrlDataList.add(picUri, path)

                thumbnailView.apply {
                    tv_upload_status.visibility = View.GONE
                    progress_bar.visibility = View.GONE
                    img_content.isClickable = true
                }

                Log.i("UploadPic", "onSuccess : " + path)

                notifyBtnSubmit()
            }

            @SuppressLint("SetTextI18n")
            override fun onProcessing(step: Int) {

                thumbnailView.apply {
                    progress_bar.progress = step
                    tv_upload_status.text = "正在上传\n$step%"
                }

            }
        })
    }


    private fun isViewAppeared(view: View): Boolean =
            (0..container_thumbnail.childCount).any { container_thumbnail.getChildAt(it) == view }


    private fun notifyBtnSubmit() {
        btn_submit.isEnabled = picUrlDataList.remoteUrlSize == shootCountRequired
    }


    private fun getLeftToShoot() = shootCountRequired - picUrlDataList.size


}

object MultiShootCameraUploadHelper {
    lateinit var uploadInterface: UploadInterface

    fun upload(file: String, subscriber: UploadSubscriber)
            = uploadInterface.upload(file, subscriber)

    fun submit(remoteUrls: Array<String>, subscriber: SubmitSubscriber) = uploadInterface.submit(remoteUrls, subscriber)
}


interface UploadInterface {
    fun upload(file: String, subscriber: UploadSubscriber): Boolean

    fun submit(remoteUrls: Array<String>, subscriber: SubmitSubscriber)
}


interface UploadSubscriber {
    fun onStart()
    fun onFinish()
    fun onSuccess(path: String)
    fun onFail()
    fun onProcessing(step: Int)
}


interface SubmitSubscriber {
    fun onSuccess()
}


fun navigateUploadActivity(activity: Activity, picUris: LinkedList<PicUrlData>) {
    val intent = Intent(activity, UploadActivity::class.java)
    intent.putExtra("picuri", picUris)
    activity.startActivity(intent)
}