package hr.ferit.soundsentry.authentication

data class UserData(
    val userId: String,
    val username: String?,
    val isNewUser: Boolean?,
    val profilePictureUrl: String?,
)
