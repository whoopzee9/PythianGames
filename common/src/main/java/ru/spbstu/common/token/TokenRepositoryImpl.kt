package ru.spbstu.common.token

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Named

class TokenRepositoryImpl @Inject constructor(
    @Named("encrypted")
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    override fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    override fun getRefresh(): RefreshToken? {
        val token = sharedPreferences.getString(REFRESH_KEY, null)
        return if (token != null) RefreshToken(token) else null
    }

    override fun saveRefresh(refresh: String) {
        sharedPreferences.edit().putString(REFRESH_KEY, refresh).apply()
    }

    override fun getOnboardingFlag(): Boolean {
        return sharedPreferences.getBoolean(ONBOARDING_FLAG_KEY, true)
    }

    override fun saveOnboardingFlag(flag: Boolean) {
        sharedPreferences.edit().putBoolean(ONBOARDING_FLAG_KEY, flag).apply()
    }

    private companion object {
        private const val TOKEN_KEY = "ru.spbstu.pythian_games.TokenRepositoryImpl.token"
        private const val REFRESH_KEY = "ru.spbstu.pythian_games.TokenRepositoryImpl.refresh"
        private const val ONBOARDING_FLAG_KEY = "ru.spbstu.pythian_games.TokenRepositoryImpl.onboarding_flag"
    }
}
