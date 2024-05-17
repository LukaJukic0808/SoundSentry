package hr.ferit.soundsentry

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import hr.ferit.soundsentry.di.sensorsModule
import hr.ferit.soundsentry.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SoundSentryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SoundSentryApp)
            modules(viewModelModule, sensorsModule)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "measurement_channel",
                "MeasurementModel",
                NotificationManager.IMPORTANCE_HIGH,
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}