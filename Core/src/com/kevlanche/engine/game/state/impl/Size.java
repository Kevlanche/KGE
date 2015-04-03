package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.FloatVariable;

public class Size extends JavaState<Size> {

	public final FloatVariable width, height;

	public Size() {
		super("size");

		width = register(new FloatVariable("width", 4));
		height = register(new FloatVariable("height", 4));
	}

	@Override
	protected Size newInstance() {
		return new Size();
	}
}
