package io.github.conno2429.cobblemonbattleconditions.util.extensions

import com.cobblemon.mod.common.api.moves.Moves
import java.util.UUID

fun String.toPossessive(): String =
    if (endsWith("s", ignoreCase = true)) "$this'" else "$this's"

fun String.getMoveName(): String {
    val template = Moves.getByName(this)
    return template?.create()?.displayName?.string ?: this
}

fun String.toUUIDFromPlainString(): UUID {
    return UUID.fromString(
        "${this.substring(0, 8)}-${this.substring(8, 12)}-${this.substring(12, 16)}-${
            this.substring(
                16,
                20
            )
        }-${this.substring(20)}"
    )
}