package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;

public class Rotation extends JavaState {

	public static final String NAME = "rotation";
	public final FloatVariable degrees, anchorX, anchorY;

	public Rotation() {
		this(new Degrees());
	}

	public Rotation(Degrees degrees) {
		this(degrees, new FloatVariable("anchorX", 0), new FloatVariable(
				"anchorY", 0));
	}

	public Rotation(Degrees degrees, FloatVariable anchorX,
			FloatVariable anchorY) {
		super(NAME);

		this.degrees = register(degrees);
		this.anchorX = register(anchorX);
		this.anchorY = register(anchorY);
	}

	public static class Degrees extends FloatVariable {

		public Degrees() {
			super(NAME, 0);
		}
	}

}
