package com.kevlanche.engine.game.actor;

import com.kevlanche.engine.game.actor.Actor.PositionScript.Position;
import com.kevlanche.engine.game.actor.Actor.SizeScript.Size;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.java.JavaScript;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class Actor extends BaseActor {

	public Position position;
	public Size size;

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
		addScript(new SizeScript(), new InstanceAcessor() {

			@Override
			public void set(ScriptInstance value) {
				size = (Size) value;
			}

			@Override
			public ScriptInstance getValue() {
				return size;
			}
		});
	}

	public class PositionScript extends JavaScript {

		public PositionScript() {
			super(Position.class);

			registerVar("x", 0);
			registerVar("y", 0);
		}

		public class Position extends Instance {
			public int x, y;

			public Position() {
			}
		}
	}

	public class SizeScript extends JavaScript {

		public SizeScript() {
			super(Size.class);

			registerVar("width", 4);
			registerVar("height", 4);
		}

		public class Size extends Instance {
			public int width, height;

			public Size() {
			}
		}
	}
}
