--k = require('kge')
--k.debug("pneis")

time = 0
owner = nil;


centerX = 0
centerY = 0

radius = radius or 122

function onCreate( ctx )
	owner = ctx;
	print("OnCreate:: ", ctx)

	centerX = owner.position.x
	centerY = owner.position.y

	print("Copied centerX/Y = ", centerX, centerY);
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

		owner.position.x = math.cos(time) * radius + centerX
		owner.position.y = math.sin(time) * radius + centerY
	end
end
