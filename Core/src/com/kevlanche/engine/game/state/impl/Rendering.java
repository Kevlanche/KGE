package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.var.DrawableVariable;

public class Rendering extends JavaState {

	public final DrawableVariable texture;

	public Rendering(DrawableVariable texture) {
		super("rendering");

		this.texture = texture;
	}
}
