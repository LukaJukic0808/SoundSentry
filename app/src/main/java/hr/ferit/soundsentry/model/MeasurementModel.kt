package hr.ferit.soundsentry.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class MeasurementModel(
    var noiseAmplitude: Int = 0,
    var doesAccelerometerExist: Boolean,
    var doesLightSensorExist: Boolean,
    var movementDetected: Boolean = false,
    var lux: Float = 0f
) {

    suspend fun save(userId: String?): Int {
        val db = Firebase.firestore
        var addedTokens = 0
        var currentTokenBalance = 0
        var period = 15

        if (noiseAmplitude != 0) {
            addedTokens += 10
        }

        if(doesAccelerometerExist) {
            addedTokens += 5
        }

        if (doesLightSensorExist) {
            addedTokens += 5
        }

        if (userId != null) {
            try {
                val currentUserInfoSnapshot = db.collection("users").document(userId).get().await()
                if (currentUserInfoSnapshot != null && currentUserInfoSnapshot.exists()) {
                    val userInfoModel = currentUserInfoSnapshot.toObject(UserInfoModel::class.java)
                    currentTokenBalance = userInfoModel?.tokens!!
                    period = userInfoModel.period
                }
            } catch (e: FirebaseFirestoreException) {
                Log.d("Fetching user info", "Failed")
            }

            db.collection("measurements").add(this)
                .addOnSuccessListener {
                    db.collection("users")
                        .document(userId)
                        .set(UserInfoModel(currentTokenBalance + addedTokens, period))
                }
        }

        return addedTokens
    }
}
