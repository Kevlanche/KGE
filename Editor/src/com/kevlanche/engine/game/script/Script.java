package com.kevlanche.engine.game.script;

import java.util.List;

import com.kevlanche.engine.game.script.var.ScriptVariable;

public interface Script {
	
	ScriptInstance createInstance(ScriptOwner context);
	
	List<ScriptVariable> getVariables();

	void set(ScriptVariable variable, Object value);
	Object get(ScriptVariable variable);

	void addReloadListener(ReloadListener listener);
	void removeReloadListener(ReloadListener listener);
}
