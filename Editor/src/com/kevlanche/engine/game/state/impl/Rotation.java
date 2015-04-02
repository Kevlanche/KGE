package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.FloatVariable;

public class Rotation extends JavaState<Rotation> {

	public final FloatVariable degrees;

	public Rotation() {
		super("degrees");

		degrees = register(new FloatVariable("degrees", 4));
	}

	@Override
	protected Rotation newInstance() {
		return new Rotation();
	}
}
