package com.kevlanche.engine.game.script;

import java.util.Collection;

import com.kevlanche.engine.game.Entity;

public interface ScriptOwner extends Entity {

	ScriptInstance get(String name);
	
	void addScript(String name, Script script);
	Collection<ScriptInstance> getScripts();
}
