package dev.triumphteam.backend.func

import java.io.File
import java.security.MessageDigest
import kotlin.experimental.and

private val SHA_DIGEST = MessageDigest.getInstance("SHA-1")

fun File.checksum(): String {
    SHA_DIGEST.reset()

    inputStream().use { stream ->
        val byteArray = ByteArray(1024)
        var bytesCount: Int
        while (stream.read(byteArray).also { bytesCount = it } != -1) {
            SHA_DIGEST.update(byteArray, 0, bytesCount)
        }
    }

    val result = buildString {
        for (byte in SHA_DIGEST.digest()) {
            append(((byte and 0xff.toByte()) + 0x100).toString(16).substring(1))
        }
        trimToSize()
    }

    return result
}