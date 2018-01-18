package com.hosle.multishootcamera.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tanjiahao on 2018/1/12
 * Original Project MultiShootCamera
 */

@SuppressWarnings("ParcelCreator")
@Parcelize
data class PicUrlData(var local: String, var remote: String?) : Parcelable

val LinkedList<PicUrlData>.remoteUrlSize: Int
    get() {

        var count = 0

        forEach {
            it.remote?.also { count++ }
        }

        return count
    }

fun LinkedList<PicUrlData>.add(localUrl: String, remoteUrl: String?): Boolean {

    forEach {
        if (localUrl == it.local) {
            it.remote = remoteUrl
            return true
        }
    }

    return this.add(PicUrlData(localUrl, remoteUrl))
}

fun LinkedList<PicUrlData>.remove(localUrl: String?): Boolean {

    forEach {
        if (localUrl == it.local) {
            return remove(it)
        }
    }

    return false
}

fun LinkedList<PicUrlData>.getRemoteUrlArray(): Array<String> {

    val remoteUrlList = ArrayList<String>()

    forEach {
        it.remote?.also {
            remoteUrlList.add(it)
        }
    }

    return remoteUrlList.toTypedArray()
}