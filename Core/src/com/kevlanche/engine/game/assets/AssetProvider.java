package com.kevlanche.engine.game.assets;

import java.util.Collection;
import java.util.List;

import com.kevlanche.engine.game.script.Script;

public interface AssetProvider {

	Collection<Drawable> getDrawables();

	Drawable getDrawable(String name);

	List<Script> getScripts();
}
