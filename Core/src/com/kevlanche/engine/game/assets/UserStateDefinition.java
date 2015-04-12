package com.kevlanche.engine.game.assets;

import com.kevlanche.engine.game.state.State;

public interface UserStateDefinition extends StateDefinition {

	Instance createInstance();

	public interface Instance extends State {

	}
}
