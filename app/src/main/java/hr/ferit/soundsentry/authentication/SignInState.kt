package hr.ferit.soundsentry.authentication

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)