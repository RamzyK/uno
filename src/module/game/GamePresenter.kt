package module.game

import data.GameMaster
import data.model.card.Card
import data.model.card.CardColor
import data.model.card.CardType
import data.model.game.Game

class GamePresenter(var view: GameView) {

    private lateinit var game: Game

    fun initGame() {
        view.showWelcomeMessage()
        view.askForPlayersNumber()
    }

    fun manageResponse(response: String) {
        if (response.isNotEmpty()) {
            when (response.toIntOrNull()) {
                null -> {
                    view.showMessage("Il faut entrer un nombre... pas autre chose.\n\n")
                    view.askForPlayersNumber()
                }
                else -> {
                    if (response.toInt() > 1) {
                        view.initializingPlayersMessage(response)
                        initPlayers(playerNumber = response.toInt())
                    } else {
                        view.showMessage("Il faut au moins 2 joueurs\n\n")
                        view.askForPlayersNumber()
                    }
                }
            }
        } else {
            view.showMessage("Vous devez nous dire combien de joueur il y a avant de pouvoir commencer la partie \n\n")
            view.askForPlayersNumber()
        }
    }

    private fun initPlayers(playerNumber: Int) {
        with(GameMaster) {
            game = initGame(playerNumber)
            playedCards.add(gameDeck.first())
            currentColor = gameDeck.first().cardColor
            if (gameDeck.first().cardType == CardType.NORMAL) {
                currentPlayer = game.players.first()
            } else {
                firstCardIsPowerAction(gameDeck.first())
            }
            gameDeck.removeAt(0)
        }
        view.showPlayers(game)
        view.putFirstCardFromDeck()
    }

    fun manageTurns() {
        view.showPlayedCard()
        view.showCurrentPlayer()
        playTheCard()
    }

    private fun playTheCard() {
        view.showMessage("Entre le numéro correspondant à la carte que tu veux jouer : ")
        readLine()?.let {
            if (it.isNotEmpty()) {
                if (it == "p") drew() else checkPlayerCardChoice(it)
            } else {
                view.showMessage(
                    "Il faut choisir une carte pour passer le tour\n\n\nQuelle carte veux-tu jouer (position entre 1 & "
                            + GameMaster.currentPlayer.cards.size
                            + ")? \n"
                )
                playTheCard()
            }
        }
    }

    private fun isPossibleToPlay(card: Card) {
        when (card.cardType) {
            CardType.BLOCK_NEXT -> blockNextPlayer()
            CardType.CHANGE_SENS -> changeGameDirection(card)
            CardType.PLUS_4 -> addPlus4Card(card)
            CardType.PLUS_2 -> addPlus2Card(card)
            CardType.JOKER -> addJokerCard(card)
            CardType.NORMAL -> normalCardPlayed(card)
        }
        if (GameMaster.currentPlayer.cards.size == 1) {
            view.shoutUno()
        }
        manageTurns()
    }

    private fun firstCardIsPowerAction(card: Card) {
        when (card.cardType) {
            CardType.BLOCK_NEXT -> {
                GameMaster.currentPlayer = game.players[1]
            }
            CardType.CHANGE_SENS -> {
                game.players.reverse()
            }
            CardType.PLUS_4 -> {
                for (i in 0 until 4) {
                    GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
                    GameMaster.gameDeck.removeAt(0)
                }
            }
            CardType.PLUS_2 -> {
                for (i in 0 until 2) {
                    GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
                    GameMaster.gameDeck.removeAt(0)
                }
            }
            CardType.JOKER -> addJokerCard(card)
            CardType.NORMAL -> return
        }
    }

    private fun goToNextPlayer() {
        val currentPlayerPos = game.players.indexOf(GameMaster.currentPlayer)
        val newCurrentPlayerPos = (currentPlayerPos + 1) % (game.players.size)
        GameMaster.currentPlayer = game.players[newCurrentPlayerPos]
    }

    // region * * * * * * * * * * * * * * * SPECIAL CARDS ACTION SECTION * * * * * * * * * * * * * * *
    private fun normalCardPlayed(card: Card) {
        val lastCardPlayed = GameMaster.playedCards[0]
        val lastPlayerPosition: Int = game.players.indexOf(GameMaster.currentPlayer)

        if (card.cardNumber == lastCardPlayed.cardNumber || card.cardColor == lastCardPlayed.cardColor) {
            GameMaster.playedCards.add(0, card)
            GameMaster.currentPlayer.cards.remove(card)
            GameMaster.currentPlayer = game.players[(lastPlayerPosition + 1) % (game.players.size)]
            manageTurns()
        } else {
            view.showErrorCardChosen()
            playTheCard()
        }
    }

