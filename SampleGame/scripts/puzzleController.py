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
	game.x = int(transform.x)
	game.y = int(transform.y)
	owner.addChangeListener(game, updatePos)

	# Perhaps addChangeListener kge.input? 
	# And then input.pressing_DOWN, input.didpress_DOWN etc

def tick():
	#owner.getState("degrees").degrees = cos(kge.time.gameTime) * 180 + 180

	up = kge.input.didPress(kge.input.UP)
	down = kge.input.didPress(kge.input.DOWN)
	left = kge.input.didPress(kge.input.LEFT)
	right = kge.input.didPress(kge.input.RIGHT)
	if up or down or left or right:
		mvx = 0
		if right:
			mvx = 1
		elif left:
			mvx = -1
		mvy = 0
		if up:
			mvy = 1
		elif down:
			mvy = -1

		game.x += mvx
		game.y += mvy

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