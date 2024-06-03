package hr.ferit.soundsentry.model

import hr.ferit.soundsentry.sensing.MeasurableSensor
import kotlinx.coroutines.flow.MutableStateFlow

class ProximitySensorModel(private val proximitySensor: MeasurableSensor) {

    var distance = MutableStateFlow(0.0f)
    val doesSensorExist = proximitySensor.doesSensorExist

    fun start() {
        proximitySensor.startListening()
        proximitySensor.setOnSensorValueChangedListener { values ->
            distance.value = values[0]
        }
    }

    fun stop() {
        proximitySensor.stopListening()
    }
}