package module.game

class GameConsole(): GameView {

    val presenter = GamePresenter(this)

    init {
        presenter.initGame()
    }

    //region  * * * Override functions * * *
    override fun showWelcomeMessage() {
       println("Bonjour, bienvenu sur UNO Kotlin")
       println("Combien de joueurs y a-t-il ?")
       readLine()?.let{
           if(it.length > 0){
               print("Très bien, nous initialisont les $it joueurs, veuillez patienter...")
               presenter.initPlayers(playerNumber = it.toInt())
           }else{
               print("Vous devez d'abord nous dire combien vous êtes avant de continuer!")
           }
       }
    }


    //endregion

}