/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hosle.multishootcamera

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * This file illustrates Kotlin's Extension Functions by extending FragmentActivity.
 */

/**
 * Shows a [Toast] on the UI thread.
 *
 * @param text The message to show
 */
fun Activity.showToast(text: String) {
    runOnUiThread { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
}

val COMMON_SHARED = "share_common"

fun Activity.saveStringInSharedPref(key: String, value: String) {

    val sharedPreference = getSharedPreferences(COMMON_SHARED, Context.MODE_PRIVATE)
    val editor = sharedPreference.edit()

    with(editor) {
        putString(key, value)
        apply()
    }
}

fun Activity.readStringInSharedPref(key: String): String? {
    val sharedPreference = getSharedPreferences(COMMON_SHARED, Context.MODE_PRIVATE)

    return sharedPreference.getString(key, "")
}

fun Activity.dp2px(dpValue : Float) : Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}