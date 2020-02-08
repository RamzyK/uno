package data.model

import data.model.card.Card

data class Player(
     var name: String,
     var cards: MutableList<Card>,
     var shouldPlay: Boolean,
     var hasPickedUp: Boolean
){


     override fun toString(): String {
        return name + "\nCartes: " + getCardsArrayToString() + "\n" + if(shouldPlay) "C'est votre tour de jouer\n" else "Ce n'est pas votre tour de jouer\n"
     }

    private fun getCardsArrayToString(): String{
        var stringResult = ""
         for (card in cards){
            stringResult += "$card | "
        }
        return stringResult
    }
}