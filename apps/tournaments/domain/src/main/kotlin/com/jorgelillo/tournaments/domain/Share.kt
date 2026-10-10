package com.jorgelillo.tournaments.domain

import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * A whole tournament inside a link, so it moves to another phone with no account or server:
 * compact JSON, deflated, base64url. A 16-player bracket with comments is well under 1 KB,
 * small enough for a QR code.
 */
object Share {
    const val HOST = "jorgelillo7.github.io"
    const val PATH = "/t/"
    private const val VERSION = "1"

    /** Longer links are still shared as text, but no QR code: it would be too dense to scan off a screen. */
    const val QR_MAX_CHARS = 1_800

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    fun encode(t: Tournament): String {
        val bytes = json.encodeToString(Tournament.serializer(), t).toByteArray()
        val deflater = Deflater(Deflater.BEST_COMPRESSION, true).apply { setInput(bytes); finish() }
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) out.write(buffer, 0, deflater.deflate(buffer))
        deflater.end()
        return VERSION + Base64.getUrlEncoder().withoutPadding().encodeToString(out.toByteArray())
    }

    /** The tournament in [code], or null if it isn't one of ours (truncated, other version…). */
    fun decode(code: String): Tournament? = runCatching {
        require(code.startsWith(VERSION))
        val bytes = Base64.getUrlDecoder().decode(code.substring(VERSION.length))
        val inflater = Inflater(true).apply { setInput(bytes) }
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val n = inflater.inflate(buffer)
            if (n == 0 && (inflater.needsInput() || inflater.needsDictionary())) error("truncated")
            out.write(buffer, 0, n)
        }
        inflater.end()
        json.decodeFromString(Tournament.serializer(), out.toString(Charsets.UTF_8.name()))
    }.getOrNull()

    /** The fragment keeps the data out of server logs: GitHub Pages never sees it. */
    fun link(t: Tournament): String = "https://$HOST$PATH#${encode(t)}"

    fun codeFromLink(link: String): String? =
        link.substringAfter("$HOST$PATH", "").substringAfter('#', "").takeIf { it.isNotBlank() }
            ?: link.trim().takeIf { it.startsWith(VERSION) && ' ' !in it }
}
