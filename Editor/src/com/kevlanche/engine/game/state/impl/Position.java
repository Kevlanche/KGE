package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.FloatVariable;

public class Position extends JavaState<Position> {

	public final FloatVariable x, y;

	public Position() {
		super("position");

		x = register(new FloatVariable("x", 0));
		y = register(new FloatVariable("y", 0));
	}

	@Override
	protected Position newInstance() {
		return new Position();
	}
}
