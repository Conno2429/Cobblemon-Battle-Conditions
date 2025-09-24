package io.github.conno2429.cobblemonbattleconditions.network

import com.cobblemon.mod.common.api.net.NetworkPacket
import com.cobblemon.mod.common.net.PacketRegisterInfo
import com.cobblemon.mod.fabric.net.FabricPacketInfo
import io.github.conno2429.cobblemonbattleconditions.network.packets.BattleConditionsPacket
import io.github.conno2429.cobblemonbattleconditions.network.packets.BattleConditionsPacketHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer

object CBCNetwork {
    val s2cPayloads: List<PacketRegisterInfo<*>> = listOf(
        PacketRegisterInfo(BattleConditionsPacket.ID, BattleConditionsPacket::decode, BattleConditionsPacketHandler)
    )
    val c2sPayloads: List<PacketRegisterInfo<*>> = listOf(
        // PacketRegisterInfo(C2SPacket.ID, C2SPacket::decode, C2SHandler)
    )

    fun registerMessages() {
        s2cPayloads.map { FabricPacketInfo(it) }.forEach { it.registerPacket(true) }
        c2sPayloads.map { FabricPacketInfo(it) }.forEach { it.registerPacket(false) }
    }

    fun registerClientHandlers() {
        s2cPayloads.map { FabricPacketInfo(it) }.forEach { it.registerClientHandler() }
    }

    fun registerServerHandlers() {
        c2sPayloads.map { FabricPacketInfo(it) }.forEach { it.registerServerHandler() }
    }

    fun sendTo(player: ServerPlayer, packet: NetworkPacket<*>) =
        ServerPlayNetworking.send(player, packet)

    fun sendToServer(packet: NetworkPacket<*>) =
        ClientPlayNetworking.send(packet)

}