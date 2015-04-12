package com.kevlanche.engine.game;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;

public interface EntityLoader {

	public abstract Entity load(Entity parent, EntityDefinition clazz,
			AssetProvider provider) throws Exception;

}