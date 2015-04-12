package com.kevlanche.engine.game.actor;

import java.util.List;

public interface EntityDefinition {

	String getClassName();
	String getParentClass();

	void setDefaultParameters(Entity entity);

	List<String> getRequiredStates();

	List<String> getRequiredScripts();
	
	List<EntityDefinition> getChildren();
}
