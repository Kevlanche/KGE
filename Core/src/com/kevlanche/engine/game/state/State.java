package com.kevlanche.engine.game.state;

import java.util.List;

import com.kevlanche.engine.game.Compilable;
import com.kevlanche.engine.game.actor.SaveStateable;
import com.kevlanche.engine.game.state.var.Variable;

public interface State extends Compilable<State>, SaveStateable {
	String getName();
	List<Variable> getVariables();
}
