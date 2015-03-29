time = 0
owner = nil;
curve = nil;

bezx = {0, 5, 10}
bezy = {0, -2, 4}

function onCreate( ctx )
	owner = ctx;
	print("MOVE OnCreate:: ", ctx)

	local bezier = loadBezier()
	curve = bezier:curve(bezx, bezy)
	print(curve)
end

function update(dt)
	num = #bezx
	time = (time + dt) % num
	
	index = math.floor(time)
	x,y = curve(time)
	
	print("Bezier to ", x, y)
	
	owner.position.x = x
	owner.position.y = y
end


function loadBezier()
	local bezier = {}

	function bezier:curve(xv, yv)
		local reductor = {__index = function(self, ind)
			return setmetatable({tree = self, level = ind}, {__index = function(curves, ind)
				return function(t)
					local x1, y1 = curves.tree[curves.level-1][ind](t)
					local x2, y2 = curves.tree[curves.level-1][ind+1](t)
					return x1 + (x2 - x1) * t, y1 + (y2 - y1) * t
					end
				end})
			end
		}
		local points = {}
		for i = 1, #xv do
			print(i, xv[i], yv[i])
			points[i] = function(t) return xv[i], yv[i] end
		end
		return setmetatable({points}, reductor)[#points][1]
	end

	return bezier
end