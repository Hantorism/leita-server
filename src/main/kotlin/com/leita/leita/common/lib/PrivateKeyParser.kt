package com.leita.leita.common.lib

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

class PrivateKeyParser {
    companion object {
        fun parsePrivateKey(privateKey: String): PrivateKey {
            val base64Content = privateKey
                .replace(Regex("-----(BEGIN|END) PRIVATE KEY-----"), "")
                .replace("\\n", "")
                .replace("\\r", "")
                .replace(Regex("[^A-Za-z0-9+/=]"), "") // Base64 알파벳 이외의 모든 문자 제거

            val privateKeyBytes = Base64.getDecoder().decode(base64Content)
            val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")

            return keyFactory.generatePrivate(keySpec)
        }
    }
}