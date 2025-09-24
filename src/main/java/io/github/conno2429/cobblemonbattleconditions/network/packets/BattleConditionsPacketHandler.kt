package io.github.conno2429.cobblemonbattleconditions.network.packets

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import io.github.conno2429.cobblemonbattleconditions.client.BattleConditionsOverlay
import net.minecraft.client.Minecraft

object BattleConditionsPacketHandler: ClientNetworkPacketHandler<BattleConditionsPacket> {
    override fun handle(packet: BattleConditionsPacket, client: Minecraft) {
        client.execute {
            BattleConditionsOverlay.onBattleDataReceived(packet.isActor, packet.conditionsData)
        }
    }
}