from math import *
from kge import *

# Keep python scripts write only?

transform = 0
game = 0

def create():
	owner.scheduleUpdate(tick)
	global transform
	transform = owner.getState("transform")

	global game
	game = owner.getState("game")

	# Begin by placing the game position at the visual position
	game.player_x = int(transform.x)
	game.player_x = int(transform.y)
	owner.addChangeListener(game, updatePos)

	# Perhaps addChangeListener kge.input? 
	# And then input.pressing_DOWN, input.didpress_DOWN etc

def tick():
	if kge.input.didPress(kge.input.UP):
		game.player_target_y += 1
	elif kge.input.didPress(kge.input.DOWN):
		game.player_target_y -= 1
	elif kge.input.didPress(kge.input.LEFT):
		game.player_target_x = game.player_x - 1
	elif kge.input.didPress(kge.input.RIGHT):
		game.player_target_x = game.player_x + 1
	

def updatePos():
	owner.interpolate({
				'start' : (transform.x, transform.y),
				'end' : (game.player_x, game.player_y),
				'duration' : 0.05,
				'callback': setPos
			})

def setPos(x, y):
	transform.x = x
	transform.y = y