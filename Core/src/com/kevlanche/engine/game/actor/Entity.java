package com.kevlanche.engine.game.actor;

import java.util.List;

import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public interface Entity extends SaveStateable {

	void tick() throws CompileException;

	void addScript(Script script);

	void addState(State state);

	List<Script> getScripts();

	void removeScript(Script script);

	List<State> getStates();

	void addActor(Entity actor);

	List<Entity> getChildren();

	Entity getParent();

	void dispose();

	void addListener(EntityListener listener);

	void removeListener(EntityListener listener);
}
