package hyunh.sample.barcode

import android.util.Log

private const val TAG = "Barcode"

fun logi(tag: String, msg: String) = Log.i(TAG, "$tag::$msg")

fun loge(tag: String, msg: String) = Log.e(TAG, "$tag::$msg")

fun logd(tag: String, msg: String) = Log.d(TAG, "$tag::$msg")
