package com.leita.leita.common.config

import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

@Configuration
@ConfigurationProperties(prefix = "oci")
class OracleStorageConfig {

    lateinit var region: String
    lateinit var bucketName: String
    lateinit var namespace: String

    @Bean
    @Throws(IOException::class)
    fun objectStorage(): ObjectStorage {
        val provider = ConfigFileAuthenticationDetailsProvider("DEFAULT")

        return ObjectStorageClient.builder()
            .region(region)
            .build(provider)
    }
}
