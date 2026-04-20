package com.leita.leita.file.util

import com.leita.leita.common.config.OracleStorageConfig
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
import com.oracle.bmc.objectstorage.requests.GetObjectRequest
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest
import com.oracle.bmc.objectstorage.requests.PutObjectRequest
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.Date
import java.util.concurrent.TimeUnit

@Component
class OracleStorageUtil(
    private val objectStorage: ObjectStorage,
    private val oracleStorageConfig: OracleStorageConfig
) {

    fun createPreAuthenticatedRequest(objectName: String): String {
        val expirationTime = Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))

        val details = CreatePreauthenticatedRequestDetails.builder()
            .name("par-for-$objectName-${System.currentTimeMillis()}")
            .objectName(objectName)
            .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite)
            .timeExpires(expirationTime)
            .build()

        val request = CreatePreauthenticatedRequestRequest.builder()
            .namespaceName(oracleStorageConfig.namespace)
            .bucketName(oracleStorageConfig.bucketName)
            .createPreauthenticatedRequestDetails(details)
            .build()

        val response = objectStorage.createPreauthenticatedRequest(request)
        return "https://objectstorage.ap-chuncheon-1.oraclecloud.com" + response.preauthenticatedRequest.accessUri
    }

    fun getPublicUrl(objectName: String): String {
        return "https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/${oracleStorageConfig.namespace}/b/${oracleStorageConfig.bucketName}/o/${objectName}"
    }

    fun downloadFile(objectName: String): ByteArray {
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

    fun readString(objectName: String): String {
        return String(downloadFile(objectName), StandardCharsets.UTF_8)
    }

    fun extractObjectName(url: String): String {
        val marker = "/o/"
        val index = url.indexOf(marker)
        val objectName = if (index != -1) {
            url.substring(index + marker.length)
        } else {
            url
        }
        return java.net.URLDecoder.decode(objectName, StandardCharsets.UTF_8)
    }

    fun getNamespace(): String = oracleStorageConfig.namespace
    fun getBucketName(): String = oracleStorageConfig.bucketName

    fun uploadString(objectName: String, content: String): String {
        val bytes = content.toByteArray(StandardCharsets.UTF_8)
        val inputStream = ByteArrayInputStream(bytes)

        return upload(objectName, bytes.size.toLong(), "text/plain; charset=UTF-8", inputStream)
    }

    fun uploadFile(objectName: String, file: File): String {
        val contentType = Files.probeContentType(file.toPath()) ?: "application/octet-stream"
        val contentLength = file.length()

        return upload(objectName, contentLength, contentType, file.inputStream())
    }

    fun deleteFolder(prefix: String) {
        var nextStartWith: String? = null
        do {
            val listRequest = ListObjectsRequest.builder()
                .namespaceName(oracleStorageConfig.namespace)
                .bucketName(oracleStorageConfig.bucketName)
                .prefix(prefix)
                .start(nextStartWith)
                .build()

            val listResponse = objectStorage.listObjects(listRequest)

            listResponse.listObjects.objects.forEach { summary ->
                deleteObject(summary.name)
            }

            nextStartWith = listResponse.listObjects.nextStartWith
        } while (!nextStartWith.isNullOrEmpty())
    }

    private fun deleteObject(objectName: String) {
        val request = DeleteObjectRequest.builder()
            .namespaceName(oracleStorageConfig.namespace)
            .bucketName(oracleStorageConfig.bucketName)
            .objectName(objectName)
            .build()

        try {
            objectStorage.deleteObject(request)
        } catch (e: Exception) {
            println("Failed to delete object: $objectName, reason: ${e.message}")
        }
    }

    private fun upload(
        objectName: String, contentLength: Long,
        contentType: String, putObjectBody: InputStream
    ): String {
        val putObjectRequest = PutObjectRequest.builder()
            .namespaceName(oracleStorageConfig.namespace)
            .bucketName(oracleStorageConfig.bucketName)
            .objectName(objectName)
            .contentLength(contentLength)
            .contentType(contentType)
            .putObjectBody(putObjectBody)
            .build()

        objectStorage.putObject(putObjectRequest)

        return "https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/${oracleStorageConfig.namespace}/b/${oracleStorageConfig.bucketName}/o/${objectName}"
    }
}

