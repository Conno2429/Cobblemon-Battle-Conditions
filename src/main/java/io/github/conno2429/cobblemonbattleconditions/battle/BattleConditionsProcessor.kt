package io.github.conno2429.cobblemonbattleconditions.battle

import com.cobblemon.mod.common.api.battles.interpreter.BattleMessage
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.battles.BattleSide
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor
import com.cobblemon.mod.common.entity.npc.NPCBattleActor
import com.cobblemon.mod.common.util.getPlayer
import io.github.conno2429.cobblemonbattleconditions.network.packets.BattleConditionsPacket
import io.github.conno2429.cobblemonbattleconditions.util.extensions.getMoveName
import io.github.conno2429.cobblemonbattleconditions.util.extensions.toUUIDFromPlainString
import java.util.UUID

object BattleConditionsProcessor {
    val conditionsDataMap = mutableMapOf<UUID, ConditionsData>()

    @JvmStatic
    fun processAbility(battle: PokemonBattle, message: BattleMessage) {
        val conditionsData = conditionsDataMap[battle.battleId]

        val abilityName = message.effectAt(1)?.id ?: return

        var neutGasPresent = false
        battle.activePokemon.forEach { activePokemon ->
            val abilityName = activePokemon.battlePokemon?.originalPokemon?.ability?.name ?: return
            if (abilityName.isNeutGas()) neutGasPresent = true
        }

        if (conditionsData != null) {
            val originalDisabled = conditionsData.weatherTracker.disabled

            if (abilityName.canDisableWeather() && !neutGasPresent) {
                conditionsData.weatherTracker.disabled = true
            } else {
                conditionsData.weatherTracker.disabled = false
            }

            if (conditionsData.weatherTracker.disabled != originalDisabled) {
                battle.sendToPlayersAndSpectators()
            }
        }
    }

    @JvmStatic
    fun processWeatherDisabling(battle: PokemonBattle) {
        val conditionsData = conditionsDataMap[battle.battleId]
        var neutGasPresent = false
        var weatherDisablingAbility = false
        if (conditionsData != null) {
            battle.activePokemon.forEach { activePokemon ->
                val abilityName = activePokemon.battlePokemon?.originalPokemon?.ability?.name ?: return
                if (abilityName.isNeutGas()) neutGasPresent = true
                if (abilityName.canDisableWeather()) weatherDisablingAbility = true
            }

            val originalDisabled = conditionsData.weatherTracker.disabled

            if (weatherDisablingAbility && !neutGasPresent) {
                conditionsData.weatherTracker.disabled = true
            } else {
                conditionsData.weatherTracker.disabled = false
            }

            if (conditionsData.weatherTracker.disabled != originalDisabled) {
                battle.sendToPlayersAndSpectators()
            }
        }
    }

    @JvmStatic
    fun processFieldStart(battle: PokemonBattle, message: BattleMessage): String {
        val conditionsData = conditionsDataMap[battle.battleId]

        val effect = processFieldMessage(message)

        conditionsData?.fieldList?.removeIf { it.first.contains("Terrain") }
        conditionsData?.fieldList?.add(effect to 0) ?: return ""

        battle.sendToPlayersAndSpectators()

        return effect
    }

    @JvmStatic
    fun processFieldEnd(battle: PokemonBattle, message: BattleMessage) {
        val conditionsData = conditionsDataMap[battle.battleId]
        val effect = processFieldMessage(message)
        if (conditionsData != null) {
            conditionsData.fieldList.removeIf { it.first == effect }
            battle.sendToPlayersAndSpectators()
        }
    }

    @JvmStatic
    fun processSideStart(battle: PokemonBattle, message: BattleMessage) {
        val conditionsData = conditionsDataMap[battle.battleId]
        val sidePair = processSideMessage(message)

        if (sidePair.first == "" || sidePair.second == "") return

        val pSideRaw = sidePair.first
        val sideEffect = sidePair.second

        val pSide = pSideRaw.take(2).substring(1)
        val pUUID = pSideRaw.substring(2).toUUIDFromPlainString()
        var playerName = "Unknown"

        for (actor in battle.actors) {
            if (actor.uuid == pUUID) {
                playerName = actor.getTypeName()
                break
            }
        }

        if (conditionsData != null) {
            val existing = conditionsData.sideList.find { it.side == pSide && it.effect == sideEffect }
            if (existing != null) {
                when (sideEffect) {
                    "Spikes", "Toxic Spikes" -> {
                        existing.layers++
                    }
                }
            } else {
                val newData = BattleSideData(pSide, playerName, sideEffect, 0, 0)
                if (sideEffect == "Spikes" || sideEffect == "Toxic Spikes") {
                    newData.layers = 1
                }
                conditionsData.sideList.add(newData)
            }

            battle.sendToPlayersAndSpectators()
        }
    }

