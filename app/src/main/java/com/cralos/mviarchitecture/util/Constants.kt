package com.cralos.mviarchitecture.util

class Constants {
    companion object {
        const val BASE_URL = "https://open-api.xyz/api/"
        const val NETWORK_TIMEOUT = 8000L
        const val TESTING_NETWORK_DELAY = 3000L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing
        const val PASSWORD_RESET_URL = "https://open-api.xyz/password_reset/"
        const val PAGINATION_PAGE_SIZE = 10
    }
}