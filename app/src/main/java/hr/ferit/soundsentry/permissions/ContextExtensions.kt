package hr.ferit.soundsentry.permissions

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import hr.ferit.soundsentry.service.MeasurementService

fun Context.hasRecordAudioPermission(): Boolean {
    return (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED
    )
}

@Suppress("DEPRECATION")
fun Context.isMeasurementRunning(): Boolean {
    val activityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

    for (serviceInfo in runningServices) {
        if (MeasurementService::class.java.name == serviceInfo.service.className) {
            return true
        }
    }

    return false
}