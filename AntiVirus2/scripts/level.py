from math import *
from kge import *

game = 0
transform = 0
camera = 0

def create():
	global game
	game = owner.getState("game")
	global transform
	transform = owner.getState("transform")
	global camera
	camera = owner.getState("camera")

	owner.addChangeListener(transform, layout)
	owner.addChangeListener(kge.graphics, layout)
	owner.addChangeListener(game, movePlayer)

	layout()

	updateBoard()
	dumpBoard()

def updateBoard():
	w = int(transform.width)
	h = int(transform.height)
	
	game.board = [[0 for i in range(h)] for j in range(w)] 
	for rock in owner.getEntitiesWithType("rock"):
		rockpos = rock.getState("transform")
		rockx = int(rockpos.x)
		rocky = int(rockpos.y)
		print("rock @ " + str(rockx) +", " + str(rocky))
		game.board[rockx][rocky] = 1
	

def dumpBoard():	
	for row in game.board:
		for col in row:
			print(str(col)),
		print("")


def layout():
	screenw = kge.graphics.width
	screenh = kge.graphics.height

	dispw = transform.width
	disph = transform.height

	if screenw > screenh:
		dispw = transform.height * screenw / screenh
	else:
		disph = transform.width * screenh / screenw

	camera.x = transform.x + (transform.width - dispw) / 2
	camera.y = transform.y + (transform.height - disph) / 2
	camera.width = dispw
	camera.height = disph
	camera.up = transform.rotation + 90

	overx = float(transform.width)/dispw
	overy = float(transform.height)/disph
	camera.zoom = 1.1 * max(overx, overy)


def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))

def movePlayer():
	gx = game.player_x
	tx = game.player_target_x
	gy = game.player_y
	ty = game.player_target_y

	if tx < 0 or tx >= len(game.board):
		game.player_target_x = clamp(tx, 0, len(game.board)-1)
		return
	elif ty < 0 or ty >= len(game.board[tx]):
		game.player_target_y = clamp(ty, 0, len(game.board[tx])-1)
		return

	mvx = tx - gx
	mvy = ty - gy

	movedRock = False

	if not emptyBoard(tx,ty):
		if emptyBoard(tx+mvx, ty+mvy):
			for rock in owner.getEntitiesWithType("rock"):
				rockPos = rock.getState("transform")
				if rockPos.x == tx and rockPos.y == ty:
					global toMove
					toMove = rockPos
					owner.interpolate({
						'start' : (tx, ty),
						'end' : (tx + mvx, ty + mvy),
						'duration' : 0.05,
						'callback': moveRock
					})
					movedRock = True


	if emptyBoard(tx,ty) or movedRock:
		game.player_x = tx
		game.player_y = ty
	else:
		game.player_target_x = gx
		game.player_target_y = gy


def emptyBoard(x,y):
	if x < 0 or x >= len(game.board) or y < 0 or y >= len(game.board[x]):
		return False
	else:
		return game.board[x][y] == 0


toMove = 0

def moveRock(x, y):
	toMove.x = x
	toMove.y = y
	updateBoard()
