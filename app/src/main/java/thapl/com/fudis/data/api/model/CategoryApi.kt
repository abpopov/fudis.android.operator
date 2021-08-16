package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("sub_categories")
    val subCategories: List<CategoryApi>?,
    @SerializedName("catalog_items")
    val catalogItems: List<CatalogApi>?
) : Serializable