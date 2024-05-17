package hr.ferit.soundsentry.view.screen

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import hr.ferit.soundsentry.R
import hr.ferit.soundsentry.authentication.UserData
import hr.ferit.soundsentry.permissions.MicrophonePermissionTextProvider
import hr.ferit.soundsentry.permissions.hasRecordAudioPermission
import hr.ferit.soundsentry.service.MeasurementService
import hr.ferit.soundsentry.view.components.HomeScreenBody
import hr.ferit.soundsentry.view.components.NetworkChecker
import hr.ferit.soundsentry.view.components.PermissionDialog
import hr.ferit.soundsentry.view.components.StatusBar
import hr.ferit.soundsentry.view.navigation.Screen
import hr.ferit.soundsentry.viewmodel.PermissionViewModel
import hr.ferit.soundsentry.viewmodel.ServiceToggleViewModel
import hr.ferit.soundsentry.viewmodel.UserInfoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
@TargetApi(29)
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel = koinViewModel(),
    serviceToggleViewModel: ServiceToggleViewModel = koinViewModel(),
    permissionViewModel: PermissionViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val permissionsToRequest: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    } else {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    // permission logic
    val dialogQueue = permissionViewModel.visiblePermissionDialogQueue
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            perms.keys.forEach { permission ->
                permissionViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true,
                )
            }
        },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_blue)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatusBar(userData, onSignOut, userInfoViewModel.tokens)
            HomeScreenBody(
                onSettingsClick = {
                    navController.navigate(Screen.SettingsScreen.route)
                },
                onToggleService = {
                    if (!NetworkChecker.isNetworkAvailable(context) && !serviceToggleViewModel.isServiceRunning) {
                        Toast.makeText(
                            context,
                            R.string.network_error,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else if (context.hasRecordAudioPermission() && !serviceToggleViewModel.isServiceRunning) {
                        Intent(context, MeasurementService::class.java).also {
                            it.action = MeasurementService.Actions.START.toString()
                            it.putExtra("user_id", userData?.userId)
                            ActivityCompat.startForegroundService(context, it)
                            serviceToggleViewModel.toggleService()
                        }
                    } else if (!serviceToggleViewModel.isServiceRunning) {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    } else {
                        Intent(context, MeasurementService::class.java).also {
                            it.action = MeasurementService.Actions.STOP.toString()
                            ActivityCompat.startForegroundService(context, it)
                            serviceToggleViewModel.toggleService()
                        }
                    }
                },
                serviceToggleViewModel.isServiceRunning,
            )
        }
    }

    // showing permission dialogs
    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.RECORD_AUDIO -> {
                        MicrophonePermissionTextProvider(context)
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission,
                ),
                onDismiss = permissionViewModel::dismissDialog,
                onOkClick = {
                    permissionViewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission),
                    )
                },
                onGoToAppSettingsClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    )
                    context.startActivity(intent)
                },
            )
        }
}