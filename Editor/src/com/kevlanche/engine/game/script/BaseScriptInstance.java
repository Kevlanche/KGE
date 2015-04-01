package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.script.var.ScriptVariable;

public abstract class BaseScriptInstance implements ScriptInstance {

	public void saveState() {
		for (ScriptVariable sv : getSource().getVariables()) {
			getSource().set(sv, getValue(sv));
			reset(sv);
		}
	}

	@Override
	public void reset(ScriptVariable var) {
		setValue(var, getSource().get(var));
	}
	
}
