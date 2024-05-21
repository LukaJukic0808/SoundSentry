package hr.ferit.soundsentry.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import hr.ferit.soundsentry.model.UserInfoModel

class UserInfoViewModel : ViewModel(), UserInfoModel.Observer {

    private val userInfoModel = UserInfoModel()
    var tokens by mutableStateOf(0)
    var period by mutableStateOf(0)

    init {
        userInfoModel.addListener(this)
        userInfoModel.getRealTimeUpdate()
    }

    suspend fun save(period: Int): Boolean {
        return userInfoModel.save(period)
    }

    override fun update(tokens: Int, period: Int) {
        this.tokens = tokens
        this.period = period
    }
}