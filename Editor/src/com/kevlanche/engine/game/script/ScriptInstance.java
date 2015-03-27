package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.script.var.ScriptVariable;


public interface ScriptInstance {

	Script getSource();
	Object getValue(ScriptVariable var);

	void update(float dt);

}
