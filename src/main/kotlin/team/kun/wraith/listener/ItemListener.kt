package team.kun.wraith.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import team.kun.wraith.item.Kunai
import team.kun.wraith.metadata.PlayerFlagMetadata

class ItemListener(private val plugin: JavaPlugin) : Listener {
    private val playerFlagMetadata = PlayerFlagMetadata(plugin)

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        val player = event.player
        if (playerFlagMetadata.getFlag(player)) {
            return
        }
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (Kunai().equal(event.player.inventory.itemInMainHand, plugin)) {
                playerFlagMetadata.avoidTwice(player)
                Kunai().execute(player, plugin)
                event.isCancelled = true
            }
        }
    }
}