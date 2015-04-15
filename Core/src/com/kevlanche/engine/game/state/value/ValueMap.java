package com.kevlanche.engine.game.state.value;

import java.util.Set;

import com.kevlanche.engine.game.state.value.variable.Variable;

public interface ValueMap {

	Set<String> keySet();
	Variable get(String key);
}
