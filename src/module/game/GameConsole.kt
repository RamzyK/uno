package module.game

import data.GameMaster
import data.huge_title
import data.model.game.Game

class GameConsole : GameView {

    private val presenter = GamePresenter(this)

    init {
        presenter.initGame()
    }

    //region  * * * Override functions * * *
    override fun showWelcomeMessage() {
        print(huge_title)
    }

    override fun askForPlayersNumber() {
        println("Combien de joueurs y a-t-il ?")
        readLine()?.let {
            presenter.manageResponse(response = it)
        }
    }

    override fun initializingPlayersMessage(response: String) {
        println("Très bien, nous initialisont les $response joueurs, veuillez patienter...\n\n")
    }

    override fun showPlayers(game: Game) {
        for (player in game.players) {
            println(player)
        }
    }

    override fun putFirstCardFromDeck() {
        println("La partie peut commencer!\n")
        presenter.manageTurns()
    }

    override fun showCurrentPlayer() {
        print(GameMaster.currentPlayer.name + ", À toi de jouer:\n\n")
        print("Tes cartes: " + GameMaster.currentPlayer.cards)
        print("\nQuelle carte veux-tu jouer (position entre 1 & " + GameMaster.currentPlayer.cards.size + ") ? \n\n")
        if (GameMaster.currentPlayer.hasPickedUp) print("p pour passer le tour (Vous avez déjà pioché)\n") else print("p pour la pioche\n")
    }

    override fun showMessage(message: String) {
        print(message)
    }

    override fun showPlayedCard() {
        val lastCardPlayed = GameMaster.playedCards.first()
        print("DERNIÈRE CARTE JOUÉE: $lastCardPlayed | ")
        println("Couleur actuelle: ${lastCardPlayed.cardColor.colorName}\n\n")
    }

    override fun shoutUno() {
        println("Joueur ${GameMaster.currentPlayer.name} est à UNO !")
    }

    override fun showErrorCardChosen() {
        println("Tu ne peux pas jouer cette carte, elle n'est pas du bon numéro ou de la bonne couleur")
    }

    //endregion

}