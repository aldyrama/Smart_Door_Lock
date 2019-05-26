package org.d3ifcool.smart.Onvif

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.rvirin.onvif.onvifcamera.*


import org.d3ifcool.smart.R

const val RTSP_URL = "com.rvirin.onvif.onvifcamera.demo.RTSP_URL"

class OnvifConfig : AppCompatActivity(), OnvifListener {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentDevice = OnvifDevice("IP_ADDRESS:PORT", "login", "pwd")
        currentDevice.listener = this
        currentDevice.getDeviceInformation()
    }

    override fun requestPerformed(response: OnvifResponse) {
        Log.d("ONVIF", "Request ${response.request.type} performed.")
        Log.d("ONVIF","Succeeded: ${response.success}, message: ${response.parsingUIMessage}")

        if (response.request.type == OnvifRequest.Type.GetDeviceInformation) {
            currentDevice.getProfiles()

        } else if (response.request.type == OnvifRequest.Type.GetProfiles) {
            currentDevice.getStreamURI()

        } else if (response.request.type == OnvifRequest.Type.GetStreamURI) {
            Log.d("ONVIF", "Stream URI retrieved: ${currentDevice.rtspURI}")
        }
    }
}
