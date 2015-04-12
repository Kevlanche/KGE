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

	for player in owner.getEntitiesWithType("player"):
		owner.addChangeListener(player.getState("gamePosition"), lambda: playerMoved(player))

	layout()

	updateBoard()
	dumpBoard()


def updateBoard():
	w = int(transform.width)
	h = int(transform.height)
	
	game.board = [[0 for i in range(h)] for j in range(w)] 
	for rock in owner.getEntitiesWithType("rock"):
		rockpos = rock.getState("gamePosition")
		rockx = int(rockpos.x)
		rocky = int(rockpos.y)
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
	camera.up = 90

	overx = float(transform.width)/dispw
	overy = float(transform.height)/disph
	camera.zoom = 1.1 * max(overx, overy)



def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))


def playerMoved(player):
	pos = player.getState("gamePosition")
	gx = pos.x
	tx = pos.reqx
	gy = pos.y
	ty = pos.reqy

	if tx < 0 or tx >= len(game.board):
		pos.reqx = clamp(tx, 0, len(game.board)-1)
		return
	elif ty < 0 or ty >= len(game.board[tx]):
		pos.reqy = clamp(ty, 0, len(game.board[tx])-1)
		return

	mvx = tx - gx
	mvy = ty - gy

	if not emptyBoard(tx,ty):
		if emptyBoard(tx+mvx, ty+mvy):
			for rock in owner.getEntitiesWithType("rock"):
				rockPos = rock.getState("gamePosition")
				if rockPos.x == tx and rockPos.y == ty:
					rockPos.x = tx + mvx
					rockPos.y = ty + mvy

					updateBoard()


	if not emptyBoard(tx,ty):
		pos.reqx = pos.x
		pos.reqy = pos.y
	else:
		pos.x = tx
		pos.y = ty


def emptyBoard(x,y):
	if x < 0 or x >= len(game.board) or y < 0 or y >= len(game.board[x]):
		return False
	else:
		return game.board[x][y] == 0