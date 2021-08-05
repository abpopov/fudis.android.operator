package thapl.com.fudis.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import thapl.com.fudis.data.Repo
import thapl.com.fudis.data.RepoImpl
import thapl.com.fudis.data.api.Api
import thapl.com.fudis.data.api.ApiImpl
import thapl.com.fudis.data.local.AppDb
import thapl.com.fudis.data.local.Local
import thapl.com.fudis.data.local.LocalImpl
import thapl.com.fudis.data.prefs.Prefs
import thapl.com.fudis.data.prefs.PrefsImpl
import thapl.com.fudis.domain.case.*
import thapl.com.fudis.ui.dialogs.PauseViewModel
import thapl.com.fudis.ui.orders.OrdersViewModel
import thapl.com.fudis.ui.register.RegisterViewModel
import thapl.com.fudis.ui.splash.SplashViewModel
import thapl.com.fudis.ui.stops.StopsViewModel

val appModule = module {
    single<Repo> { RepoImpl(androidContext(), get(), get(), get()) }
    single<SplashUseCase> { SplashUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get()) }
    single<OrdersUseCase> { OrdersUseCaseImpl(get()) }
    single<StopsUseCase> { StopsUseCaseImpl(get()) }
    single<PauseUseCase> { PauseUseCaseImpl(get()) }
    single<Api> { ApiImpl(androidContext(), get(), get()) }
    single<Local> { LocalImpl(get()) }
    single<Prefs> { PrefsImpl(androidContext(), get()) }
    single {
        Room.databaseBuilder(androidContext(), AppDb::class.java, "fudis.local")
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        GsonBuilder().setLenient().create()
    }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { StopsViewModel(get()) }
    viewModel { PauseViewModel(get()) }
}