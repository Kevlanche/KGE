package com.kevlanche.engine.game;

import com.kevlanche.engine.game.actor.Entity;

public interface GameStateObserver {

	void onEntityAdded(Entity entity);
	void onEntityRemoved(Entity entity);
	void onFocusChanged(Entity newFocus);
	
	void onGenericChange();
	
	void onRunningChanged();
}
