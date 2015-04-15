from math import *
from kge import *

game = 0
position = 0

def create():
	owner.scheduleUpdate(tick)
	global game
	game = owner.getState("game")
	global position
	position = owner.getState("gamePosition")
	print("Created, game = " + str(game))

def tick():
	(x,y) = (0,0)

	if kge.input.didPress(kge.input.UP):
		y += 1
	elif kge.input.didPress(kge.input.DOWN):
		y -= 1
	elif kge.input.didPress(kge.input.LEFT):
		x -= 1
	elif kge.input.didPress(kge.input.RIGHT):
		x += 1
	else:
		return

	print("req move on " + str(position))
	res = game.movePlayer(position, x, y)
	print(res)
	
