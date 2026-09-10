package mb.delivery.operator.ui.dialogs

import androidx.lifecycle.MutableLiveData
import mb.delivery.operator.domain.case.PauseUseCase
import mb.delivery.operator.domain.model.CAUSE_INCIDENT
import mb.delivery.operator.domain.model.CAUSE_TECH
import mb.delivery.operator.domain.model.HIGHLOAD_MODE
import mb.delivery.operator.domain.model.KitchenOption
import mb.delivery.operator.domain.model.OrganizationKitchenEntity
import mb.delivery.operator.domain.model.OrganizationKitchenIndex
import mb.delivery.operator.domain.model.ResultEntity
import mb.delivery.operator.domain.model.STOP_MODE
import mb.delivery.operator.ui.base.BaseViewModel
import mb.delivery.operator.utils.SingleLiveEvent

class PauseViewModel(private val useCase: PauseUseCase) : BaseViewModel() {

    val catalog = MutableLiveData<ResultEntity<OrganizationKitchenIndex>>()
    val selectedOrg = MutableLiveData<OrganizationKitchenEntity?>()
    val mode = MutableLiveData(HIGHLOAD_MODE)
    val dropAfterMinutes = MutableLiveData<Int?>()
    val highLoadGap = MutableLiveData<Int?>()
    val cause = MutableLiveData<Int?>()
    val comment = MutableLiveData("")
    val canSubmit = MutableLiveData(false)
    val request = SingleLiveEvent<ResultEntity<OrganizationKitchenEntity>>()
    val close = SingleLiveEvent<Boolean>()

    fun load() {
        doPostActionRequest(
            catalog,
            block = { useCase.load() },
            action = { result ->
                if (result is ResultEntity.Success) {
                    val idle = result.data.items.filter { !it.isActive }
                    val saved = useCase.getSavedOrganizationId()
                    val selected = when {
                        idle.size == 1 -> idle.first()
                        else -> idle.firstOrNull { it.id == saved }
                    }
                    selectedOrg.value = selected
                    selected?.let { useCase.saveOrganizationId(it.id) }
                    resetForm()
                }
            }
        )
    }

    fun selectOrganization(org: OrganizationKitchenEntity?) {
        if (selectedOrg.value?.id == org?.id) {
            refreshCanSubmit()
            return
        }
        if (org != null) {
            useCase.saveOrganizationId(org.id)
        }
        selectedOrg.value = org
        refreshCanSubmit()
    }

    fun selectMode(value: String) {
        if (mode.value == value) {
            return
        }
        mode.value = value
        dropAfterMinutes.value = null
        highLoadGap.value = null
        refreshCanSubmit()
    }

    fun selectDropAfter(minutes: Int) {
        dropAfterMinutes.value = minutes
        refreshCanSubmit()
    }

    fun selectGap(minutes: Int) {
        highLoadGap.value = minutes
        refreshCanSubmit()
    }

    fun selectCause(id: Int) {
        cause.value = id
        refreshCanSubmit()
    }

    fun setComment(value: String) {
        comment.value = value
        refreshCanSubmit()
    }

    fun timesForMode(): List<KitchenOption> {
        val data = (catalog.value as? ResultEntity.Success)?.data ?: return listOf()
        return if (mode.value == STOP_MODE) data.stopTimes else data.highloadTimes
    }

    fun idleOrgs(): List<OrganizationKitchenEntity> {
        return (catalog.value as? ResultEntity.Success)?.data?.items
            ?.filter { !it.isActive }
            .orEmpty()
    }

    fun submit() {
        val org = selectedOrg.value ?: return
        if (org.isActive) {
            return
        }
        val currentMode = mode.value ?: return
        val period = dropAfterMinutes.value ?: return
        val currentCause = cause.value ?: return
        val gap = if (currentMode == HIGHLOAD_MODE) highLoadGap.value else null
        if (currentMode == HIGHLOAD_MODE && gap == null) {
            return
        }
        val text = comment.value?.trim().orEmpty()
        doPostActionRequest(
            request,
            block = {
                useCase.setStatus(
                    organizationId = org.id,
                    mode = currentMode,
                    cause = currentCause,
                    dropAfterMinutes = period,
                    highLoadGap = gap,
                    stopCauseText = if (needsComment(currentCause)) text else null
                )
            },
            action = { result ->
                if (result is ResultEntity.Success) {
                    replaceOrg(result.data)
                    close.postValue(true)
                }
            }
        )
    }

    private fun replaceOrg(updated: OrganizationKitchenEntity) {
        val current = catalog.value
        if (current is ResultEntity.Success) {
            catalog.postValue(
                ResultEntity.Success(
                    current.data.copy(
                        items = current.data.items.map {
                            if (it.id == updated.id) updated else it
                        }
                    )
                )
            )
        }
        selectedOrg.postValue(updated)
    }

    private fun resetForm() {
        mode.value = HIGHLOAD_MODE
        dropAfterMinutes.value = null
        highLoadGap.value = null
        cause.value = null
        comment.value = ""
        refreshCanSubmit()
    }

    private fun refreshCanSubmit() {
        val org = selectedOrg.value
        if (org == null || org.isActive) {
            canSubmit.postValue(false)
            return
        }
        val currentMode = mode.value
        val periodOk = dropAfterMinutes.value != null
        val causeId = cause.value
        val causeOk = causeId != null
        val gapOk = currentMode != HIGHLOAD_MODE || highLoadGap.value != null
        val commentOk = causeId == null || !needsComment(causeId) || comment.value.orEmpty().trim().isNotEmpty()
        canSubmit.postValue(periodOk && causeOk && gapOk && commentOk)
    }

    fun needsComment(causeId: Int? = cause.value): Boolean {
        return causeId == CAUSE_INCIDENT || causeId == CAUSE_TECH
    }
}
