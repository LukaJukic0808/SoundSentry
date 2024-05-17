package hr.ferit.soundsentry.authentication

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
)