    @JvmStatic
    fun processSideEnd(battle: PokemonBattle, message: BattleMessage) {
        val conditionsData = conditionsDataMap[battle.battleId]
        val sidePair = processSideMessage(message)
        val pSide = sidePair.first.take(2).substring(1)
        val sideEffect = sidePair.second
        if (conditionsData != null) {
            for (sideData in conditionsData.sideList) {
                if (sideData.side == pSide && sideData.effect == sideEffect) {
                    conditionsData.sideList.remove(sideData)
                    battle.sendToPlayersAndSpectators()
                    break
                }
            }
        }
    }

    @JvmStatic
    fun processSwapSideConditions(battle: PokemonBattle) {
        val conditionsData = conditionsDataMap[battle.battleId] ?: return

        for (sideData in conditionsData.sideList) {
            when (sideData.side) {
                "1" -> {
                    sideData.side = "2"
                    sideData.playerName = getPlayerNameBySide(battle, battle.side2) ?: sideData.playerName
                }

                "2" -> {
                    sideData.side = "1"
                    sideData.playerName = getPlayerNameBySide(battle, battle.side1) ?: sideData.playerName
                }
            }
        }

        battle.sendToPlayersAndSpectators()
    }

    @JvmStatic
    fun updateCounters(battle: PokemonBattle) {
        val conditionsData = conditionsDataMap[battle.battleId]
        if (conditionsData != null) {
            if (conditionsData.weatherTracker.weather != "") {
                conditionsData.weatherTracker.turns++
            }

            for ((index, effectPair) in conditionsData.fieldList.withIndex()) {
                conditionsData.fieldList[index] = effectPair.first to (effectPair.second + 1)
            }

            for ((index, sideData) in conditionsData.sideList.withIndex()) {
                if (!(sideData.effect.contains("Spikes") || sideData.effect == "Sticky Web" || sideData.effect == "Stealth Rock")) {
                    conditionsData.sideList[index].turnCounter++
                }
            }

            var neutGasPresent = false
            var weatherDisablingAbility = false
            battle.activePokemon.forEach { activePokemon ->
                val abilityName = activePokemon.battlePokemon?.originalPokemon?.ability?.name ?: return
                if (abilityName.isNeutGas()) neutGasPresent = true
                if (abilityName.canDisableWeather()) weatherDisablingAbility = true
            }

            if (weatherDisablingAbility && !neutGasPresent) {
                conditionsData.weatherTracker.disabled = true
            } else {
                conditionsData.weatherTracker.disabled = false
            }

            battle.sendToPlayersAndSpectators()
        }
    }

    @JvmStatic
    fun processWeatherTurns(battle: PokemonBattle, message: BattleMessage) {
        val conditionsData = conditionsDataMap[battle.battleId]
        val weatherRaw = message.effectAt(0)?.id ?: return
        val weather = when (weatherRaw) {
            "raindance" -> "Rain"
            "sunnyday" -> "Sun"
            "sandstorm" -> "Sand"
            "hail" -> "Hail"
            "snow" -> "Snow"
            "deltastream" -> "Delta Stream"
            "desolateland" -> "Desolate Land"
            "primordialsea" -> "Primordial Sea"

            else -> weatherRaw.getMoveName()
        }

        val weatherTracker = conditionsData?.weatherTracker ?: return
        weatherTracker.upkeep = message.hasOptionalArgument("upkeep")

        if (weatherTracker.weather != weather) {
            weatherTracker.weather = weather
            weatherTracker.turns = 0
            battle.sendToPlayersAndSpectators()
        }
    }

    @JvmStatic
    fun processBattleEnd(battle: PokemonBattle) {
        conditionsDataMap.remove(battle.battleId)
    }

    fun processFieldMessage(message: BattleMessage): String =
        message.effectAt(0)?.id?.getMoveName() ?: ""

    fun processSideMessage(message: BattleMessage): Pair<String, String> {
        val pSideRaw = message.effectAt(0)?.id ?: return "" to ""
        val sideEffect = message.effectAt(1)?.id?.getMoveName() ?: return "" to ""

        return pSideRaw to sideEffect
    }

    fun String.canDisableWeather(): Boolean {
        return this == "airlock" || this == "cloudnine"
    }

    fun String.isNeutGas(): Boolean {
        return this == "neutralizinggas"
    }

    fun PokemonBattle.sendToPlayersAndSpectators() {
        val conditionsData = conditionsDataMap[this.battleId] ?: return
        this.sendToActors(BattleConditionsPacket(true, conditionsData.copy()))
        this.sendSpectatorUpdate(BattleConditionsPacket(false, conditionsData.copy()))
    }

    fun getPlayerNameBySide(battle: PokemonBattle, side: BattleSide): String? {
        for (actor in battle.actors) {
            if (side.actors.contains(actor)) {
                return actor.getTypeName()
            }
        }
        return null
    }

    fun BattleActor.getTypeName(): String {
        return when (this) {
            is PlayerBattleActor -> this.uuid.getPlayer()?.name?.string ?: "Unknown"
            is PokemonBattleActor -> this.pokemon.originalPokemon.species.name
            is TrainerBattleActor -> this.trainerName
            is NPCBattleActor -> this.npc.name.string ?: "Unknown"
            else -> "Unknown"
        }
    }
}