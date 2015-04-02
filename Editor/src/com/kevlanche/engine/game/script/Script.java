package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.Entity;

public interface Script extends Entity<CompiledScript> {

	void addReloadListener(ReloadListener listener);
	void removeReloadListener(ReloadListener listener);
}
