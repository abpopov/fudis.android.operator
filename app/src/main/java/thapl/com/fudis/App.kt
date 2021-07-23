package thapl.com.fudis

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import thapl.com.fudis.di.appModule

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}

@GlideModule
class CustomGlideModuleV4 : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions().format(DecodeFormat.PREFER_RGB_565)
        )
    }

}