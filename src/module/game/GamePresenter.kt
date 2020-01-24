package module.game

import data.GameMaster
import data.model.card.Card
import data.model.card.CardColor
import data.model.card.CardType
import data.model.game.Game

class GamePresenter(var view: GameView){

    private lateinit var game: Game
    private lateinit var gameMaster: GameMaster

    fun initGame() {
        view.showWelcomeMessage()
        view.askForPlayersNumber()
    }

    fun initPlayers(playerNumber: Int){
        with(GameMaster){
            game = initGame(playerNumber)
            playedCards.add(gameDeck.first())
            currentColor = gameDeck.first().cardColor
            if(gameDeck.first().cardType == CardType.NORMAL){
                currentPlayer = game.players.first()
            }else{
                currentPlayer = game.players[1]
            }
            gameDeck.removeAt(0)
        }
        view.showPlayers(game)

        view.putFirstCardFromDeck()
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
                        println("Très bien, nous initialisont les $response joueurs, veuillez patienter...\n\n")
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

    fun manageTurns() {
        with(GameMaster){
            print(currentPlayer.name + ", À toi de jouer:\n\n")
            print(currentPlayer.cards)
            print("\n\nQuelle carte veux-tu jouer (position entre 1 & "
                    + currentPlayer.cards.size
                    +")? \n")
            print("p pour la pioche\n")
        }
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
        GameMaster.playedCards.add(0, card)
        GameMaster.currentPlayer.cards.remove(card)
        println("dernière carte jouée: " + GameMaster.playedCards[0])

        when(card.cardType){
            CardType.BLOCK_NEXT -> blockNextPlayer()
            CardType.CHANGE_SENS -> changeGameDirection()
            CardType.PLUS_4 -> addPlus4Card(card)
            CardType.PLUS_2 -> addPlus2Card()
            CardType.JOKER -> addJokerCard(card)
            CardType.NORMAL -> normalCardPlayed(card)
        }
        manageTurns()
    }

    // region * * * * * * * * * * * * * * * SPECIAL CARDS ACTION SECTION * * * * * * * * * * * * * * *
    private fun normalCardPlayed(card: Card){
        val lastCardPlayed = GameMaster.playedCards[0]
        var lastPlayerPosition: Int = game.players.indexOf(GameMaster.currentPlayer)

        // Last card played is a normal card
        if(card.cardNumber == lastCardPlayed.cardNumber || card.cardColor == lastCardPlayed.cardColor){
            // Same number, same or different color
            GameMaster.playedCards.add(0, card)
            println("dernière carte jouée: " + GameMaster.playedCards[0])
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

    private fun changeGameDirection(){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        game.players.reverse()
        val nextPlayerPosition = (currentPlayerIndex + 1) % (game.players.size)
        GameMaster.currentPlayer = game.players[nextPlayerPosition]
    }

    private fun blockNextPlayer(){
        val currentPlayerIndex = game.players.indexOf(GameMaster.currentPlayer)
        GameMaster.currentPlayer = game.players[(currentPlayerIndex + 2) % (game.players.size)]
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
        print("Tu as pioché une carte")
        print(GameMaster.currentPlayer.cards)
        playTheCard()
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
                if(currentPlayerCardsSize >= 2 && pos >= 0 && pos <= currentPlayerCardsSize){
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