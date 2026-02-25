package com.leita.leita.common.lib

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

class PrivateKeyParser {
    companion object {
        fun parsePrivateKey(privateKey: String): PrivateKey {
            val privateKeyContent = privateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .filter { !it.isWhitespace() }

            val privateKeyBytes = Base64.getDecoder().decode(privateKeyContent)
            val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")

            return keyFactory.generatePrivate(keySpec)
        }
    }
}