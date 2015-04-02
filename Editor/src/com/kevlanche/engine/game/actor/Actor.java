package com.kevlanche.engine.game.actor;

import java.util.List;

import com.kevlanche.engine.game.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public interface Actor {

	void tick() throws CompileException;

	void addScript(Script script);

	void addState(State state);

	List<Script> getScripts();

	void removeScript(Script script);

	List<State> getStates();

	void addActor(Actor actor);

	List<Actor> getChildren();

	Actor getParent();

	void saveState() throws CompileException;

	void restoreState();

	void dispose();
}
