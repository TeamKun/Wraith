package team.kun.wraith.item

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

interface Craftable {
    fun getRecipe(plugin: JavaPlugin): ShapedRecipe?

    fun getKey(plugin: JavaPlugin): NamespacedKey? {
        return this::class.simpleName?.let {
            NamespacedKey(plugin, it)
        }
    }
}