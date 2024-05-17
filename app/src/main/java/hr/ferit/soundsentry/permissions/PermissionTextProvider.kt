package hr.ferit.soundsentry.permissions

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}