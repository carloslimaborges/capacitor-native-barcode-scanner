package com.github.carloslimaborges.barcodescanner

import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "MpdftBarcodeScanner")
class MpdftBarcodeScannerPlugin : Plugin() {

    @PluginMethod
    fun scan(call: PluginCall) {
        val intent = Intent(context, BarcodeScannerActivity::class.java)
        startActivityForResult(call, intent, "handleScanResult")
    }

    @ActivityCallback
    private fun handleScanResult(call: PluginCall?, result: ActivityResult) {
        if (call == null) return
        val data = result.data
        val ret = JSObject()
        if (result.resultCode == android.app.Activity.RESULT_OK && data != null) {
            ret.put("value", data.getStringExtra(BarcodeScannerActivity.EXTRA_VALUE) ?: "")
            ret.put("format", data.getStringExtra(BarcodeScannerActivity.EXTRA_FORMAT) ?: "")
            ret.put("cancelled", false)
        } else {
            ret.put("value", "")
            ret.put("format", "")
            ret.put("cancelled", true)
        }
        call.resolve(ret)
    }
}
