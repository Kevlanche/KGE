package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.FloatVariable;
import com.kevlanche.engine.game.state.var.Variable;

public class Position extends JavaState {

	public final FloatVariable x, y;

	public Position() {
		this(new FloatVariable("x", 0), new FloatVariable("y", 0));
	}

	public Position(FloatVariable x, FloatVariable y) {
		super("position");

		this.x = register(x);
		this.y = register(y);
	}
}
