package hr.ferit.soundsentry.model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class UserInfoModel(
    var tokens: Int = 0,
    var period: Int = 0
) {
    private val db = FirebaseFirestore.getInstance()
    private val userId = Firebase.auth.currentUser?.uid
    private val listeners = mutableListOf<Observer>()

    fun getRealTimeUpdate(){
        if (userId != null) {
            val docRef = db.collection("users").document(userId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeScreen", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userInfo = snapshot.toObject(UserInfoModel::class.java)
                    tokens = userInfo?.tokens!!
                    period = userInfo.period

                    for(listener in listeners) {
                        listener.update(tokens, period)
                    }

                } else {
                    Log.d("HomeScreen", "Snapshot")
                }
            }
        }
    }

    suspend fun save(period: Int): Boolean {
        return if (userId != null) {
            val docRef = db.collection("users").document(userId)
            try {
                docRef.update("period", period).await()
                this.period = period
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    fun addListener(observer: Observer) {
        listeners.add(observer)
    }

    interface Observer {
        fun update(tokens: Int, period: Int)
    }
}