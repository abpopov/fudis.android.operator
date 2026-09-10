package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.data.api.ProjectUrlBuilder
import mb.delivery.operator.data.api.model.BAD_LOGIN
import mb.delivery.operator.data.auth.AuthSession
import mb.delivery.operator.data.auth.SessionEvents
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.notifications.OperatorWebSocket
import mb.delivery.operator.utils.FudisException

class RegisterUseCaseImpl(
    private val repo: Repo,
    private val socket: OperatorWebSocket,
    private val events: SessionEvents
) : RegisterUseCase {

    override fun getContext() = repo.getContext()

    override fun getProjectId() = repo.getProjectId()

    override fun setProjectId(value: Int?) {
        repo.setProjectId(value)
    }

    override fun setProjectCode(value: String) {
        val digits = value.filter { it.isDigit() }
        ProjectUrlBuilder.fromProjectCode(digits)
        repo.setCustomBaseUrl(null)
        repo.setProjectId(digits.toInt())
    }

    override fun setCustomHost(value: String) {
        val base = ProjectUrlBuilder.fromCustomHost(value)
        repo.setProjectId(null)
        repo.setCustomBaseUrl(base)
    }

    override fun clearHostConfig() {
        repo.clearHostConfig()
    }

    override fun getHostDisplayLabel() = repo.getHostDisplayLabel()

    override suspend fun login(login: String, pwd: String): Any {
        val result = repo.auth(
            username = login,
            password = pwd
        )
        val user = result.user
        val token = user?.accessToken
        if (result.result != true || user == null || token.isNullOrEmpty()) {
            throw FudisException(
                ErrorEntity(
                    code = BAD_LOGIN,
                    textCode = "server",
                    type = "FudisException",
                    message = result.errors?.values?.firstOrNull().orEmpty()
                )
            )
        }
        AuthSession.applyUser(repo, socket, events, user)
        return token
    }
}
