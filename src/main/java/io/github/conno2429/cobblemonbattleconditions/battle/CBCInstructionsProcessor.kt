package io.github.conno2429.cobblemonbattleconditions.battle

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient


data class BattleSideData(
    var side: String = "",
    var UUID: String = "",
    var effect: String = "",
    var layers: Int = 0,
    var turnCounter: Int = 0
)

object CBCInstructionsProcessor {
    val fieldList = mutableListOf<Pair<String, Int>>()
    val sideList = mutableListOf<BattleSideData>()

    private var weatherTurnCounter = 0

    @JvmStatic
    fun runFieldEffectTurns() {
        for (effectPair in fieldList) {
            processUpkeep(effectPair.first)
        }
        for (sideData in sideList) {
            processUpkeepSide(sideData.effect)
        }
    }

    @JvmStatic
    fun processFieldStart(battle: PokemonBattle, message: BattleMessage): String {
        val effectRaw = message.effectAt(0)?.id ?: return ""
        println("Effect: $effectRaw")
        val effect = when {
            effectRaw.endsWith("terrain") -> {
                val name = effectRaw.removeSuffix("terrain").replaceFirstChar { it.uppercaseChar() }
                "$name Terrain"
            }

            effectRaw == "gravity" -> "Gravity"

            effectRaw.endsWith("room") -> {
                val name = effectRaw.removeSuffix("room").replaceFirstChar { it.uppercaseChar() }
                "$name Room"
            }

            else -> effectRaw
        }
        fieldList.add(effect to 0)
        return effect
    }

    @JvmStatic
    fun processFieldEnd(battle: PokemonBattle, message: BattleMessage) {
        var effect = processFieldStart(battle, message)
        fieldList.removeIf { it.first == effect }
    }

    @JvmStatic
    fun processSideStart(battle: PokemonBattle, message: BattleMessage): String {
        val pSideRaw = message.effectAt(0)?.id ?: return ""
        val pSide = pSideRaw.take(2).substring(1)
        val pSideIndex = pSide.toIntOrNull()?.minus(1) ?: return ""
        val rawEffect = message.effectAt(1)?.id ?: return ""
        val sideEffect = when (rawEffect) {
            "spikes" -> "Spikes"
            "stealthrock" -> "Stealth Rock"
            "stickyweb" -> "Sticky Web"
            "toxicspikes" -> "Toxic Spikes"


            "auroraveil" -> "Aurora Veil"
            "lightscreen" -> "Light Screen"
            "reflect" -> "Reflect"

            "luckychant" -> "Lucky Chant"
            "safeguard" -> "Safeguard"
            "tailwind" -> "Tailwind"

            else -> rawEffect
        }
        val pUUID = pSideRaw.substring(2)
        sideList.add(BattleSideData(pSide, pUUID, sideEffect, 0, 0))

        for (sideData in sideList) {
            when (sideData.effect) {
                "Spikes" -> {
                    sideData.layers++
                }
                "Toxic Spikes" -> {
                    sideData.layers++
                }
            }
        }

        return sideEffect
    }

    @JvmStatic
    fun processSideEnd(battle: PokemonBattle, message: BattleMessage) {
        var sideEffect = processFieldStart(battle, message)
        fieldList.removeIf { it.first == sideEffect }
    }

    @JvmStatic
    fun processUpkeep(effect: String) {
        for ((index, effectPair) in fieldList.withIndex()) {
            if (effect == effectPair.first) {
                displayTurns(effect, effectPair.second)
                fieldList[index] = effect to (effectPair.second + 1)
            }
        }
    }

    @JvmStatic
    fun processUpkeepSide(sideEffect: String) {
        for ((index, sideData) in sideList.withIndex()) {
            if (sideData.effect.contains("Spikes") || sideData.effect == "Sticky Web" || sideData.effect == "Stealth Rock") {
                displayHazards(sideData)
            } else {
                displayTurnsSide(sideData)
                sideList[index].turnCounter++
            }
        }
    }

