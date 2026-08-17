package mb.delivery.operator.domain.case

import mb.delivery.operator.data.Repo
import mb.delivery.operator.domain.model.ErrorEntity
import mb.delivery.operator.utils.FudisException

class RegisterUseCaseImpl(private val repo: Repo) : RegisterUseCase {

    override fun getContext() = repo.getContext()

    override fun getProjectId() = repo.getProjectId()

    override fun setProjectId(value: Int?) {
        repo.setProjectId(value)
    }

    override suspend fun login(login: String, pwd: String): Any {
        val result = repo.auth(
            username = login,
            password = pwd
        )
        if (result.errorCode != null || result.errorMessage != null) {
            throw FudisException(
                ErrorEntity(
                    code = result.errorCode ?: -1,
                    textCode = "server",
                    type = "FudisException",
                    message = result.errorMessage ?: ""
                )
            )
        } else {
            repo.setUserToken(result.token)
            repo.setOrganizationId(result.user?.organizations?.getOrNull(0))
            return result.token ?: Any()
        }
    }

}