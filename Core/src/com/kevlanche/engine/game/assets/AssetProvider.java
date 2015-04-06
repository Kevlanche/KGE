package com.kevlanche.engine.game.assets;

import java.util.Collection;

public interface AssetProvider {

	Collection<Drawable> getDrawables();
	Drawable getDrawable(String name);
}
