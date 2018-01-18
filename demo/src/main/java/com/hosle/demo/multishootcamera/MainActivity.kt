package com.hosle.demo.multishootcamera

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hosle.multishootcamera.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MultiShootCameraUploadHelper.uploadInterface = object : UploadInterface {
            override fun upload(file: String, subscriber: UploadSubscriber): Boolean {
                subscriber.onStart()

                Thread(Runnable {
                    for (i in 0..100) {
                        Thread.sleep(60)
                        val j = i
                        runOnUiThread { subscriber.onProcessing(j) }
                    }

                    runOnUiThread {
                        subscriber.onSuccess("remote:"+file)
//                        subscriber.onFail()
                    }

                }).start()

                return true
            }

            override fun submit(remoteUrls: Array<String>,subscriber: SubmitSubscriber){
                showToast(with(StringBuilder()) {
                    for (url in remoteUrls) {
                        append(url)
                        append(",")

                    }
                    toString()
                })
            }
        }
        btn_start_camera.setOnClickListener { navigateCameraActivity(this, 3) }
    }
}
