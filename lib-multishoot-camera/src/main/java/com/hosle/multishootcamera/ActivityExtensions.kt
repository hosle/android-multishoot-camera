package com.hosle.multishootcamera

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * Created by tanjiahao on 2018/1/18
 * Original Project HoMultiShootCamera
 */

fun Activity.showToast(text: String) {
    runOnUiThread { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
}

fun Activity.dp2px(dpValue : Float) : Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}