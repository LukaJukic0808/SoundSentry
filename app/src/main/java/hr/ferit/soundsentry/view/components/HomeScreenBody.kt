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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreenBody(
    onSettingsClick: () -> Unit,
    onToggleService: () -> Unit,
    isServiceRunning: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { onSettingsClick.invoke() },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(
                    height = 100.dp,
                    width = 200.dp,
                )
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp),
                ),
            colors = ButtonDefaults.buttonColors(contentColor = Color.White,
                containerColor = colorResource(R.color.purple_700)),
        ) {
            Text(
                stringResource(R.string.settings),
                textAlign = TextAlign.Center,
                fontFamily = RaleWay,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
        Spacer(Modifier.height(150.dp))
        Button(
            onClick = {
                onToggleService.invoke()
            },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .size(
                    height = 100.dp,
                    width = 200.dp,
                )
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp),
                ),
            colors =
            if (isServiceRunning) {
                ButtonDefaults.buttonColors(contentColor = Color.White, containerColor = Color.Red)
            } else {
                ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = colorResource(
                        R.color.green,
                    ),
                )
            },
        ) {
            Text(
                if (isServiceRunning) {
                    stringResource(R.string.stop_measuring)
                } else {
                    stringResource(R.string.start_measuring)
                },
                textAlign = TextAlign.Center,
                fontFamily = RaleWay,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}