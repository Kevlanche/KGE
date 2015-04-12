from math import *
from kge import *

# Keep python scripts write only?

physics = 0

def create():
	owner.scheduleUpdate(tick)
	global physics
	physics = owner.getState("physics")

def tick():
	left = kge.input.isPressing(kge.input.LEFT)
	right = kge.input.isPressing(kge.input.RIGHT)

	if left ^ right:
		vx = physics.velocityX
		mv = kge.time.dt * 20.0
		if left:
			physics.velocityX = max(-5.0, vx - mv)
		else:
			physics.velocityX = min(5.0, vx + mv)

	if (kge.input.didPress(kge.input.SPACE)):
		kge.physics.gravityY = -kge.physics.gravityY

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