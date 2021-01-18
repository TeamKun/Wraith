package team.kun.wraith.item

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import team.kun.wraith.ext.getMeta
import team.kun.wraith.ext.playSound
import team.kun.wraith.ext.setMeta
import team.kun.wraith.ext.spawn
import team.kun.wraith.metadata.MetadataKey
import team.kun.wraith.rx.Observable

class Portal : Item(), Craftable {
    override val name = "ポータル"
    override val description = listOf(
            "2地点間をポータルで60秒間連結する。"
    )
    override val itemStack = ItemStack(Material.GOLDEN_SWORD)

    private val coolTime = 210
    private val warpCoolTime = 5

    override fun getRecipe(plugin: JavaPlugin): ShapedRecipe? {
        val key = getKey(plugin) ?: return null
        return ShapedRecipe(key, toItemStack(plugin)).apply {
            shape("OGO", "GOG", "OGO")
            setIngredient('O', Material.OBSIDIAN)
            setIngredient('G', Material.GOLD_INGOT)
        }
    }

    fun execute(player: Player, plugin: JavaPlugin) {
        val second = calcSecond(player)
        if (second > 0) {
            player.sendMessage("「ディメンションリフト」再使用可能まであと${second}秒")
            return
        }
        player.setMeta(plugin, MetadataKey.DimensionLiftCoolTime, System.currentTimeMillis())
        player.setMeta(plugin, MetadataKey.UsingDimensionLift, true)

        player.location.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.0f)
        val entranceLocation = player.location
        val locations = mutableListOf<Location>(entranceLocation)

        Observable.interval(4).take(100)
                .doOnNext {
                    val component = TextComponent("${ChatColor.LIGHT_PURPLE}エネルギー ${(100 - it.toInt())}")
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
                    locations.add(player.location)
                }
                .doOnErrorCondition { player.isDead }
                .doOnError {
                }
                .doOnCompleteCondition { locations.size >= 100 || !player.getMeta(MetadataKey.UsingDimensionLift, false) }
                .doOnComplete {
                    locations.add(player.location)
                    player.setMeta(plugin, MetadataKey.WarpCoolTime, System.currentTimeMillis())

                    object : BukkitRunnable() {
                        override fun run() {
                            val entrance = createPortal(entranceLocation, true, locations, plugin)
                            entrance.location.playSound(Sound.ENTITY_SHULKER_TELEPORT, 1.0f, 1.0f)
                            val exit = createPortal(player.location, false, locations, plugin)
                            exit.location.playSound(Sound.ENTITY_SHULKER_TELEPORT, 1.0f, 1.0f)

                            object : BukkitRunnable() {
                                override fun run() {
                                    entrance.remove()
                                    exit.remove()
                                }
                            }.runTaskLater(plugin, 1200)
                        }
                    }.runTaskLater(plugin, 1)
                }
                .subscribe(plugin)
    }

    fun warp(player: Player, portal: ArmorStand, plugin: JavaPlugin) {
        val second = calcWarpSecond(player)
        if (second > 0) {
            return
        }

        player.playSound(player.location, Sound.BLOCK_PORTAL_TRAVEL, 0.2f, 1.0f)
        var locations = portal.getMeta(MetadataKey.Locations) ?: return
        val isEntrance = portal.getMeta(MetadataKey.IsDimensionLiftEntrance) ?: return
        player.gameMode = GameMode.SPECTATOR
        if (!isEntrance) {
            locations = locations.reversed()
        }
        Observable.interval(1).take(locations.size.toLong() - 1)
                .doOnNext {
                    object : BukkitRunnable() {
                        override fun run() {
                            player.teleport(locations[it.toInt()])
                        }
                    }.runTaskLater(plugin, 1)
                }
                .doOnComplete {
                    player.setMeta(plugin, MetadataKey.WarpCoolTime, System.currentTimeMillis())
                    object : BukkitRunnable() {
                        override fun run() {
                            player.gameMode = GameMode.SURVIVAL
                        }
                    }.runTaskLater(plugin, 1)
                }
                .subscribe(plugin)
    }

    private fun createPortal(location: Location, isEntrance: Boolean, locations: List<Location>, plugin: JavaPlugin): ArmorStand {
        return location.spawn<ArmorStand> {
            it.equipment?.helmet = ItemStack(Material.OBSIDIAN)
            it.setGravity(false)
            it.isInvulnerable = true
            it.setMeta(plugin, MetadataKey.DimensionLift, true)
            it.setMeta(plugin, MetadataKey.Locations, locations)
            it.setMeta(plugin, MetadataKey.IsDimensionLiftEntrance, isEntrance)
        } as ArmorStand
    }

    private fun calcSecond(player: Player): Int {
        val currentCoolTime = player.getMeta(MetadataKey.DimensionLiftCoolTime) ?: return 0
        return ((currentCoolTime.toInt() + coolTime * 1000) - System.currentTimeMillis().toInt()) / 1000
    }

    private fun calcWarpSecond(player: Player): Int {
        val currentCoolTime = player.getMeta(MetadataKey.WarpCoolTime) ?: return 0
        return ((currentCoolTime.toInt() + warpCoolTime * 1000) - System.currentTimeMillis().toInt()) / 1000
    }
}