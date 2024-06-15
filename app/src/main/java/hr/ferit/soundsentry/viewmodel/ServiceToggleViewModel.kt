package hr.ferit.soundsentry.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import hr.ferit.soundsentry.extension.isMeasurementRunning

class ServiceToggleViewModel(context: Context) : ViewModel() {

    var isServiceRunning by mutableStateOf(false)

    init {
        isServiceRunning = context.isMeasurementRunning()
    }
    fun toggleService() {
        isServiceRunning = !isServiceRunning
    }
}