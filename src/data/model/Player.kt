package data.model

import data.model.card.Card

data class Player(
     var name: String,
     var cards: MutableList<Card>,
     var shouldPlay: Boolean
){


     override fun toString(): String {
          if(shouldPlay){
               return name + "\nCartes: " + getCardsArrayToString() + "\n" + "C'est votre tour de jouer\n"
          }else{
               return name + "\nCartes: " + getCardsArrayToString() + "\n" + "Ce n'est pas votre tour de jouer\n"
          }
     }

    private fun getCardsArrayToString(): String{
        var stringResult = ""
         for (card in cards){
            stringResult += "$card | "
        }
        return stringResult
    }
}