package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.notifications.OperatorWebSocket

class HelpUseCaseImpl(
    private val repo: Repo,
    private val socket: OperatorWebSocket
) : HelpUseCase {

    override fun getContext() = repo.getContext()

    override fun logout() {
        socket.clear()
        repo.clearSession()
    }
}
