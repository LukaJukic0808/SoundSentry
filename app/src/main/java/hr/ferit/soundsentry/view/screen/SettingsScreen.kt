package hr.ferit.soundsentry.view.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import hr.ferit.soundsentry.R
import hr.ferit.soundsentry.authentication.UserData
import hr.ferit.soundsentry.view.components.NetworkChecker
import hr.ferit.soundsentry.view.components.SettingsScreenBody
import hr.ferit.soundsentry.view.components.StatusBar
import hr.ferit.soundsentry.viewmodel.UserInfoViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen (
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel = koinViewModel(),
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.login_background),
                contentScale = ContentScale.FillBounds,
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatusBar(userData, onSignOut, userInfoViewModel.tokens)
            SettingsScreenBody(
                onBackClick = {
                    navController.popBackStack()
                },
                onSetPeriodClick = { period ->
                    if (!NetworkChecker.isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        // check and save to DB if correct, quit current running service
                        coroutineScope.launch {
                            val isSuccessful = userInfoViewModel.save(period)
                            if (isSuccessful) {
                                Toast.makeText(
                                    context,
                                    R.string.update,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.update_failed,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                        navController.popBackStack()
                    }
                },
                userInfoViewModel.period.toFloat()
            )
        }
    }
}