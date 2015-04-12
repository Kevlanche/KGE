package com.kevlanche.engine.game.assets;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.kevlanche.engine.game.Level;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.script.ScriptDefinition;

public interface AssetProvider {

	Collection<Drawable> getDrawables();

	List<ScriptDefinition> getScripts();

	List<StateDefinition> getAvailableStates();

	List<EntityDefinition> getClasses();

	List<Level> getLevels();
	
	void storeAsLevel(List<Entity> entities, String levelName) throws IOException;
}
