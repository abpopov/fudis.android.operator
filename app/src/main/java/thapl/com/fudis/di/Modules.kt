package thapl.com.fudis.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*single<Repo> { RepoImpl(androidContext(), get(), get(), get()) }
    single<SplashUseCase> { SplashUseCaseImpl(get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get()) }
    single<Api> { ApiImpl(androidContext(), get(), get()) }
    single<Local> { LocalImpl(get()) }
    single<Prefs> { PrefsImpl(androidContext(), get()) }
*//*    single {
        Room.databaseBuilder(androidContext(), AppDb::class.java, "nauka.local")
            .fallbackToDestructiveMigration()
            .build()
    }*//*
    single {
        GsonBuilder().setLenient().create()
    }
    viewModel { SplashViewModel(get()) }
    viewModel { RegisterViewModel(get()) }*/
}