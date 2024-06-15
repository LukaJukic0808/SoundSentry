package hr.ferit.soundsentry.model

import hr.ferit.soundsentry.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.sqrt

class AccelerometerModel(private val accelerometer: MeasurableSensor) {

    private var x = MutableStateFlow(0.0f)
    private var y = MutableStateFlow(0.0f)
    private var z = MutableStateFlow(0.0f)
    val doesSensorExist = accelerometer.doesSensorExist
    private var isMeasuring = false
    private var stepCount: Int = 0

    fun start() {
        accelerometer.startListening()
        accelerometer.setOnSensorValueChangedListener { values ->
            x.value = values[0]
            y.value = values[1]
            z.value = values[2]

            if (sqrt(x.value * x.value + y.value * y.value + z.value * z.value) >= 13f && isMeasuring) {
                stepCount++
            }
        }
    }

    fun startStepCount() {
        isMeasuring = true
    }

    fun stopStepCount() {
        isMeasuring = false
    }

    fun getMovementInformation(): Boolean {
        val hasMoved = stepCount > 0
        stepCount = 0
        return hasMoved
    }

    fun stop() {
        accelerometer.stopListening()
    }
}