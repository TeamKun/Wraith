package team.kun.wraith

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.dependency.Dependency
import org.bukkit.plugin.java.annotation.dependency.DependsOn
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author
import team.kun.wraith.ext.registerListener
import team.kun.wraith.item.RecipeService
import team.kun.wraith.listener.ItemListener
import team.kun.wraith.listener.PlayerListener

@Plugin(name = "Wraith", version = "1.0-SNAPSHOT")
@Author("ReyADayer")
@DependsOn(
        Dependency("ProtocolLib"),
)
@ApiVersion(ApiVersion.Target.v1_15)
class Wraith : JavaPlugin() {
    override fun onEnable() {
        registerListener(ItemListener(this))
        registerListener(PlayerListener(this))

        RecipeService.add(this)
    }

    override fun onDisable() {
        RecipeService.remove(this)
    }
}