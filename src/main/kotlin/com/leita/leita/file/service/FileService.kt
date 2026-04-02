package com.leita.leita.file.service

import com.leita.leita.file.util.OracleStorageUtil
import org.springframework.stereotype.Service

@Service
class FileService(
    private val oracleStorageUtil: OracleStorageUtil
) {
    fun createPreAuthenticatedRequest(objectName: String): String {
        return oracleStorageUtil.createPreAuthenticatedRequest(objectName)
    }
}
