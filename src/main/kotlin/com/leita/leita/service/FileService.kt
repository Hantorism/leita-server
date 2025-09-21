package com.leita.leita.service

import com.leita.leita.port.storage.StoragePort
import org.springframework.stereotype.Service

@Service
class FileService(
    private val storagePort: StoragePort
) {
    fun createPreAuthenticatedRequest(objectName: String): String {
        return storagePort.createPreAuthenticatedRequest(objectName)
    }
}
