package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.NamedBoolVariable;
import com.kevlanche.engine.game.state.value.variable.NamedFloatVariable;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public class Physics extends JavaState {

	public static final String NAME = "physics";

	public final NamedBoolVariable fixedRotation, dynamic;
	public final VelocityX velocityX;
	public final VelocityY velocityY;

	private NamedFloatVariable mInterceptedX, mInterceptedY;

	public Physics() {
		this(new FixedRotation(), new Dynamic(), new VelocityX(),
				new VelocityY());
	}

	public Physics(FixedRotation fixedRotation, Dynamic dynamic,
			VelocityX velocityX, VelocityY velocityY) {
		super(NAME);

		this.fixedRotation = register(fixedRotation);
		this.dynamic = register(dynamic);
		this.velocityX = register(velocityX);
		this.velocityY = register(velocityY);
	}

	// TODO intercepting variables like this can be very useful (albeit hard to
	// follow). Consider doing it a standard api in the Variable interface
	public void intercept(NamedFloatVariable velX, NamedFloatVariable velY) {
		velocityX.setIntercepter(velX);
		velocityY.setIntercepter(velY);
	}

	public static class VelocityX extends InterceptableFloatVariable {

		public VelocityX() {
			super("velocityX", 0);
		}

		@Override
		public float asFloat() throws TypeException {
			return super.asFloat();
		}
	}

	public static class VelocityY extends InterceptableFloatVariable {

		public VelocityY() {
			super("velocityY", 0);
		}
	}

	public static class InterceptableFloatVariable extends NamedFloatVariable {

		private NamedFloatVariable mIntercepter;

		public InterceptableFloatVariable(String name, float defaultValue) {
			super(name, defaultValue);
		}

		void setIntercepter(NamedFloatVariable var) {
			mIntercepter = var;
			onChanged();
		}

		@Override
		public float asFloat() throws TypeException {
			if (mIntercepter != null) {
				return mIntercepter.asFloat();
			} else {
				return super.asFloat();
			}
		}

		@Override
		public void set(float value) throws TypeException {
			if (mIntercepter != null) {
				mIntercepter.set(value);
			}
			// Still set super to trigger a change event.
			super.set(value);
		}
	}

	public static class FixedRotation extends NamedBoolVariable {

		public static final boolean DEFAULT_VALUE = false;

		public FixedRotation() {
			super("fixedRotation", DEFAULT_VALUE);
		}
	}

	public static class Dynamic extends NamedBoolVariable {

		public static final boolean DEFAULT_VALUE = false;

		public Dynamic() {
			super("dynamic", DEFAULT_VALUE);
		}
	}
}
