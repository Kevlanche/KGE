package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.Compilable;

public interface Script extends Compilable<CompiledScript> {

	String getName();

	void addReloadListener(ReloadListener listener);

	void removeReloadListener(ReloadListener listener);
}
