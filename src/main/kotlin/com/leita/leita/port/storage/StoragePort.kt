
package com.leita.leita.port.storage

interface StoragePort {
    fun createPreAuthenticatedRequest(objectName: String): String
}
