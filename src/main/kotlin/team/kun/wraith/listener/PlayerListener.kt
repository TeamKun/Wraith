package team.kun.wraith.listener

import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import team.kun.wraith.ext.getMeta
import team.kun.wraith.item.Portal
import team.kun.wraith.metadata.MetadataKey
import team.kun.wraith.util.EntityUtil

class PlayerListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        EntityUtil.syncHiddenPlayers(player)
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        if (attacker.getMeta(MetadataKey.IsHidden, false)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onclick(event: PlayerInteractAtEntityEvent) {
        val entity = event.rightClicked
        if (entity.getMeta(MetadataKey.DimensionLift, false)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (!player.getMeta(MetadataKey.Warping, false)) {
            player.location.world?.getNearbyEntities(player.location, 0.3, 0.3, 0.3)
                    ?.filterIsInstance<ArmorStand>()
                    ?.forEach {
                        if (it.getMeta(MetadataKey.DimensionLift, false)) {
                            Portal().warp(player, it, plugin)
                            return
                        }
                    }
        }
    }
}