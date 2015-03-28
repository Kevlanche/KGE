package com.kevlanche.engine.game.script;

import com.kevlanche.engine.game.script.var.ScriptVariable;

public abstract class BaseScriptInstance implements ScriptInstance {

	public void saveState() {
		for (ScriptVariable sv : getSource().getVariables()) {
			getSource().set(sv, getValue(sv));
			reset(sv);
		}
	}

//	public void reset() {
//		for (ScriptVariable var : getVariables()) {
//			set(var, var.getDefaultValue());
//		}
//	};
}
