package data.model.game

import data.GameDirection
import data.model.Player

data class Game (val players: MutableList<Player>, var gameDirection: GameDirection)
