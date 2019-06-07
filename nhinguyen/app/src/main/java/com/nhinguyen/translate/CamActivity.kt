package com.nhinguyen.translate

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.orhanobut.logger.Logger
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cam_activity.*
//import org.jetbrains.anko.toast
import kotlin.properties.Delegates
import com.google.gson.Gson
//import kotlinx.android.synthetic.main.cam_activity.*
import okhttp3.*
import java.io.IOException
//import java.util.logging.Logger

@SuppressLint("SetTextI18n")

class CamActivity : AppCompatActivity() {

    private var mCameraSource by Delegates.notNull<CameraSource>()
    private var textRecognizer by Delegates.notNull<TextRecognizer>()

    private val PERMISSION_REQUEST_CAMERA = 100
    private var textFromCam: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cam_activity)

        startCameraSource()
        transNow.setOnClickListener{getData(textFromCam)}
    }

    private fun startCameraSource() {

        //  Create text Recognizer
        textRecognizer = TextRecognizer.Builder(this).build()

        if (!textRecognizer.isOperational) {
            //toast("Dependencies are not loaded yet...please try after few moment!!")
            //Logger.d("Dependencies are downloading....try after few moment")
            return
        }

        //  Init camera source to use high resolution and auto focus
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setAutoFocusEnabled(true)
            .setRequestedFps(2.0f)
            .build()

        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(surface_camera_preview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    //toast("Error:  ${e.message}")
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                tv_result.post {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    textFromCam = stringBuilder.toString()
                    //tv_result.text = stringBuilder.toString()
                }
            }
        })
    }
    private fun getData(textRec: String) {
        //pbLoading.visibility = View.VISIBLE
        val url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20190526T190621Z.e1359929da9ad67c.617237bc4b5e0d527207ac3d32e30b672c2c18e9&lang=en-vi&text="+textRec
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {


                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = response.body()!!.string()
                    val data = Gson().fromJson(json, dataLang::class.java)
                    runOnUiThread {
                        tv_result.text = data.text[0]
                        Log.d("res", data.toString())
                    }
                }

            })
    }
    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                mCameraSource.start(surface_camera_preview.holder)
            } else {
                //toast("Permission need to grant")
                finish()
            }
        }
    }
}
