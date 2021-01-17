package team.kun.wraith.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class EntityDestroyPacket(private val entity: Entity) : PacketClient() {
    override fun send(player: Player) {
        val packetContainer = getPacketContainer()
        sendPacket(player, packetContainer)
    }

    private fun getPacketContainer(): PacketContainer {
        val packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, intArrayOf(entity.entityId))
        return packet
    }
}