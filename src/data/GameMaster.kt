package data

import data.model.Player
import data.model.card.Card
import data.model.card.CardColor
import data.model.card.CardType
import data.model.game.Game

object GameMaster{

    var playerAccount = 4
    var gameDeck : MutableList<Card> = mutableListOf()
    var playedCards : MutableList<Card> = mutableListOf()
    var playingDirection: GameDirection = GameDirection.RIGHT
    lateinit var currentPlayer: Player
    lateinit var currentColor: CardColor

    fun initGame(players: Int) = prepareGame(players)

    private fun prepareGame(playersNumber: Int) : Game{
        playerAccount = playersNumber
        val players: MutableList<Player> = mutableListOf()

        for(i in 0 until playerAccount){
            if(i == 0){
                players.add(Player(name = "Player$i", cards = mutableListOf(), shouldPlay = true))
            }else{
                players.add(Player(name = "Player$i", cards = mutableListOf(), shouldPlay = false))
            }

        }

        currentPlayer = players[0]
        gameDeck = generateDeck().toMutableList()

        for(i in 0 until 7 * playerAccount) {
            players[i% playerAccount].cards.add(gameDeck[0])
            gameDeck.removeAt(0)
        }

        return Game(players = players, gameDirection = playingDirection)
    }

    private fun generateDeck() : List<Card>{
        var cards : MutableList<Card> = mutableListOf()

        //cards.addAll(generateCardsFor(CardColor.BLUE))
        //cards.addAll(generateCardsFor(CardColor.RED))
        //cards.addAll(generateCardsFor(CardColor.GREEN))
        //cards.addAll(generateCardsFor(CardColor.YELLOW))

        cards.addAll(generatePowerCards())

        return cards.shuffled()
    }

    private fun generateCardsFor(color: CardColor):MutableList<Card>{
        val cardsForColor : MutableList<Card> = mutableListOf()

        cardsForColor.add(Card(cardNumber = 0, cardColor = color, cardType = CardType.NORMAL))

        for (i in 1..9){
            cardsForColor.add(Card(cardNumber = i, cardColor = color, cardType = CardType.NORMAL))
            cardsForColor.add(Card(cardNumber = i, cardColor = color, cardType = CardType.NORMAL))
        }
        return cardsForColor
    }

    private fun generatePowerCards() : MutableList<Card>{
        val powerCards: MutableList<Card> = mutableListOf()
        val colors = CardColor.values()

        for(i in 0 until 4){
            // Add 2  +2
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.PLUS_2))
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.PLUS_2))
            // Add 2  change sens
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.CHANGE_SENS))
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.CHANGE_SENS))
            // Add 2  block next
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.BLOCK_NEXT))
            powerCards.add(Card(cardNumber = -1, cardColor = colors[i], cardType = CardType.BLOCK_NEXT))
            // Add 1  Joker
            powerCards.add(Card(cardNumber = -1, cardColor = CardColor.POWER, cardType = CardType.JOKER))
            // Add 1  +4
            powerCards.add(Card(cardNumber = -1, cardColor = CardColor.POWER, cardType = CardType.PLUS_4))
        }

        return powerCards
    }

}

enum class GameDirection(){
    LEFT,
    RIGHT
}