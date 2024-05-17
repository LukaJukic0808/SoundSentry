package hr.ferit.soundsentry.view.navigation

sealed class Screen(val route: String) {
    object SignInScreen : Screen("sign_in")
    object HomeScreen : Screen("home")
    object SettingsScreen : Screen("settings")
}