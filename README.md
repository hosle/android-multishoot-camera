# android-multishoot-camera

This project is build up by reference to [google cameraview](https://github.com/google/cameraview.git) demo.

# Feaures

* To capture specific number of photos.
* A thumbnail will be shown after shooting, which can be clicked to switch the preview.
* A new page layouts all captured photos and supports for editting.
* The upload interface is provided.

# Demo

# Installation

Gradle

```
dependencies {
	compile 'com.hosle.multishootcamera:lib-multishootcamera:1.0.1'
}
```

Maven

```
<dependency>
  <groupId>com.hosle.multishootcamera</groupId>
  <artifactId>lib-multishootcamera</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

# Interface

### 1. Implement UploadInterface

The upload ability and the click event on submit button are explored to the UploadInterface class.

Implement the UploadInterface and set it to the Singleton `MultiShootCameraUploadHelper`

```kotlin
MultiShootCameraUploadHelper.uploadInterface = object : UploadInterface {
            override fun upload(file: String, subscriber: UploadSubscriber): Boolean {
          		
          		//start uploading
                subscriber.onStart()

                Thread(Runnable {
                    for (i in 0..100) {
                        Thread.sleep(60)
                        val j = i
                        
                        //upload is in process
                        runOnUiThread { subscriber.onProcessing(j) }
                    }

                    runOnUiThread {
                    
                        //success uploading
                        subscriber.onSuccess("remote:"+file)
                    }

                }).start()

                return true
            }

            override fun submit(remoteUrls: Array<String>,subscriber: SubmitSubscriber){
            
            		// callback the array of photo's remote urls
            		// todo what you what to on next
            }
        }
```


### 2. Navigation Entrance

To navigate the Activity, call `navigateCameraActivity` the extension function on the top package.

```kotlin
fun navigateCameraActivity(activity: Activity, \*count of photos you need*\shootCounts: Int)
```

# Lisence

```
Copyright (C) 2018. Henry Tam (Hosle) 

Contact: hosle@163.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
```