    private fun addPlus2Card(card: Card) {
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        val lastCardPlayed = GameMaster.playedCards[0]
        if (lastCardPlayed.cardColor == card.cardColor || lastCardPlayed.cardType == card.cardType) {
            for (c in 0 until 2) {
                game.players[(currentPlayerIndex + 1) % (game.players.size)]
                    .cards.add(GameMaster.gameDeck[0])
                GameMaster.gameDeck.removeAt(0)
            }
            GameMaster.playedCards.add(0, card)
            GameMaster.currentPlayer.cards.remove(card)
            GameMaster.currentPlayer = game.players[(currentPlayerIndex + 2) % (game.players.size)]
        } else {
            view.showErrorCardChosen()
            playTheCard()
        }
    }

    private fun addPlus4Card(card: Card) {
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        for (c in 0 until 4) {
            game.players[(currentPlayerIndex + 1) % (game.players.size)]
                .cards.add(GameMaster.gameDeck[0])
            GameMaster.gameDeck.removeAt(0)
        }
        addJokerCard(card)
    }

    private fun changeGameDirection(card: Card) {
        if (GameMaster.playedCards[0].cardType == CardType.CHANGE_SENS) {
            reversePlayers(card)
        } else {
            if (GameMaster.playedCards[0].cardColor == card.cardColor) {
                reversePlayers(card)
            } else {
                view.showErrorCardChosen()
                playTheCard()
            }
        }

    }

    private fun reversePlayers(card: Card) {
        GameMaster.currentPlayer.cards.remove(card)
        game.players.reverse()
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        val nextPlayerPosition = ((currentPlayerIndex + 1) % (game.players.size))
        GameMaster.playedCards.add(0, card)
        GameMaster.currentPlayer = game.players[nextPlayerPosition]
    }

    private fun blockNextPlayer() {
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        GameMaster.currentPlayer = game.players[(currentPlayerIndex + 2) % (game.players.size)]
    }

    private fun addJokerCard(card: Card) {
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        view.showMessage("Quelle couleur veux-tu choisir ?")
        for ((count, color) in CardColor.values().withIndex()) {
            view.showMessage("${count+1} " + color.colorName)
        }
        readLine()?.let {
            if (it.isNotEmpty()) {
                when (it.toIntOrNull()) {
                    null -> {
                        view.showMessage("Mauvaise position, rééssaye\n\n")
                        isPossibleToPlay(card)
                    }
                    else -> {
                        GameMaster.currentColor = CardColor.values()[it.toInt() - 1]
                        view.showMessage("Couleur du jeu: " + GameMaster.currentColor.colorName)
                        val nextPlayerPosition = (currentPlayerIndex + 2) % (game.players.size)
                        GameMaster.currentPlayer = game.players[nextPlayerPosition]
                        manageTurns()
                    }
                }
            } else {
                view.showMessage("Il faut choisir une carte pour passer le tour\n\n\nQuelle carte veux-tu jouer (position entre 1 & "
                            + GameMaster.currentPlayer.cards.size
                            + ")? \n"
                )
                isPossibleToPlay(card)
            }
        }

    }
    //endregion


    // region * * * * * * * * * * * * * * * GAME ACTIONS * * * * * * * * * * * * * * *
    private fun drew() {
        if (GameMaster.currentPlayer.hasPickedUp) {
            GameMaster.currentPlayer.hasPickedUp = false
            goToNextPlayer()
            manageTurns()
        } else {
            if (GameMaster.gameDeck.size == 0) {
                view.showMessage("Impossible de piocher il n'y a plus de carte")
                manageTurns()
                return
            }
            GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
            GameMaster.gameDeck.removeAt(0)
            GameMaster.currentPlayer.hasPickedUp = true
            view.showMessage("${GameMaster.currentPlayer.name} a pioché\n")
            manageTurns()
        }
    }

    private fun checkPlayerCardChoice(it: String) {
        when (it.toIntOrNull()) {
            null -> {
                view.showMessage("Mauvaise réponse bonhomme, retente ta chance !\n\n")
                playTheCard()
            }
            else -> {
                val currentPlayerCardsSize = GameMaster.currentPlayer.cards.size
                val pos = it.toInt() - 1
                if (currentPlayerCardsSize >= 2 && pos >= 0 && pos < currentPlayerCardsSize) {
                    isPossibleToPlay(GameMaster.currentPlayer.cards[pos])
                } else {
                    view.showMessage("Aucune carte à cette position, rééssayez\n")
                    playTheCard()
                }
            }
        }
    }
    //endregion

}