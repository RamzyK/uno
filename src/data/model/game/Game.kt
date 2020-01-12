package data.model.game

import data.GameDirection
import data.model.Player

data class Game (val players: List<Player>, var gameDirection: GameDirection)
