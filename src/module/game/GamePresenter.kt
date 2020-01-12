package module.game

import data.GameMaster
import data.model.game.Game

class GamePresenter(var view: GameView){

    private lateinit var game: Game

    fun initGame() {
        view.showWelcomeMessage()
        //print("Players: " + game.players)
    }

    fun initPlayers(playerNumber: Int){
        GameMaster.playerAccount = playerNumber
        game = GameMaster.initGame()
    }

}