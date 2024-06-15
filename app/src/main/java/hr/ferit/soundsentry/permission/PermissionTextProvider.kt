package hr.ferit.soundsentry.permission

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}