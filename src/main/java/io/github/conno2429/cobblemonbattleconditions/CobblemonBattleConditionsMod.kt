package io.github.conno2429.cobblemonbattleconditions

import io.github.conno2429.cobblemonbattleconditions.event.CBCCobblemonEvents
import io.github.conno2429.cobblemonbattleconditions.network.CBCNetwork.registerMessages
import io.github.conno2429.cobblemonbattleconditions.network.CBCNetwork.registerServerHandlers
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader

object CobblemonBattleConditionsMod : ModInitializer {
    private val active = !FabricLoader.getInstance().isModLoaded("generations_core")

    override fun onInitialize() {
        if (!active) {
            println("[CBC] Generations present — skipping standalone initialization")
            return
        }

        registerMessages()
        registerServerHandlers()

        CBCCobblemonEvents.init()
    }

    fun isActive(): Boolean = active
}
