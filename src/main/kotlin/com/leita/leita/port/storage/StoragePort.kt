
package com.leita.leita.port.storage

import java.io.File

interface StoragePort {
    fun createPreAuthenticatedRequest(objectName: String): String
    fun downloadFile(objectName: String): ByteArray
    fun uploadFile(objectName: String, file: File): String
    fun uploadString(objectName: String, content: String): String
}
