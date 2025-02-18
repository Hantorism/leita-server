package com.leita.leita.common.config.env

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SpringEnv(private val environment: Environment) {

    fun isProdProfile(): Boolean {
        return environment.activeProfiles.contains(EnvStep.PROD.displayName)
    }

    fun isDevProfile(): Boolean {
        return environment.activeProfiles.contains(EnvStep.DEV.displayName)
    }

    fun isLocalProfile(): Boolean {
        return environment.activeProfiles.contains(EnvStep.LOCAL.displayName)
    }

    fun getProfile(): String {
        return environment.activeProfiles.firstOrNull() ?: "default"
    }
}