package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrganizationKitchenApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("is_temporary_stopped")
    val isTemporaryStopped: Boolean?,
    @SerializedName("is_high_load_state")
    val isHighLoadState: Boolean?,
    @SerializedName("cause")
    val cause: Int?,
    @SerializedName("high_load_gap")
    val highLoadGap: Int?,
    @SerializedName("high_load_drop_after_time")
    val highLoadDropAfterTime: Long?
) : Serializable

data class KitchenCauseApi(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?
) : Serializable

data class KitchenTimeApi(
    @SerializedName("minutes")
    val minutes: Int?,
    @SerializedName("title")
    val title: String?
) : Serializable

data class OrganizationKitchenListApi(
    @SerializedName("items")
    val items: List<OrganizationKitchenApi>?,
    @SerializedName("causes")
    val causes: List<KitchenCauseApi>?,
    @SerializedName("stop_times")
    val stopTimes: List<KitchenTimeApi>?,
    @SerializedName("highload_times")
    val highloadTimes: List<KitchenTimeApi>?,
    @SerializedName("highload_add_times")
    val highloadAddTimes: List<KitchenTimeApi>?
) : Serializable

data class OrganizationSetStatusRequestApi(
    @SerializedName("organization_id")
    val organizationId: Int?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("cause")
    val cause: Int?,
    @SerializedName("drop_after_minutes")
    val dropAfterMinutes: Int?,
    @SerializedName("high_load_gap")
    val highLoadGap: Int?,
    @SerializedName("stop_cause_text")
    val stopCauseText: String?
) : Serializable

data class OrganizationDropStatusRequestApi(
    @SerializedName("organization_id")
    val organizationId: Int?
) : Serializable
