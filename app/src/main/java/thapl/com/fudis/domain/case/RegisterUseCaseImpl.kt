package thapl.com.fudis.domain.case

import thapl.com.fudis.data.Repo
import thapl.com.fudis.domain.model.ErrorEntity
import thapl.com.fudis.utils.FudisException

class RegisterUseCaseImpl(private val repo: Repo) : RegisterUseCase {

    override fun getContext() = repo.getContext()

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