--k = require('kge')
--k.debug("pneis")

x = x or 0.0
y = y or 0.0


owner = nil

function onCreate( ctx )
	print("Transform onCreate!, ", ctx)
	owner = ctx
	--owner:colorize(255, 0, 0)
	--print("About to install")
	owner:install("transform")
	--owner.transform = self
	print("Installed?")
end

function update(dt)
	--print(owner.transform.x)
	print(owner.test.var)
	--ot.x = ot.x + 2
end

--[[
function getX( )
	return x
end
function getY( )
	return y
end

function setX( newX )
	x = newX
end
function setY( newY )
	y = newY
end
]]--
