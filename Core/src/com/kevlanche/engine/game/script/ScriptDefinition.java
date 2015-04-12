package com.kevlanche.engine.game.script;


public interface ScriptDefinition {
	
	String getName();

	Script createInstance();
}
