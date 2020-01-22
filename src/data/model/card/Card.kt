package data.model.card

data class Card(
    val cardNumber: Int,
    val cardColor: CardColor,
    val cardType: CardType
){
    override fun toString(): String {
        if(cardNumber >= 0){
            return cardNumber.toString() + " " + cardColor.colorName + " | "
        }else{
            return cardType.typeName + " " + cardColor.colorName + " | "
        }
    }

}