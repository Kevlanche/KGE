package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;

public class Position extends JavaState {

	public static final String NAME = "position";

	public final FloatVariable x, y;

	public Position() {
		this(new X(), new Y());
	}

	public Position(X x, Y y) {
		super(NAME);

		this.x = register(x);
		this.y = register(y);
	}

	public static class X extends FloatVariable {

		public X() {
			super("x", 0);
		}
	}

	public static class Y extends FloatVariable {

		public Y() {
			super("y", 0);
		}
	}
}
