package com.kevlanche.engine.game.state;

import java.util.List;

import com.kevlanche.engine.game.Entity;
import com.kevlanche.engine.game.state.var.Variable;

public interface State extends Entity<State> {
	String getName();
	List<Variable> getVariables();
}
