package hr.ferit.soundsentry.di

import hr.ferit.soundsentry.model.LightSensorModel
import hr.ferit.soundsentry.model.AccelerometerModel
import hr.ferit.soundsentry.sensing.sensor.LightSensor
import hr.ferit.soundsentry.sensing.sensor.Accelerometer
import hr.ferit.soundsentry.viewmodel.PermissionViewModel
import hr.ferit.soundsentry.viewmodel.ServiceToggleViewModel
import hr.ferit.soundsentry.viewmodel.SignInViewModel
import hr.ferit.soundsentry.viewmodel.UserInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sensorsModule = module {
    single<Accelerometer> { Accelerometer(androidContext()) }
    single<LightSensor> { LightSensor(androidContext()) }
    single<AccelerometerModel> { AccelerometerModel(get<Accelerometer>()) }
    single<LightSensorModel> { LightSensorModel(get<LightSensor>()) }
}

val viewModelModule = module {
    viewModel<SignInViewModel> { SignInViewModel() }
    viewModel<ServiceToggleViewModel> { ServiceToggleViewModel(androidContext()) }
    viewModel<UserInfoViewModel> { UserInfoViewModel() }
    viewModel<PermissionViewModel> { PermissionViewModel() }
}