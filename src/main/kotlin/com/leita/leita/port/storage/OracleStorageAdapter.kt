package com.leita.leita.port.storage

import com.leita.leita.common.config.OracleStorageConfig
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.requests.GetObjectRequest
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.concurrent.TimeUnit

@Component
class OracleStorageAdapter(
    private val objectStorage: ObjectStorage,
    private val oracleStorageConfig: OracleStorageConfig
) : StoragePort {
    override fun createPreAuthenticatedRequest(objectName: String): String {
        val expirationTime = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))

        val details = CreatePreauthenticatedRequestDetails.builder()
            .name("par-for-$objectName-${System.currentTimeMillis()}")
            .objectName(objectName)
            .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectRead)
            .timeExpires(expirationTime)
            .build()

        val request = CreatePreauthenticatedRequestRequest.builder()
            .namespaceName(oracleStorageConfig.namespace)
            .bucketName(oracleStorageConfig.bucketName)
            .createPreauthenticatedRequestDetails(details)
            .build()

        val response = objectStorage.createPreauthenticatedRequest(request)
        return "https://" + oracleStorageConfig.namespace + ".objectstorage." + oracleStorageConfig.region + ".oci.customer-oci.com" + response.preauthenticatedRequest.accessUri
    }

    override fun downloadFile(objectName: String): ByteArray {
        val getObjectRequest = GetObjectRequest.builder()
            .namespaceName(oracleStorageConfig.namespace)
            .bucketName(oracleStorageConfig.bucketName)
            .objectName(objectName)
            .build()

        val response = objectStorage.getObject(getObjectRequest)

        val outputStream = ByteArrayOutputStream()
        response.inputStream.use { input ->
            input.copyTo(outputStream)
        }
        return outputStream.toByteArray()
    }
}
