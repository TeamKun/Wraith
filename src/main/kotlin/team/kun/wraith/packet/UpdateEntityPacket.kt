package team.kun.wraith.packet

import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class UpdateEntityPacket(private val entity: Entity) : PacketClient() {
    override fun send(player: Player) {
        protocolManager.updateEntity(entity, listOf(player))
    }

    override fun send(players: List<Player>) {
        protocolManager.updateEntity(entity, players)
    }
}