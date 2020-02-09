package module.game

import data.GameMaster
import data.model.card.Card
import data.model.card.CardColor
import data.model.card.CardType
import data.model.game.Game

class GamePresenter(var view: GameView){

    private lateinit var game: Game

    fun initGame() {
        view.showWelcomeMessage()
        view.askForPlayersNumber()
    }

    fun manageResponse(response: String){
        if(response.isNotEmpty()){
            when(response.toIntOrNull()) {
                null ->{
                    view.showWtfResponseMessage("Il faut entrer in nombre... pas autre chose.\n\n")
                    view.askForPlayersNumber()
                }
                else -> {
                    if(response.toInt() > 1){
                        view.initializingPlayersMessage(response)
                        initPlayers(playerNumber = response.toInt())
                    }else{
                        view.showWtfResponseMessage("Il faut au moins 2 joueurs\n\n")
                        view.askForPlayersNumber()
                    }
                }
            }
        }else{
            view.showWtfResponseMessage("Vous devez nous dire combien de jour il y'a avant de pouvoir commecner la partie \n\n")
            view.askForPlayersNumber()
        }
    }

    fun initPlayers(playerNumber: Int){
        with(GameMaster){
            game = initGame(playerNumber)
            playedCards.add(gameDeck.first())
            currentColor = gameDeck.first().cardColor
            if(gameDeck.first().cardType == CardType.NORMAL){
                currentPlayer = game.players.first()
            }else{
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

    fun playTheCard(){
        readLine()?.let {
            if(it.isNotEmpty()){
                if(it == "p"){
                    pioche()
                }else{
                   checkPlayerCardChoice(it)
                }
            }else{
                view.showWtfResponseMessage("Il faut choisir une carte pour passer le tour\n")
                print("\n\nQuelle carte veux-tu jouer (position entre 1 & "
                        + GameMaster.currentPlayer.cards.size
                        +")? \n")
                playTheCard()
            }
        }
    }

    private fun checkCardIsPossibleToPlay(card: Card){
        // Last card played is a power card
        when(card.cardType){
            CardType.BLOCK_NEXT -> blockNextPlayer(card)
            CardType.CHANGE_SENS -> changeGameDirection(card)
            CardType.PLUS_4 -> addPlus4Card(card)
            CardType.PLUS_2 -> addPlus2Card()
            CardType.JOKER -> addJokerCard(card)
            CardType.NORMAL -> normalCardPlayed(card)
        }
        if(GameMaster.currentPlayer.cards.size == 1){
            view.shoutUno()
        }
        manageTurns()
    }

    private fun firstCardIsPowerAction(card: Card){
        when(card.cardType){
            CardType.BLOCK_NEXT -> {GameMaster.currentPlayer = game.players[1]}
            CardType.CHANGE_SENS -> {game.players.reverse()}
            CardType.PLUS_4 -> {
                for(i in 0 until 4){
                    GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
                    GameMaster.gameDeck.removeAt(0)
                }
            }
            CardType.PLUS_2 -> {
                for(i in 0 until 4){
                    GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
                    GameMaster.gameDeck.removeAt(0)
                }
            }
            CardType.JOKER -> print("Choisir une couleur NOT DONE")
        }
    }

    private fun goToNextPlayer(){
        val currentPlayerPos = game.players.indexOf(GameMaster.currentPlayer)
        val newCurrentPlayerPos = (currentPlayerPos + 1)%(game.players.size)
        GameMaster.currentPlayer = game.players.get(newCurrentPlayerPos)
    }

    // region * * * * * * * * * * * * * * * SPECIAL CARDS ACTION SECTION * * * * * * * * * * * * * * *
    private fun normalCardPlayed(card: Card){
        val lastCardPlayed = GameMaster.playedCards[0]
        val lastPlayerPosition: Int = game.players.indexOf(GameMaster.currentPlayer)

        // Last card played is a normal card
        if(card.cardNumber == lastCardPlayed.cardNumber || card.cardColor == lastCardPlayed.cardColor){
            // Same number, same or different color

            GameMaster.playedCards.add(0, card)
            GameMaster.currentPlayer.cards.remove(card)
            GameMaster.currentPlayer = game.players[(lastPlayerPosition+1) % (game.players.size)]
            manageTurns()
        }else{
            // Card had different number or different color
            view.shosErroCardChosen()
            playTheCard()
        }
    }

    private fun addPlus2Card(){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        for(c in 0 until 2){
            game.players
                .get((currentPlayerIndex + 1) % (game.players.size))
                .cards.add(GameMaster.gameDeck[0])
            GameMaster.gameDeck.removeAt(0)
        }
        val nextPlayerPosition = (currentPlayerIndex + 2) % (game.players.size)
        GameMaster.currentPlayer = game.players[nextPlayerPosition]
    }

    private fun addPlus4Card(card: Card){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        for(c in 0 until 4) {
            game.players
                .get((currentPlayerIndex + 1) % (game.players.size))
                .cards.add(GameMaster.gameDeck[0])
            GameMaster.gameDeck.removeAt(0)
        }
        addJokerCard(card)
    }

    private fun changeGameDirection(card: Card){
        if(GameMaster.playedCards[0].cardType == CardType.CHANGE_SENS){
            // Last card was not a normal card
            reversePlayers(card)
        }else{
            // Last card was a normal card
            if(GameMaster.playedCards[0].cardColor == card.cardColor){
                reversePlayers(card)
            }else{
                view.shosErroCardChosen()
                playTheCard()
            }
        }

    }

    private fun reversePlayers(card: Card){
        GameMaster.currentPlayer.cards.remove(card)
        game.players.reverse()
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        val nextPlayerPosition = ((currentPlayerIndex + 1) % (game.players.size))
        GameMaster.playedCards.add(0, card)
        GameMaster.currentPlayer = game.players[nextPlayerPosition]
    }

    private fun blockNextPlayer(card : Card){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        val lastCardPlayed = GameMaster.playedCards[0]
        if(card.cardColor == lastCardPlayed.cardColor && card.cardType == CardType.BLOCK_NEXT) {
            GameMaster.currentPlayer = game.players[(currentPlayerIndex + 2) % (game.players.size)]
            GameMaster.playedCards.add(0, card)
            manageTurns()
        } else {
            view.shosErroCardChosen()
            playTheCard()
        }

    }

    private fun addJokerCard(card: Card){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        println("Quelle couleur veux-tu choisir ?")
        var count = 1
        for(color in CardColor.values()){
            println("$count- " + color.colorName)
            count++
        }
        readLine()?.let {
            if(it.isNotEmpty()){
                when(it.toIntOrNull()) {
                    null ->{
                        view.showWtfResponseMessage("Mauvaise position, rééssaye\n\n")
                        checkCardIsPossibleToPlay(card)
                    }
                    else -> {
                        GameMaster.currentColor = CardColor.values()[it.toInt() - 1]
                        println("Couleur du jeu: " + GameMaster.currentColor.colorName)
                        val nextPlayerPosition = (currentPlayerIndex + 2) % (game.players.size)
                        GameMaster.currentPlayer = game.players[nextPlayerPosition]
                        manageTurns()
                    }
                }
            }else{
                view.showWtfResponseMessage("Il faut choisir une carte pour passer le tour\n")
                print("\n\nQuelle carte veux-tu jouer (position entre 1 & "
                        + GameMaster.currentPlayer.cards.size
                        +")? \n")
                checkCardIsPossibleToPlay(card)
            }
        }

    }
    //endregion


    // region * * * * * * * * * * * * * * * GAME ACTIONS * * * * * * * * * * * * * * *
    private fun pioche(){
        GameMaster.currentPlayer.cards.add(GameMaster.gameDeck[0])
        GameMaster.gameDeck.removeAt(0)
        println("${GameMaster.currentPlayer.name} a pioché\n")
        goToNextPlayer()
        manageTurns()
    }

    private fun checkPlayerCardChoice(it: String){
        when(it.toIntOrNull()) {
            null ->{
                view.showWtfResponseMessage("Mauvaise position, rééssaye\n\n")
                playTheCard()
            }
            else -> {
                val currentPlayerCardsSize = GameMaster.currentPlayer.cards.size
                val pos = it.toInt() - 1
                if(currentPlayerCardsSize >= 2 && pos >= 0 && pos < currentPlayerCardsSize){
                    checkCardIsPossibleToPlay(GameMaster.currentPlayer.cards[pos])
                }else{
                    view.showWtfResponseMessage("Aucune carte à cette position, rééssayez\n")
                    playTheCard()
                }
            }
        }
    }
    //

}