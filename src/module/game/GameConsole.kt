package module.game

import data.GameMaster
import data.huge_title
import data.model.game.Game
import kotlin.reflect.jvm.internal.impl.load.java.Constant

class GameConsole(): GameView {

    val presenter = GamePresenter(this)

    init {
        presenter.initGame()
    }

    //region  * * * Override functions * * *
    override fun showWelcomeMessage() {
       println(huge_title)

    }

    override fun askForPlayersNumber() {
        println("Combien de joueurs y a-t-il ?")
        readLine()?.let{
            presenter.manageResponse(response = it)
        }
    }

    override fun initializingPlayersMessage(response: String) {
        println("Très bien, nous initialisont les $response joueurs, veuillez patienter...\n\n")
    }

    override fun showPlayers(game: Game) {
        for(player in game.players){
            println(player )
        }
    }

    override fun putFirstCardFromDeck() {
        println("La partie peut commencer!\n\n\n")
        presenter.manageTurns()
    }

    override fun showCurrentPlayer() {
        print(GameMaster.currentPlayer.name + ", À toi de jouer:\n\n")
        print("Tes cartes: " + GameMaster.currentPlayer.cards)
        print("\nQuelle carte veux-tu jouer (position entre 1 & " + GameMaster.currentPlayer.cards.size + ")? \n\n")
        print("p pour la pioche\n")
    }

    override fun showWtfResponseMessage(errorMessage: String) {
        print(errorMessage)
    }

    override fun showPlayedCard() {
        val lastCardPlayed = GameMaster.playedCards.first()
        print("DERNIÈRE CARTE JOUÉE: $lastCardPlayed | ")
        println("Couleur actuelle: ${lastCardPlayed.cardColor.colorName}\n\n")
    }

    override fun showPowerCardActionOnNextPlayer() {

    }

    override fun shoutUno() {

    }

    override fun shosErroCardChosen() {
        println("Tu ne peux pas jouer cette carte, elle 'est pas du bon numéro ou de la bonne couleur")
    }

    //endregion

}