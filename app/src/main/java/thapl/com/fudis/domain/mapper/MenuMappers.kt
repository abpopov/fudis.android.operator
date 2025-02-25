package thapl.com.fudis.domain.mapper

import thapl.com.fudis.data.api.model.MenuApi
import thapl.com.fudis.domain.model.MenuEntity

object MenuApiToEntityMapper : BaseMapperSafe<MenuApi, MenuEntity> {

    override fun map(type: MenuApi?): MenuEntity {
        return MenuEntity(
            showMenu = type?.showMenu ?: false,
            showOrders = type?.showOrders ?: false,
            showStopList = type?.showStopList ?: false,
            showHighload = type?.showHighload ?: false
        )
    }

}