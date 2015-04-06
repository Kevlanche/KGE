from math import *
from kge import *

# Keep python scripts write only?


def clamp(val, minVal, maxVal):
	return max(minVal, min(maxVal, val))

def tick():
	mv = sin(kge.time.gameTime) * 3 + 5
	position.x = mv

	# todo:
	#  owner.[attributes] istället för [attributes]
	
	#  self.removeScript() istället för owner.removeScript()? 
	# Men vad ska vi göra med addscript då?
