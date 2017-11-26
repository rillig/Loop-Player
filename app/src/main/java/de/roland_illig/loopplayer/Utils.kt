package de.roland_illig.loopplayer

import android.content.Context
import android.net.Uri
import java.security.MessageDigest

private val hexArray = "0123456789ABCDEF".toCharArray()

fun bytesToHex(bytes: ByteArray): String {
    val hexChars = CharArray(bytes.size * 2)
    for (j in bytes.indices) {
        val v = bytes[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

fun sha256Hex(ctx: Context, uri: Uri): String {
    ctx.contentResolver.openInputStream(uri).use { input ->
        val sha256 = MessageDigest.getInstance("SHA-256")
        val buf = ByteArray(4096)
        while (true) {
            val n = input.read(buf)
            if (n <= 0)
                break
            sha256.update(buf, 0, n)
        }
        return bytesToHex(sha256.digest())
    }
}

fun formatTime(millis: Int) = String.format("%d:%02d.%03d",
        millis / 60_000, millis / 1000 % 60, millis % 1000)
