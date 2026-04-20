package com.leita.leita.file.service

import com.leita.leita.file.util.OracleStorageUtil
import org.springframework.stereotype.Service

import com.leita.leita.file.dto.GeneratePARResponse

@Service
class FileService(
    private val oracleStorageUtil: OracleStorageUtil
) {
    fun createPreAuthenticatedRequest(objectName: String): GeneratePARResponse {
        val parUrl = oracleStorageUtil.createPreAuthenticatedRequest(objectName)
        val finalUrl = oracleStorageUtil.getPublicUrl(objectName)
        return GeneratePARResponse(parUrl, finalUrl)
    }
}
