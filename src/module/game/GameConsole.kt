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
    override fun showPlayers(game: Game) {
        for(player in game.players){
            println(player )
        }
    }

    override fun putFirstCardFromDeck() {
        println("\n1ere carte: " + GameMaster.playedCards.first())
        println("La partie peut commencer!\n")
        presenter.manageTurns()
    }

    override fun showWtfResponseMessage(errorMessage: String) {
        print(errorMessage)
    }

    override fun showPlayedCard() {

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