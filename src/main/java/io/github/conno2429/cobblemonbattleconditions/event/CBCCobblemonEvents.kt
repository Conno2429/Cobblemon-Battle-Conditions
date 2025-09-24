package io.github.conno2429.cobblemonbattleconditions.event

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import io.github.conno2429.cobblemonbattleconditions.battle.BattleConditionsProcessor
import io.github.conno2429.cobblemonbattleconditions.battle.BattleConditionsProcessor.getTypeName
import io.github.conno2429.cobblemonbattleconditions.battle.BattleConditionsProcessor.sendToPlayersAndSpectators
import io.github.conno2429.cobblemonbattleconditions.battle.BattleSideData
import io.github.conno2429.cobblemonbattleconditions.battle.ConditionsData

object CBCCobblemonEvents {
    fun init() {
        CobblemonEvents.BATTLE_STARTED_POST.subscribe(Priority.NORMAL) {
            val conditionsData = ConditionsData()
            for (actor in it.battle.actors) {
                if (actor in it.battle.side1.actors) {
                    conditionsData.sideList.add(BattleSideData("1", actor.getTypeName(), "legend"))
                    println("Actor Side 1: ${actor.getTypeName()}")
                }
                if (actor in it.battle.side2.actors) {
                    conditionsData.sideList.add(BattleSideData("2", actor.getTypeName(), "legend"))
                    println("Actor Side 2: ${actor.getTypeName()}")
                }
            }

            BattleConditionsProcessor.conditionsDataMap[it.battle.battleId] = conditionsData
            it.battle.sendToPlayersAndSpectators()
        }
    }
}