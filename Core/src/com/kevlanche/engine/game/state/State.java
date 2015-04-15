package com.kevlanche.engine.game.state;

import com.kevlanche.engine.game.Compilable;
import com.kevlanche.engine.game.actor.SaveStateable;
import com.kevlanche.engine.game.state.value.ValueMap;
import com.kevlanche.engine.game.state.value.variable.NamedVariable;

public interface State extends ValueMap, Compilable<State>, SaveStateable {
	
	String getName();

	@Override
	NamedVariable get(String key);

	boolean canBeShared();
}
