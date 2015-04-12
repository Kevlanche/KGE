from math import *
from kge import *

# Keep python scripts write only?

position = 0
rotation = 0
body = 0

def create():
	owner.scheduleUpdate(tick)
	global position
	position = owner.getState("position")

	global rotation
	rotation = owner.getState("rotation")

	size = owner.getState("size")

	global body
	body = owner.createPhysicsBody({
			'x' : position.x,
			'y' : position.y,
			'width' : size.width,
			'height' : size.height,
			'rotation' : size.rotation,
			'type' : 'dynamic'
		})
	owner.scheduleUpdate(tick)


def tick():
	position.x = body.x
	position.y = body.y
	rotation.degrees = body.rotation

def updatePos():
	owner.interpolate({
				'start' : (position.x, position.y),
				'end' : (game.x, game.y),
				'duration' : 0.25,
				'callback': setPos
			})

def setPos(x, y):
	position.x = x
	position.y = y