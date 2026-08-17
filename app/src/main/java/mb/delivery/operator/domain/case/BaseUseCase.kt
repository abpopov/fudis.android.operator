package mb.delivery.operator.domain.case

import android.content.Context

interface BaseUseCase {
    fun getContext(): Context
}