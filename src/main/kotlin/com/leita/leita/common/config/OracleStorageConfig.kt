package com.leita.leita.common.config

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Supplier

@Configuration
@ConfigurationProperties(prefix = "oci")
class OracleStorageConfig {

    // Common properties
    lateinit var region: String
    lateinit var bucketName: String
    lateinit var namespace: String

    // Properties for API Key authentication
    lateinit var tenancyOcid: String
    lateinit var userOcid: String
    lateinit var fingerprint: String
    lateinit var privatekeyPath: String

    @Bean
    @Throws(IOException::class)
    fun objectStorage(): ObjectStorage {
        val provider = SimpleAuthenticationDetailsProvider.builder()
            .tenantId(tenancyOcid)
            .userId(userOcid)
            .fingerprint(fingerprint)
            .privateKeySupplier(Supplier { 
                val keyFileContent = String(Files.readAllBytes(Paths.get(privatekeyPath)))
                keyFileContent.byteInputStream()
            })
            .build()

        return ObjectStorageClient.builder()
            .region(region)
            .build(provider)
    }
}
