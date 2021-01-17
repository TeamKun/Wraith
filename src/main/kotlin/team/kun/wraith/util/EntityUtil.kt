package team.kun.wraith.util

import team.kun.wraith.ext.removeMeta
import team.kun.wraith.ext.setMeta
import team.kun.wraith.metadata.MetadataKey
import team.kun.wraith.packet.EntityDestroyPacket
import team.kun.wraith.packet.UpdateEntityPacket
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object EntityUtil {
    private val hideEntities: MutableList<Entity> = mutableListOf()

    fun hide(hiddenEntity: Entity, plugin: JavaPlugin) {
        val packet = EntityDestroyPacket(hiddenEntity)
        val players = plugin.server.onlinePlayers.filterNot { it == hiddenEntity }
        packet.send(players)
        hideEntities.add(hiddenEntity)
        hiddenEntity.setMeta(plugin, MetadataKey.IsHidden, true)
    }

    // TODO : 動作しない
    fun syncHiddenPlayers(targetPlayer: Player) {
        hideEntities.forEach { hiddenEntity ->
            if (hiddenEntity != targetPlayer) {
                val packet = EntityDestroyPacket(hiddenEntity)
                packet.send(targetPlayer)
            }
        }
    }

    fun show(hiddenEntity: Entity, plugin: JavaPlugin) {
        val packet = UpdateEntityPacket(hiddenEntity)
        val players = plugin.server.onlinePlayers.filterNot { it == hiddenEntity }
        packet.send(players)
        hideEntities.remove(hiddenEntity)
        hiddenEntity.removeMeta(plugin, MetadataKey.IsHidden)
    }
}