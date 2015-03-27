package com.kevlanche.engine.game.actor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kevlanche.engine.game.actor.Actor.PositionScript.Position;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;
import com.kevlanche.engine.game.script.java.JavaScript;
import com.kevlanche.engine.game.script.var.IntVariable;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class Actor extends BaseActor {

	public Position position;

	public Actor() {
		addScript(new PositionScript(), new InstanceAcessor() {

			@Override
			public void set(ScriptInstance value) {
				position = (Position) value;
			}

			@Override
			public ScriptInstance getValue() {
				return position;
			}
		});
	}

	public class PositionScript extends JavaScript {

		private final ScriptVariable x = new IntVariable("x", 0);
		private final ScriptVariable y = new IntVariable("x", 0);

		Map<ScriptVariable, Object> values = new HashMap<>();

		@Override
		public List<ScriptVariable> getVariables() {
			return Arrays.asList(x, y);
		}

		@Override
		public void set(ScriptVariable variable, Object value) {
			values.put(variable, value);
		}

		@Override
		public Object get(ScriptVariable variable) {
			Object setVar = values.get(variable);
			return setVar == null ? variable.getDefaultValue() : setVar;
		}

		@Override
		public Position createInstance(ScriptOwner context) {
			Position ret = new Position();

			return ret;
		}

		public class Position extends Instance {
			public int x = bindInt(PositionScript.this.x, new IntAccessor() {

				@Override
				public Object getValue() {
					return x;
				}

				@Override
				protected void setInt(Integer value) {
					x = value;
				}
			});
			public int y = bindInt(PositionScript.this.y, new IntAccessor() {

				@Override
				public Object getValue() {
					return y;
				}

				@Override
				protected void setInt(Integer value) {
					y = value;
				}
			});
		}

	}
}
