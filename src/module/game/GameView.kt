package module.game

import data.model.game.Game

interface GameView{

    fun showWelcomeMessage()
    fun askForPlayersNumber()
    fun showPlayers(game: Game)
    fun putFirstCardFromDeck()
    fun showWtfResponseMessage(error_message: String)
    fun showPlayedCard()
    fun showPowerCardActionOnNextPlayer()
    fun shoutUno()
    fun shosErroCardChosen()
    
}