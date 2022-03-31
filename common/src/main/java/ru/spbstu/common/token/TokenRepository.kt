package ru.spbstu.common.token

interface TokenRepository {
    fun getToken(): String?
    fun saveToken(token: String)
    fun getRefresh(): RefreshToken?
    fun saveRefresh(refresh: String)
    fun getOnboardingFlag(): Boolean
    fun saveOnboardingFlag(flag: Boolean)
}
