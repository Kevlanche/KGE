package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;

public class Transform extends JavaState {

	public static final String NAME = "transform";

	public final FloatVariable x, y, width, height, rotation;

	public final FloatVariable anchorX, anchorY;

	public Transform() {
		this(new X(), new Y(), new Width(), new Height(), new Rotation());
	}

	public Transform(X x, Y y, Width width, Height height, Rotation rotation) {
		super(NAME);

		this.x = register(x);
		this.y = register(y);
		this.width = register(width);
		this.height = register(height);
		this.rotation = register(rotation);
		this.anchorX = register(new FloatVariable("anchorX", 0));
		this.anchorY = register(new FloatVariable("anchorY", 0));
	}

	public static class X extends FloatVariable {

		public static final String NAME = "x";

		public X() {
			super(NAME, 0f);
		}
	}

	public static class Y extends FloatVariable {

		public static final String NAME = "y";

		public Y() {
			super(NAME, 0f);
		}
	}

	public static class Width extends FloatVariable {

		public static final String NAME = "width";

		public Width() {
			super(NAME, 4f);
		}
	}

	public static class Height extends FloatVariable {

		public static final String NAME = "height";

		public Height() {
			super(NAME, 4f);
		}
	}

	public static class Rotation extends FloatVariable {

		public static final String NAME = "rotation";

		public Rotation() {
			super(NAME, 0f);
		}
	}
}
