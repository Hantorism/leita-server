package com.leita.leita.service

import com.leita.leita.util.storage.OracleStorageUtil
import org.springframework.stereotype.Service

@Service
class FileService(
    private val oracleStorageUtil: OracleStorageUtil
) {
    fun createPreAuthenticatedRequest(objectName: String): String {
        return oracleStorageUtil.createPreAuthenticatedRequest(objectName)
    }
}
