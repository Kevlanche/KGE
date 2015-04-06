package com.kevlanche.engine.game.actor;

import com.kevlanche.engine.game.script.CompileException;

public interface SaveStateable {

	void saveState();

	void restoreState();

}
