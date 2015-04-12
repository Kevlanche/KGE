from math import *
from kge import *

position = 0
size = 0
camera = 0

def create():
	global position
	position = owner.getState("position")
	global size
	size = owner.getState("size")
	global camera
	camera = owner.getState("camera")

	owner.addChangeListener(position, layout)
	owner.addChangeListener(size, layout)
	owner.scheduleUpdate(zoom)

	layout()

def layout():
	screenw = kge.graphics.width
	screenh = kge.graphics.height
	#todo use screenw/screenh to set an aspect-ratio-keeping size.

	camera.x = position.x
	camera.y = position.y
	camera.width = size.width
	camera.height = size.height


def zoom():
	camera.zoom = 1.0 + cos(kge.time.gameTime) * 0.25