    @JvmStatic
    fun processTurns() {
        for (effectPair in fieldList) {
            processUpkeep(effectPair.first)
        }
        for (sideData in sideList) {
            processUpkeepSide(sideData.effect)
        }
    }

    @JvmStatic
    fun processWeatherTurns(battle: PokemonBattle, message: BattleMessage) {
        val weatherRaw = message.effectAt(0)?.id ?: return
        val weather = when (weatherRaw) {
            "raindance" -> "Rain"
            "sunnyday" -> "Sun"
            "sandstorm" -> "Sand"
            "hail" -> "Hail"
            "snowscape" -> "Snow"

            else -> weatherRaw
        }
        if (message.hasOptionalArgument("upkeep") && weatherTurnCounter > 0) {
            displayWeather(weather, message, weatherTurnCounter)
            weatherTurnCounter++
        } else {
            weatherTurnCounter = 0
            displayWeather(weather, message, weatherTurnCounter)
            weatherTurnCounter++
        }
    }

    @JvmStatic
    fun processBattleEnd() {
        weatherTurnCounter = 0
    }

    private fun displayTurns(condition: String, turnCounter: Int) {
        if (condition == "Unknown" || condition == "") {
            println("No Field Conditions")
        } else if (condition.contains("Terrain")) {
            if (turnCounter < 5) {
                println("$condition turns remaining: ${8 - turnCounter} or ${5 - turnCounter}")
            } else {
                println("$condition turns remaining: ${8 - turnCounter}")
            }
        } else if (condition.contains("Room") || condition == "Gravity") {
            println("$condition turns remaining: ${5 - turnCounter}")
        }

    }

    private fun displayTurnsSide(sideData: BattleSideData) {
        val turnCounter = sideData.turnCounter
        val side = when (sideData.side) {
            "1", "2" -> if (matchUUID(sideData.UUID)) "Ally" else "Opponent"
            else -> "Unknown Side"
        }
        if (sideData.effect == "Reflect" || sideData.effect == "Light Screen" || sideData.effect == "Aurora Veil") {
            if (turnCounter < 5) {
                println("${sideData.effect} ($side) turns remaining: ${8 - turnCounter} or ${5 - turnCounter}")
            } else {
                println("${sideData.effect} ($side) turns remaining: ${8 - turnCounter}")
            }
        } else if (sideData.effect == "Tailwind") {
            println("${sideData.effect} ($side) turns remaining: ${4 - turnCounter}")
        } else {
            println("${sideData.effect} ($side) turns remaining: ${5 - turnCounter}")
        }
    }

    private fun displayHazards(sideData: BattleSideData) {
        val side = when (sideData.side) {
            "1", "2" -> if (matchUUID(sideData.UUID)) "Ally's Side" else "Opponent's Side"
            else -> "Unknown Side"
        }
        val hazardMessage = if (sideData.effect.contains("Spikes")) {
            "${sideData.effect} ($side) (${sideData.layers} layers)"
        } else {
            "${sideData.effect} ($side)"
        }
        println(hazardMessage)
    }

    private fun displayWeather(weather: String, message: BattleMessage, turnCounter: Int) {
        if (message.hasOptionalArgument("upkeep") && turnCounter < 5) {
            println("$weather turns remaining: ${8 - turnCounter} or ${5 - turnCounter}")

        } else if (message.hasOptionalArgument("upkeep") && turnCounter >= 5) {
            println("$weather turns remaining: ${8 - turnCounter}")

        } else {
            println("$weather turns remaining: ${8 - turnCounter} or ${5 - turnCounter}")
        }
    }

    private fun matchUUID(pUUID: String): Boolean {
        val clientPlayer = MinecraftClient.getInstance().player
        val clientUUID = clientPlayer?.uuid?.toString()?.replace("-","")
//        println("Pokemon UUID: $pUUID")
//        println("Client UUID: $clientUUID")
        return pUUID == clientUUID
    }

}