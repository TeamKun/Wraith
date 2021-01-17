package team.kun.wraith.metadata

sealed class MetadataKey<T>(val value: String) {
    object IsHidden : MetadataKey<Boolean>("IsHidden")
    object IsPlayerInteract : MetadataKey<Boolean>("IsPlayerInteract")

    object IntoTheVoidCoolTime : MetadataKey<Long>("IntoTheVoidCoolTime")
}