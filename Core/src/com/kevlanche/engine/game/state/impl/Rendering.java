package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.DrawableVariable;

public class Rendering extends JavaState {

	public static final String NAME = "rendering";
	public final DrawableVariable texture;

	public Rendering(DrawableSrc texture) {
		super(NAME);

		this.texture = register(texture);
	}

	public static class DrawableSrc extends DrawableVariable {

		public DrawableSrc(Drawable defaultImage, AssetProvider provider) {
			super("image", defaultImage, provider);
		}
	}
}
