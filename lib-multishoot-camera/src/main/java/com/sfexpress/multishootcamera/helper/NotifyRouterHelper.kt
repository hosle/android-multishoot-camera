package com.sfexpress.multishootcamera.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.sfexpress.multishootcamera.ROUTER_SHOOT_EVENT
import com.sfexpress.multishootcamera.ROUTER_SHOOT_RESULT_CANCEL
import com.sfexpress.multishootcamera.ROUTER_SHOOT_RESULT_SUCCESS

/**
 * Created by tanjiahao on 2018/1/11
 * Original Project MultiShootCamera
 */


class NotifyRouter {

    private fun buildUri(authority: String, path: String): Uri {

        return Uri.Builder().scheme(ROUTER_SHOOT_EVENT).authority(authority).path(path).build()
    }

    fun notifySuccess(fromActivity: Activity) {

        navigateTo(fromActivity, ROUTER_SHOOT_EVENT, ROUTER_SHOOT_RESULT_SUCCESS)
    }

    fun notifyCancel(fromActivity: Activity) {

        navigateTo(fromActivity, ROUTER_SHOOT_EVENT, ROUTER_SHOOT_RESULT_CANCEL)
    }

    private fun navigateTo(fromActivity: Activity?, authority: String, path: String) {

        fromActivity?:return

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra(ROUTER_SHOOT_EVENT, path)
            intent.data = buildUri(authority, path)
            fromActivity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fromActivity.finish()
        }
    }
}