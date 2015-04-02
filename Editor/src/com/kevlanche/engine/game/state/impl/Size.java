package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.IntVariable;

public class Size extends JavaState<Size> {

	public final IntVariable width, height;

	public Size() {
		super("size");

		width = register(new IntVariable("width", 4));
		height = register(new IntVariable("height", 4));
	}

	@Override
	protected Size newInstance() {
		return new Size();
	}
}
