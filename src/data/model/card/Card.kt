package data.model.card

data class Card(
    val cardNumber: Int,
    val cardColor: CardColor,
    val cardType: CardType
) {
    override fun toString(): String {
        return if (cardNumber >= 0) {
            cardNumber.toString() + " " + cardColor.colorName
        } else {
            cardType.typeName + " " + cardColor.colorName
        }
    }

}