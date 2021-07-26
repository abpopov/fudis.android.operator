package thapl.com.fudis.data

import android.content.Context
import thapl.com.fudis.data.api.Api
import thapl.com.fudis.data.local.Local
import thapl.com.fudis.data.prefs.Prefs

interface Repo : Api, Local, Prefs {
    fun getContext(): Context
}