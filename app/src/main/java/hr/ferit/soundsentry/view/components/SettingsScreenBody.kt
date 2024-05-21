package hr.ferit.soundsentry.view.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.soundsentry.R
import hr.ferit.soundsentry.ui.theme.RaleWay
import kotlin.math.roundToInt

@Composable
fun SettingsScreenBody(
    onSetPeriodClick: (period: Int) -> Unit,
    currentPeriod: Float
) {
    var sliderPosition by remember { mutableStateOf(0f) }
    val sliderRange = 1f..15f

    LaunchedEffect(currentPeriod != 0f) {
        sliderPosition = currentPeriod
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(R.string.measurement_description),
            textAlign = TextAlign.Center,
            fontFamily = RaleWay,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
        )
        Spacer(Modifier.height(20.dp))
        Slider(
            value = sliderPosition,
            valueRange = sliderRange,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                sliderPosition = sliderPosition.roundToInt().toFloat()
            },
            steps = (sliderRange.endInclusive - sliderRange.start).toInt() - 1
        )
        Spacer(Modifier.height(10.dp))
        Text(text = "${sliderPosition.toInt()} min", fontSize = 20.sp)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = {
                onSetPeriodClick.invoke(sliderPosition.toInt())
            },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(
                    height = 70.dp,
                    width = 200.dp,
                )
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp),
                ),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                containerColor = colorResource(
                    R.color.green,
                ),
            ),
        ) {
            Text(
                stringResource(R.string.set),
                textAlign = TextAlign.Center,
                fontFamily = RaleWay,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}