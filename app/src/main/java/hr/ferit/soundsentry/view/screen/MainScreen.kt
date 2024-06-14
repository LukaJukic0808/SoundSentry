package hr.ferit.soundsentry.view.screen

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import hr.ferit.soundsentry.R
import hr.ferit.soundsentry.authentication.GoogleAuthClient
import hr.ferit.soundsentry.permissions.isMeasurementRunning
import hr.ferit.soundsentry.service.MeasurementService
import hr.ferit.soundsentry.view.components.NetworkChecker
import hr.ferit.soundsentry.view.navigation.Screen
import hr.ferit.soundsentry.viewmodel.SignInViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen() {
    val applicationContext = LocalContext.current
    val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
        )
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SignInScreen.route) {
        composable(Screen.SignInScreen.route) {
            val signInViewModel: SignInViewModel = koinViewModel()
            val state by signInViewModel.state.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(key1 = Unit) {
                if (googleAuthClient.getSignedInUser() != null) {
                    navController.navigate(Screen.HomeScreen.route)
                }
            }

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        R.string.sign_in_success,
                        Toast.LENGTH_SHORT,
                    ).show()
                    navController.navigate(Screen.HomeScreen.route)
                    signInViewModel.resetState()
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        coroutineScope.launch {
                            val signInResult = googleAuthClient.signInWithIntent(
                                intent = result.data ?: return@launch,
                            )
                            signInViewModel.onSignInResult(signInResult)
                        }
                    }
                },
            )

            SignInScreen(
                state = state,
                onSignInClick = {
                    if (!NetworkChecker.isNetworkAvailable(applicationContext)) {
                        Toast.makeText(
                            applicationContext,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthClient.signIn(applicationContext)
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch,
                                ).build(),
                            )
                        }
                    }
                },
            )
        }

        composable(Screen.HomeScreen.route) {
            val coroutineScope = rememberCoroutineScope()
            HomeScreen(
                userData = googleAuthClient.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthClient.signOut()
                        Toast.makeText(
                            applicationContext,
                            R.string.sign_out_success,
                            Toast.LENGTH_SHORT,
                        ).show()
                        if (applicationContext.isMeasurementRunning()) {
                            Intent(applicationContext, MeasurementService::class.java).also {
                                it.action = MeasurementService.Actions.STOP.toString()
                                ActivityCompat.startForegroundService(applicationContext, it)
                            }
                        }
                        navController.popBackStack()
                    }
                },
                navController = navController,
            )
        }

        composable(Screen.SettingsScreen.route) {
            val coroutineScope = rememberCoroutineScope()
            SettingsScreen(
                userData = googleAuthClient.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        googleAuthClient.signOut()
                        Toast.makeText(
                            applicationContext,
                            R.string.sign_out_success,
                            Toast.LENGTH_SHORT,
                        ).show()
                        if (applicationContext.isMeasurementRunning()) {
                            Intent(applicationContext, MeasurementService::class.java).also {
                                it.action = MeasurementService.Actions.STOP.toString()
                                ActivityCompat.startForegroundService(applicationContext, it)
                            }
                        }
                        navController.navigate(Screen.SignInScreen.route) {
                            popUpTo(Screen.SignInScreen.route)
                        }
                    }
                },
                navController = navController,
            )
        }
    }
}