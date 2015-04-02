from math import *
from kge import *

# Keep python scripts write only?

dx = 0.0
dy = 0.0
maxSpeed = 10.0
speedOnRelease = 0
timeOfRelease = 0

FULL_STOP_TIME = 0.55

def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))

def tick():
	mv = kge.time.dt * 30

	global dx
	global dy

	if kge.input.isPressing(kge.input.RIGHT):
		print("isRight")
		dx += mv
	elif kge.input.isPressing(kge.input.LEFT):
		dx -= mv
	elif kge.input.didRelease(kge.input.LEFT) or kge.input.didRelease(kge.input.RIGHT):
		global speedOnRelease
		global timeOfRelease
		speedOnRelease = dx
		timeOfRelease = kge.time.gameTime
	else:
		timeSinceRelease = kge.time.gameTime - timeOfRelease
		dx = speedOnRelease * max(0, FULL_STOP_TIME - timeSinceRelease)
		
	if kge.input.didPress(kge.input.SPACE):
		dy = 15
	elif not kge.input.isPressing(kge.input.SPACE):
		dy -= mv
	
	dx = clamp(dx, -maxSpeed, maxSpeed)
	dy = clamp(dy, -maxSpeed, maxSpeed)

	position.x += dx * kge.time.dt
	position.y = max(position.y + dy * kge.time.dt, 0)