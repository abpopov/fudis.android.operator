package mb.delivery.operator.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("is_stop")
    val isStopped: Boolean?
) : Serializable

data class CatalogProductsListApi(
    @SerializedName("items")
    val items: List<ProductApi>?
) : Serializable

data class StopListItemApi(
    @SerializedName("catalog_item_id")
    val catalogItemId: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("organization_id")
    val organizationId: Int?,
    @SerializedName("organization_title")
    val organizationTitle: String?,
    @SerializedName("is_stop")
    val isStop: Boolean?
) : Serializable

data class StopListResponseApi(
    @SerializedName("items")
    val items: List<StopListItemApi>?
) : Serializable

data class SetProductStopRequestApi(
    @SerializedName("organization_id")
    val organizationId: Int?,
    @SerializedName("catalog_item_id")
    val catalogItemId: Long?,
    @SerializedName("stop")
    val stop: Boolean?
) : Serializable