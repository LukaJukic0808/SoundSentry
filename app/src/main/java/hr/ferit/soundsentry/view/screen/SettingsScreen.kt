package hr.ferit.soundsentry.view.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import hr.ferit.soundsentry.authentication.UserData
import hr.ferit.soundsentry.viewmodel.UserInfoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen (
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel = koinViewModel(),
){
}