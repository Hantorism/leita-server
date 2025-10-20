
package com.leita.leita.port.storage

interface StoragePort {
    fun createPreAuthenticatedRequest(objectName: String): String
    fun downloadFile(objectName: String): ByteArray
}
