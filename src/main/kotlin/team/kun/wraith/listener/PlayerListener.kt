package team.kun.wraith.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import team.kun.wraith.ext.getMeta
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
}