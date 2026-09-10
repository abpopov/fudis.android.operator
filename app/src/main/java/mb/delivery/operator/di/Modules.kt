package mb.delivery.operator.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import mb.delivery.operator.data.Repo
import mb.delivery.operator.data.RepoImpl
import mb.delivery.operator.data.api.Api
import mb.delivery.operator.data.api.ApiImpl
import mb.delivery.operator.data.auth.SessionEvents
import mb.delivery.operator.data.local.AppDb
import mb.delivery.operator.data.local.Local
import mb.delivery.operator.data.local.LocalImpl
import mb.delivery.operator.data.prefs.Prefs
import mb.delivery.operator.data.prefs.PrefsImpl
import mb.delivery.operator.domain.case.*
import mb.delivery.operator.notifications.OperatorWebSocket
import mb.delivery.operator.notifications.OperatorWebSocketImpl
import mb.delivery.operator.ui.categories.CategoriesViewModel
import mb.delivery.operator.ui.dialogs.HelpViewModel
import mb.delivery.operator.ui.dialogs.PauseViewModel
import mb.delivery.operator.ui.highload.HighloadViewModel
import mb.delivery.operator.ui.orders.OrdersViewModel
import mb.delivery.operator.ui.register.RegisterViewModel
import mb.delivery.operator.ui.splash.SplashViewModel
import mb.delivery.operator.ui.stops.StopApplyViewModel
import mb.delivery.operator.ui.stops.StopsViewModel

val appModule = module {
    single<Repo> { RepoImpl(androidContext(), get(), get(), get()) }
    single { SessionEvents() }
    single<SplashUseCase> { SplashUseCaseImpl(get(), get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get(), get(), get()) }
    single<OrdersUseCase> { OrdersUseCaseImpl(get()) }
    single<StopsUseCase> { StopsUseCaseImpl(get()) }
    single<PauseUseCase> { PauseUseCaseImpl(get()) }
    single<HelpUseCase> { HelpUseCaseImpl(get(), get()) }
    single<CategoriesUseCase> { CategoriesUseCaseImpl(get()) }
    single<OperatorWebSocket> { OperatorWebSocketImpl(get()) }
    single<Api> { ApiImpl(get(), get(), get(), get()) }
    single<Local> { LocalImpl(get()) }
    single<Prefs> { PrefsImpl(androidContext(), get()) }
    single {
        Room.databaseBuilder(androidContext(), AppDb::class.java, "fudis.local")
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { OrdersViewModel(get(), get()) }
    viewModel { StopsViewModel(get()) }
    viewModel { StopApplyViewModel(get()) }
    viewModel { PauseViewModel(get()) }
    viewModel { HighloadViewModel(get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { HelpViewModel(get()) }
}