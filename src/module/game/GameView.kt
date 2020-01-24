package module.game

import data.model.game.Game

interface GameView{

    fun showWelcomeMessage()
    fun askForPlayersNumber()
    fun initializingPlayersMessage(response: String)
    fun showPlayers(game: Game)
    fun showCurrentPlayer()
    fun putFirstCardFromDeck()
    fun showWtfResponseMessage(error_message: String)
    fun showPlayedCard()
    fun showPowerCardActionOnNextPlayer()
    fun shoutUno()
    fun shosErroCardChosen()
    
}