package hr.ferit.soundsentry.model

import hr.ferit.soundsentry.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class LightSensorModel(private val lightSensor: MeasurableSensor) {

    var lux = MutableStateFlow(0.0f)
    val doesSensorExist = lightSensor.doesSensorExist

    fun start() {
        lightSensor.startListening()
        lightSensor.setOnSensorValueChangedListener { values ->
            lux.value = values[0]
        }
    }

    fun stop() {
        lightSensor.stopListening()
    }
}