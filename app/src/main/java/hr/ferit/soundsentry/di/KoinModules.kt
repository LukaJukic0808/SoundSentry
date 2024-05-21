package hr.ferit.soundsentry.di

import hr.ferit.soundsentry.viewmodel.PermissionViewModel
import hr.ferit.soundsentry.viewmodel.ServiceToggleViewModel
import hr.ferit.soundsentry.viewmodel.SignInViewModel
import hr.ferit.soundsentry.viewmodel.UserInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sensorsModule = module {

}

val viewModelModule = module {
    viewModel<SignInViewModel> { SignInViewModel() }
    viewModel<ServiceToggleViewModel> { ServiceToggleViewModel(androidContext()) }
    viewModel<UserInfoViewModel> { UserInfoViewModel() }
    viewModel<PermissionViewModel> { PermissionViewModel() }
}