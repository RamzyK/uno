package data.model

import data.model.card.Card

data class Player(
     var name: String,
     var cards: MutableList<Card>,
     var shouldPlay: Boolean
){

     override fun toString(): String {
          if(shouldPlay){
               return name + "\nCartes: " + cards + "\n" + "C'est votre tour de jouer\n"
          }else{
               return name + "\nCartes: " + cards + "\n" + "Ce n'est pas votre tour de jouer\n"
          }
     }
}