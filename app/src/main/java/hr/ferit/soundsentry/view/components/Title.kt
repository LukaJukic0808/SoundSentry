package hr.ferit.soundsentry.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.ferit.soundsentry.R
import hr.ferit.soundsentry.ui.theme.RaleWay

@Composable
fun Title() {
    Text(
        text = stringResource(R.string.app_title),
        textAlign = TextAlign.Center,
        style = TextStyle(
            color = Color.White,
            fontFamily = RaleWay,
            fontStyle = FontStyle.Italic,
            fontSize = 45.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier
            .padding(
                start = 15.dp,
                end = 15.dp,
            ),
    )
}