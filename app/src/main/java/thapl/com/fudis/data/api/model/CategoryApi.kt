package thapl.com.fudis.data.api.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryApi(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("show_on_main_page")
    val showMainPage: Boolean?,
    @SerializedName("show_in_top_menu")
    val showTopMenu: Boolean?,
    @SerializedName("children")
    val children: List<CategoryApi>?
) : Serializable