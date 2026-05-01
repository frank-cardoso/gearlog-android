package br.edu.unisatc.gearlog.data.remote

import android.util.Log
import retrofit2.HttpException

class FipeDataSource(
    private val api: FipeApiService
) {
    suspend fun getLatestReferenceCode(): Int {
        val references = api.getReferences()
        return references.firstOrNull()?.code
            ?: throw IllegalStateException("Referencia FIPE indisponivel.")
    }

    suspend fun getBrands(referenceCode: Int): List<FipeOptionDto> {
        return api.getBrands(referenceCode)
    }

    suspend fun getModels(brandId: String, referenceCode: Int): List<FipeOptionDto> {
        return api.getModels(brandId, referenceCode)
    }

    suspend fun getYears(brandId: String, modelId: String, referenceCode: Int): List<FipeOptionDto> {
        return api.getYears(brandId, modelId, referenceCode)
    }

    fun mapError(throwable: Throwable): Throwable {
        if (throwable is HttpException) {
            logHttpError(throwable)
            return when (throwable.code()) {
                429 -> IllegalStateException("Limite de requisicoes atingido. Tente novamente amanha.")
                401, 403 -> IllegalStateException("Acesso negado. Verifique o token da API.")
                else -> IllegalStateException("Erro ao acessar a FIPE (${throwable.code()}).")
            }
        }
        return throwable
    }

    private fun logHttpError(exception: HttpException) {
        val errorBody = runCatching { exception.response()?.errorBody()?.string() }
            .getOrNull()
            ?: "<empty>"
        Log.e("FipeDataSource", "HTTP ${exception.code()} - $errorBody")
    }
}
