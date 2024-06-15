package hr.ferit.soundsentry.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import hr.ferit.soundsentry.model.LightSensorModel
import hr.ferit.soundsentry.model.MeasurementModel
import hr.ferit.soundsentry.model.AccelerometerModel
import hr.ferit.soundsentry.model.UserInfoModel
import hr.ferit.soundsentry.extension.hasRecordAudioPermission
import hr.ferit.soundsentry.extension.NetworkChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

@TargetApi(31)
@SuppressLint("MissingPermission")
class MeasurementService : Service(), KoinComponent {
    private val handler = Handler(Looper.getMainLooper())
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var initialNotificationDelay = true
    private var isSuccessful = false
    private var userId: String? = null
    private var measurementTokens = 0
    private var earnedTokens = 0
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var outputFile: File
    private lateinit var notificationManager: NotificationManager
    private lateinit var accelerometerModel: AccelerometerModel
    private lateinit var lightSensorModel: LightSensorModel
    private var measurementPeriod: Long = 15
    private val db = FirebaseFirestore.getInstance()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaRecorder = MediaRecorder(this)
        outputFile = File(this.filesDir, "recording")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        accelerometerModel = get()
        lightSensorModel = get()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("user_id")
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = createMainNotification(applicationContext, earnedTokens)
        startForeground(1, notification)
        collectData()
    }

    private fun collectData() {
        coroutineScope.launch {
            if (initialNotificationDelay) {
                try {
                    val currentUserInfoSnapshot = db.collection("users").document(userId!!).get().await()
                    if (currentUserInfoSnapshot != null && currentUserInfoSnapshot.exists()) {
                        val userInfoModel = currentUserInfoSnapshot.toObject(UserInfoModel::class.java)
                        if(userInfoModel?.period != null) {
                            measurementPeriod = userInfoModel.period.toLong()
                        }
                    }
                } catch (e: FirebaseFirestoreException) {
                    Log.d("Fetching user info", "Failed")
                }
                delay(10000L)
                initialNotificationDelay = false
            }
            if (!NetworkChecker.isNetworkAvailable(applicationContext)) {
                showNoInternetNotification(applicationContext, notificationManager)
            } else {
                if (applicationContext.hasRecordAudioPermission()) {
                    showMeasuringNotification(applicationContext, notificationManager)
                    withContext(Dispatchers.IO) {
                        try {
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                            mediaRecorder.setOutputFile(outputFile.absolutePath)
                            mediaRecorder.prepare()
                            mediaRecorder.start()
                            var amplitudeSum = mediaRecorder.maxAmplitude
                            accelerometerModel.startStepCount()
                            for (i in 1..50) {
                                delay(100L)
                                amplitudeSum += mediaRecorder.maxAmplitude
                            }
                            mediaRecorder.stop()
                            accelerometerModel.stopStepCount()

                            val measurement = MeasurementModel(
                                noiseAmplitude = amplitudeSum / 50,
                                doesAccelerometerExist = accelerometerModel.doesSensorExist,
                                doesLightSensorExist = lightSensorModel.doesSensorExist,
                                movementDetected = accelerometerModel.getMovementInformation(),
                                lux = lightSensorModel.lux.value
                                )
                            measurementTokens = measurement.save(userId)
                            earnedTokens += measurementTokens
                            isSuccessful = true

                        } catch (e: IOException) {
                            Log.d("IOException", "Caught exception: $e")
                        }
                    }
                    if (isMainNotificationActive(notificationManager)) {
                        notificationManager.notify(
                            1,
                            createMainNotification(applicationContext, earnedTokens)
                        )
                    }
                    if (isSuccessful) {
                        showMeasuringSuccessNotification(
                            applicationContext,
                            notificationManager,
                            measurementTokens
                        )
                        isSuccessful = false
                    } else {
                        Log.e("MeasurementModel", "Not successful")
                    }
                } else {
                    Log.d("Missing permissions", "Grant all permissions")
                }
            }
            handler.postDelayed(::collectData, TimeUnit.MINUTES.toMillis(measurementPeriod))
        }
    }

    private fun stop() {
        mediaRecorder.release()
        coroutineScope.cancel()
        handler.removeCallbacksAndMessages(null)
        notificationManager.cancel(2)
        stopSelf()
    }

    enum class Actions {
        START, STOP
    }
}