from math import *
from kge import *

# Keep python scripts write only?


def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))

def tick():
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


		owner.finishInterpolation(setPos)
		owner.interpolate({
				'start' : (position.x, position.y),
				'end' : (position.x + mvx, position.y + mvy),
				'duration' : 0.25,
				'callback': setPos
			})
		#ease(position.y, 1.0, 15);


def setX(value):
	print("Set x : " + str(value))

def setY(value):
	position.y = value
	print("Set y : " + str(value))

def setPos(x, y):
	position.x = x
	position.y = y