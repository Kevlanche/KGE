package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.FloatVariable;

public class Size extends JavaState {

	public final FloatVariable width, height;

	public Size() {
		this(new FloatVariable("width", 4), new FloatVariable("height", 4));
	}

	public Size(FloatVariable width, FloatVariable height) {
		super("size");

		this.width = register(width);
		this.height = register(height);
	}
}
