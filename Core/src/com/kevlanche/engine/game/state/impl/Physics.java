package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.BoolVariable;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;

public class Physics extends JavaState {

	public static final String NAME = "physics";
	public final BoolVariable fixedRotation, staticBody;
	public final FloatVariable velocityX, velocityY;

	public Physics() {
		this(new FixedRotation(), new Static(), new VelocityX(),
				new VelocityY());
	}

	public Physics(FixedRotation fixedRotation, Static simulate,
			VelocityX velocityX, VelocityY velocityY) {
		super(NAME);

		this.fixedRotation = register(fixedRotation);
		this.staticBody = register(simulate);
		this.velocityX = register(velocityX);
		this.velocityY = register(velocityY);
	}

	public static class VelocityX extends FloatVariable {

		public VelocityX() {
			super("velocityX", 0);
		}
	}

	public static class VelocityY extends FloatVariable {

		public VelocityY() {
			super("velocityY", 0);
		}
	}

	public static class FixedRotation extends BoolVariable {

		public static final boolean DEFAULT_VALUE = false;

		public FixedRotation() {
			super("fixedRotation", DEFAULT_VALUE);
		}
	}

	public static class Static extends BoolVariable {

		public static final boolean DEFAULT_VALUE = true;

		public Static() {
			super("static", DEFAULT_VALUE);
		}
	}
}
