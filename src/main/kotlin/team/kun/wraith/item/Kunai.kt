package team.kun.wraith.item

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import team.kun.wraith.ext.getMeta
import team.kun.wraith.ext.playSound
import team.kun.wraith.ext.random
import team.kun.wraith.ext.setMeta
import team.kun.wraith.ext.spawnParticle
import team.kun.wraith.metadata.MetadataKey
import team.kun.wraith.rx.Observable
import team.kun.wraith.util.EntityUtil
import java.util.*

class Kunai : Item(), Craftable {
    override val name = "クナイ"
    override val description = listOf(
            "右クリックで虚空を通り、安全かつ迅速に移動する。",
            "ダメージは一切受けない。"
    )
    override val itemStack = ItemStack(Material.STONE_SWORD)

    private val coolTime = 15

    override fun getRecipe(plugin: JavaPlugin): ShapedRecipe? {
        val key = getKey(plugin) ?: return null
        return ShapedRecipe(key, toItemStack(plugin)).apply {
            shape(" O ", " O ", " S ")
            setIngredient('O', Material.OBSIDIAN)
            setIngredient('S', Material.STICK)
        }
    }

    fun execute(player: Player, plugin: JavaPlugin) {
        val second = calcSecond(player)
        if (second > 0) {
            player.sendMessage("「虚空へ」再使用可能まであと${second}秒")
            return
        }
        player.setMeta(plugin, MetadataKey.IntoTheVoidCoolTime, System.currentTimeMillis())

        player.location.playSound(Sound.ENTITY_COW_AMBIENT, 1.0f, 1.8f)
        Observable.interval(5)
                .take(17)
                .doOnNext {
                    if (it == 5L) {
                        player.location.playSound(Sound.BLOCK_PORTAL_TRIGGER, 1.0f, 1.0f)
                        object : BukkitRunnable() {
                            override fun run() {
                                EntityUtil.hide(player, plugin)
                            }
                        }.runTaskLater(plugin, 1)
                    }
                    if (it >= 5L) {
                        repeat(10) {
                            player.location.random(1.3, 1.3, 1.3).add(0.0, 0.8, 0.0).spawnParticle(Particle.REDSTONE, 1, Particle.DustOptions(Color.fromRGB(150 + Random().nextInt(100), 0, 255), 1f))
                        }
                    }
                }
                .doOnComplete {
                    player.location.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                    object : BukkitRunnable() {
                        override fun run() {
                            EntityUtil.show(player, plugin)
                        }
                    }.runTaskLater(plugin, 1)
                }
                .subscribe(plugin)
    }

    private fun calcSecond(player: Player): Int {
        val currentCoolTime = player.getMeta(MetadataKey.IntoTheVoidCoolTime) ?: return 0
        return ((currentCoolTime.toInt() + coolTime * 1000) - System.currentTimeMillis().toInt()) / 1000
    }
}