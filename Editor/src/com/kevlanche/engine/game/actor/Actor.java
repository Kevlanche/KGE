package com.kevlanche.engine.game.actor;

import com.kevlanche.engine.game.actor.Actor.PositionScript.Position;
import com.kevlanche.engine.game.actor.Actor.RotationScript.Rotation;
import com.kevlanche.engine.game.actor.Actor.SizeScript.Size;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.java.JavaScript;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class Actor extends BaseActor {

	public Position position;
	public Size size;
	public Rotation rotation;

	public Actor() {
		// addScript("position", new PositionScript());
		// addScript("size", new SizeScript());
		// addScript("rotation", new RotationScript());
		addScript(new PositionScript(), new InstanceAcessor() {

			@Override
			public void set(ScriptInstance value) {
				mInstalledComponents.put("position", value);
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
				mInstalledComponents.put("size", value);
				size = (Size) value;
			}

			@Override
			public ScriptInstance getValue() {
				return size;
			}
		});
		addScript(new RotationScript(), new InstanceAcessor() {

			@Override
			public void set(ScriptInstance value) {
				mInstalledComponents.put("rotation", value);
				rotation = (Rotation) value;
			}

			@Override
			public ScriptInstance getValue() {
				return rotation;
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
			public float x, y;

			public Position() {
			}

			// TODO interpolateTo(...). Gör det till en protected generisk
			// function i instance.
		}
	}

	public class SizeScript extends JavaScript {
		public SizeScript() {
			super(Size.class);

			registerVar("width", 4);
			registerVar("height", 4);
		}

		@Override
		public void set(ScriptVariable variable, Object value) {
			super.set(variable, Math.max(1, toInt(value)));
		}

		public class Size extends Instance {
			public int width, height;

			public Size() {
			}
		}
	}

	public class RotationScript extends JavaScript {

		private ScriptVariable mDegrees;

		public RotationScript() {
			super(Rotation.class);

			mDegrees = registerVar("degrees", 0);
			registerVar("anchorX", 0);
			registerVar("anchorY", 0);
		}

		@Override
		public void set(ScriptVariable variable, Object value) {
			if (variable == mDegrees) {
				super.set(variable, toFloat(value) % 360f);
			} else {
				super.set(variable, Math.max(0f, Math.min(1f, toFloat(value))));
			}
		}

		public class Rotation extends Instance {
			public float degrees;
			public float anchorX;
			public float anchorY;

			public Rotation() {
			}
		}
	}
}
