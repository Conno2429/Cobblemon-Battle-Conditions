package io.github.conno2429.cobblemonbattleconditions.client

import com.mojang.blaze3d.platform.InputConstants
import io.github.conno2429.cobblemonbattleconditions.network.CBCNetwork
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object CobblemonBattleConditionsClient : ClientModInitializer {
    lateinit var TOGGLE_CONDITIONS_KEY: KeyMapping
    var toggleConditions: Boolean = true


    override fun onInitializeClient() {
        CBCNetwork.registerClientHandlers()

        TOGGLE_CONDITIONS_KEY =
            KeyBindingHelper.registerKeyBinding(
                KeyMapping(
                    "Toggle Battle Conditions",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    "Generations"
                )
                // TODO: replace name/category with lang i'm lazy
            )

        ClientTickEvents.END_CLIENT_TICK.register {
            onTick()
        }
    }

    fun onTick() {
        if (::TOGGLE_CONDITIONS_KEY.isInitialized && TOGGLE_CONDITIONS_KEY.consumeClick()) {
            toggleConditions = !toggleConditions
        }

    }
}