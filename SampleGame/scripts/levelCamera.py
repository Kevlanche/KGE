from math import *
from kge import *

transform = 0
camera = 0

def create():
	global transform
	transform = owner.getState("transform")
	global camera
	camera = owner.getState("camera")

	owner.addChangeListener(transform, layout)
	owner.addChangeListener(kge.graphics, layout)

	layout()

def layout():
	screenw = kge.graphics.width
	screenh = kge.graphics.height

	dispw = transform.height * screenw / screenh

	camera.x = transform.x
	camera.y = transform.y
	camera.width = dispw
	camera.height = transform.height
	camera.up = transform.rotation + 90