package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.Compilable;

public interface Script extends Compilable<CompiledScript> {

	void addReloadListener(ReloadListener listener);
	void removeReloadListener(ReloadListener listener);
}
