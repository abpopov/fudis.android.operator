package mb.delivery.operator.domain.model

data class KitchenOption(
    val value: Int,
    val title: String
)

data class OrganizationKitchenEntity(
    val id: Int,
    val title: String,
    val address: String?,
    val isTemporaryStopped: Boolean,
    val isHighLoadState: Boolean,
    val cause: Int?,
    val highLoadGap: Int?,
    val highLoadDropAfterTime: Long?
) : ListItem {
    val isActive: Boolean
        get() = isTemporaryStopped || isHighLoadState

    fun label(): String {
        return title.ifBlank { address?.takeIf { it.isNotBlank() } ?: id.toString() }
    }

    override fun unique() = id

    override fun sameContent(other: ListItem) = this == other
}

data class OrganizationKitchenIndex(
    val items: List<OrganizationKitchenEntity>,
    val causes: List<KitchenOption>,
    val stopTimes: List<KitchenOption>,
    val highloadTimes: List<KitchenOption>,
    val highloadAddTimes: List<KitchenOption>
)

const val HIGHLOAD_MODE = "highload"
const val STOP_MODE = "stop"
const val CAUSE_INCIDENT = 9
const val CAUSE_TECH = 19
