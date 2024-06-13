package hr.ferit.soundsentry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import hr.ferit.soundsentry.model.LightSensorModel
import hr.ferit.soundsentry.model.AccelerometerModel
import hr.ferit.soundsentry.permissions.isMeasurementRunning
import hr.ferit.soundsentry.ui.theme.SoundSentryTheme
import hr.ferit.soundsentry.view.screen.MainScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class MainActivity : ComponentActivity(), KoinComponent {
    private val accelerometerModel: AccelerometerModel = get()
    private val lightSensorModel: LightSensorModel = get()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundSentryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!applicationContext.isMeasurementRunning()) {
            accelerometerModel.start()
            lightSensorModel.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!applicationContext.isMeasurementRunning()) {
            accelerometerModel.stop()
            lightSensorModel.stop()
        }
    }
}