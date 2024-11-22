package com.debouncewrite.debouncev2.infrastructure.security

import com.debouncewrite.debouncev2.common.model.UserAction
import com.debouncewrite.debouncev2.infrastructure.security.ratelimiter.model.SecurityEndpointConfig
import com.debouncewrite.debouncev2.application.user.model.Role

//val pathMatcher = AntPathMatcher()

object EndpointSecurityConfigs {

    private val defaultPath = SecurityEndpointConfig(
        path = "/",
        id = 1,
        refillMs = 4000,
        max = 9,
        start = 6,
        penaltyMs = 300000,
        userAction = UserAction.NONE,
    )

    /**
     *
     *      ENDPOINT ID MUST NOT BE CHANGED
     *
     */
    private val endpointConfig: Set<SecurityEndpointConfig> = setOf(
        defaultPath,
        SecurityEndpointConfig(
            path = "/v1/ping",
            id = 2,
            refillMs = 2000,
            max = 10,
            start = 10,
            penaltyMs = 5000,
            userAction = UserAction.PING,
        ),
        SecurityEndpointConfig(
            path = "/v1/rsocket/idtoken",
            id = 3,
            refillMs = 1000,
            max = 20,
            start = 20,
            penaltyMs = 30000,
            userAction = UserAction.NONE,
        ),
        SecurityEndpointConfig(
            path = "/v1/checkbox/metadata",
            id = 4,
            refillMs = 1000,
            max = 10,
            start = 10,
            penaltyMs = 30000,
            userAction = UserAction.NONE,
        ),

        // Admin
        SecurityEndpointConfig(
            path = "/v1/admin/login",
            id = 5,
            refillMs = 5000,
            max = 3,
            start = 2,
            penaltyMs = 120000,
            userAction = UserAction.NONE,
        ),
        SecurityEndpointConfig(
            path = "/v1/admin/persist",
            id = 6,
            refillMs = 5000,
            max = 3,
            start = 2,
            penaltyMs = 120000,
            userAction = UserAction.NONE,
        ),
        SecurityEndpointConfig(
            path = "/v1/admin/auth",
            id = 7,
            refillMs = 4000,
            max = 6,
            start = 6,
            penaltyMs = 120000,
            userAction = UserAction.NONE,
        ),
    )

    private val endpoints: Map<String, SecurityEndpointConfig> by lazy {
        return@lazy endpointConfig.associateBy { it.path }
    }

    private val wildcardEndpoints: Map<String, SecurityEndpointConfig> by lazy {
        val wildcards = endpointConfig.filter { it.path.endsWith("**") }
        val map = HashMap<String, SecurityEndpointConfig>()

        wildcards.forEach {
            map.put(it.path.substringBeforeLast("**"), it)
        }

        return@lazy map
    }

    private val idMap: Map<Int, String> by lazy {
        endpoints.mapValues { it.value.id }.entries.associate { it.value to it.key }
    }

    fun fromId(id: Int): SecurityEndpointConfig {
        val path = idMap[id] ?: throw IllegalArgumentException("No configuration found for ID: $id")
        return endpoints[path]!!
    }

    fun fromPath(path: String): SecurityEndpointConfig {
        val direct = endpoints[path]
        if (direct != null) return direct

        wildcardEndpoints.forEach { endpoint ->
            if (path.startsWith(endpoint.key)) return endpoint.value
        }

        return defaultPath
    }
}
