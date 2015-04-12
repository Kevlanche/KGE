from math import *
from kge import *

# Keep python scripts write only?

transform = 0
game = 0

def create():
	global transform
	transform = owner.getState("transform")

	global game
	game = owner.getState("gamePosition")

	# Begin by placing the game position at the visual position
	game.x = int(transform.x)
	game.y = int(transform.y)
	owner.addChangeListener(game, updatePos)

def updatePos():
	owner.interpolate({
				'start' : (transform.x, transform.y),
				'end' : (game.x, game.y),
				'duration' : 0.25,
				'callback': setPos
			})

def setPos(x, y):
	transform.x = x
	transform.y = y