package data.model

import data.model.card.Card

data class Player(
     var name: String,
     var cards: MutableList<Card>,
     var shouldPlay: Boolean
)