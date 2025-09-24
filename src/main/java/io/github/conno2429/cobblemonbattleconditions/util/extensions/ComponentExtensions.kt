package io.github.conno2429.cobblemonbattleconditions.util.extensions

import com.cobblemon.mod.common.api.text.bold
import io.github.conno2429.cobblemonbattleconditions.battle.BattleSideData
import io.github.conno2429.cobblemonbattleconditions.client.BattleConditionsOverlay.isActor
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

fun MutableComponent.colorSort(sideData: BattleSideData): MutableComponent {
    if (isActor == true) {
        if (Minecraft.getInstance().player?.name?.string == sideData.playerName) {
            this.withRgb(0x227ace).bold()
        } else this.withRgb(0xbe2f2f).bold()
    } else {
        if (sideData.side == "1") this.withRgb(0x227ace).bold() else this.withRgb(0xbe2f2f).bold()
    }
    return this
}

fun MutableComponent.withRgb(rgb: Int): MutableComponent {
    return this.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)))
}