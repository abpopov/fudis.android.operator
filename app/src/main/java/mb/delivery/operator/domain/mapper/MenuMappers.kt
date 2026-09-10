package mb.delivery.operator.domain.mapper

import mb.delivery.operator.data.api.model.MenuApi
import mb.delivery.operator.domain.model.MenuEntity

object MenuApiToEntityMapper : BaseMapperSafe<MenuApi, MenuEntity> {

    override fun map(type: MenuApi?): MenuEntity {
        return MenuEntity(
            showMenu = type?.showMenu ?: false,
            showOrders = type?.showOrders ?: false,
            showStopList = type?.showStopList ?: false,
            showHighload = type?.showHighload ?: false,
            allowStatusWithoutDishesReady = type?.allowStatusWithoutDishesReady ?: false,
            allowEditOrder = type?.allowEditOrder ?: false,
            enableManualPosExport = type?.enableManualPosExport ?: false
        )
    }

}
