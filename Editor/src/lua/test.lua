--k = require('kge')
--k.debug("pneis")

time = 0
owner = nil;


centerX = centerX or 50
centerY = centerY or 50

function onCreate( ctx )
	owner = ctx;
	print("OnCreate:: ", ctx)

	owner.position.x = centerX
	owner.position.y = centerY
end

function update(dt)
	time = time + dt

	if owner then
		--[[
		local red = math.cos(time) * 122 + 122
		local green = math.sin(time) * 122 + 122
		local blue = math.cos(time + math.pi/2) * 122 + 122

		owner:colorize(red, green, blue)
		]]

		owner.position.x = math.cos(time) * 122 + centerX
		owner.position.y = math.sin(time) * 122 + centerY
	end
end
