from math import *
from kge import *

transform = 0
camera = 0

def create():
	global transform
	transform = owner.getState("transform")
	global camera
	camera = owner.getState("camera")

	owner.scheduleUpdate(tick)


def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))

def tick():
	camera.x = clamp(transform.x + transform.width/2 - camera.width/2, 0, 10)
	camera.y = 0