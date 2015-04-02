package com.kevlanche.engine.game.actor;

import java.util.List;

import com.kevlanche.engine.game.Entity;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public interface CompiledActor {

	void update(); // TODO +Canvas

	List<State> getStates();

	List<CompiledActor> getChildren();
}
