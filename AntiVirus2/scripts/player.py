from math import *
from kge import *

game = 0

def create():
	owner.scheduleUpdate(tick)
	global game
	game = owner.getState("gamePosition")

def tick():
	if kge.input.didPress(kge.input.UP):
		game.reqy += 1
	elif kge.input.didPress(kge.input.DOWN):
		game.reqy -= 1
	elif kge.input.didPress(kge.input.LEFT):
		game.reqx -= 1
	elif kge.input.didPress(kge.input.RIGHT):
		game.reqx += 1
	
