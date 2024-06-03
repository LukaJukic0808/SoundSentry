package hr.ferit.soundsentry.sensing.sensor

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import hr.ferit.soundsentry.sensing.AndroidSensor

class ProximitySensor(context: Context) :
    AndroidSensor(
        context = context,
        sensorFeature = PackageManager.FEATURE_SENSOR_PROXIMITY,
        sensorType = Sensor.TYPE_PROXIMITY
    )