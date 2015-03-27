package com.kevlanche.engine.game.script.var;

import java.util.List;

import com.kevlanche.engine.game.script.ValueType;

public interface ScriptVariable {

	String getName();
	Object getDefaultValue();
	
	ValueType getType();
}
