package com.kevlanche.engine.game.actor;

import java.util.List;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public interface Entity extends SaveStateable {

	void tick(GameState game) throws CompileException;

	void addScript(Script script);

	void addUserState(UserStateDefinition state);

	void removeUserState(UserStateDefinition.Instance state);

	List<Script> getScripts();

	void removeScript(Script script);

	List<State> getStates();

	void addChild(Entity actor);

	List<Entity> getChildren();

	Entity getParent();

	void dispose();

	void addListener(EntityListener listener);

	void removeListener(EntityListener listener);

	String getClassName();

	void removeChild(Entity entity);
}
