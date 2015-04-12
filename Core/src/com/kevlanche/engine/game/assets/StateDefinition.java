package com.kevlanche.engine.game.assets;

import com.kevlanche.engine.game.state.State;

public interface StateDefinition {

	String getName();

	State createInstance();

}
