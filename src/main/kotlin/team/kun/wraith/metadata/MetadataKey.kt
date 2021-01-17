package team.kun.wraith.metadata

import org.bukkit.Location

sealed class MetadataKey<T>(val value: String) {
    object IsHidden : MetadataKey<Boolean>("IsHidden")
    object IsPlayerInteract : MetadataKey<Boolean>("IsPlayerInteract")

    object IntoTheVoidCoolTime : MetadataKey<Long>("IntoTheVoidCoolTime")
    object DimensionLiftCoolTime : MetadataKey<Long>("DimensionLiftCoolTime")
    object WarpCoolTime : MetadataKey<Long>("WarpCoolTime")

    object DimensionLift : MetadataKey<Boolean>("DimensionLift")
    object IsDimensionLiftEntrance : MetadataKey<Boolean>("IsDimensionLiftEntrance")
    object UsingDimensionLift : MetadataKey<Boolean>("UsingDimensionLift")


    object Locations : MetadataKey<List<Location>>("Locations")
}