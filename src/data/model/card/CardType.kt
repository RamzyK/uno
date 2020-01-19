package data.model.card

enum class CardType(val typeName: String) {
    PLUS_4(typeName = "+4"),
    PLUS_2(typeName = "+2"),
    CHANGE_SENS(typeName = "Change sens"),
    BLOCK_NEXT(typeName = "Block"),
    JOKER(typeName = "Couleur"),
    NORMAL(typeName = "Normal")
}