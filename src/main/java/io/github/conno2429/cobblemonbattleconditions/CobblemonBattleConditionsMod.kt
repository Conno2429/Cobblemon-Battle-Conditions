package io.github.conno2429.cobblemonbattleconditions

import io.github.conno2429.cobblemonbattleconditions.event.CBCCobblemonEvents
import io.github.conno2429.cobblemonbattleconditions.network.CBCNetwork.registerMessages
import io.github.conno2429.cobblemonbattleconditions.network.CBCNetwork.registerServerHandlers
import net.fabricmc.api.ModInitializer

class CobblemonBattleConditionsMod : ModInitializer {
    override fun onInitialize() {
        registerMessages()
        registerServerHandlers()

        CBCCobblemonEvents.init()
    }
}
