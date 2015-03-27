package com.kevlanche.engine.game.script;

import java.util.Collection;

import com.kevlanche.engine.game.Entity;

public interface ScriptOwner extends Entity {

	void installComponent(String name, Object value);
	Object get(String name);
	
	void addScript(Script script);
	Collection<ScriptInstance> getScripts();
}
