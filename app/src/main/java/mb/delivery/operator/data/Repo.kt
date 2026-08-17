package mb.delivery.operator.data

import android.content.Context
import mb.delivery.operator.data.api.Api
import mb.delivery.operator.data.local.Local
import mb.delivery.operator.data.prefs.Prefs

interface Repo : Api, Local, Prefs {
    fun getContext(): Context
}