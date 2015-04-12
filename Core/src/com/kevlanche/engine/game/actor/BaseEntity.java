package com.kevlanche.engine.game.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.script.ReloadListener;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public class BaseEntity implements Entity {

	protected class ScriptContainer implements ReloadListener {
		public final Script script;
		public CompiledScript compiled;

		public ScriptContainer(Script script) {
			this.script = script;

			script.addReloadListener(this);
		}

		@Override
		public void onScriptSourceReloaded() {
			compiled = null;
		}
	}

	private class ActiveState {
		public final List<State> states = new ArrayList<>();

		ActiveState createCopy() {
			final ActiveState ret = new ActiveState();
			ret.states.addAll(states);

			for (State state : states) {
				state.saveState();
			}
			return ret;
		}

		void restore() {
			for (State state : states) {
				state.restoreState();
			}
		}
	}

	protected final Map<String, CompiledScript> mInstalledComponents = new HashMap<>();

	private final List<ScriptContainer> mScripts;
	private final LinkedList<ActiveState> mStack;
	private ActiveState mCurrentState;

	private final List<Entity> mChildren;
	private final Entity mParent;

	private final List<EntityListener> mListeners;

	private final String mClassName;

	public BaseEntity(String className, Entity parent) {
		mClassName = className;
		mScripts = new CopyOnWriteArrayList<>();
		mStack = new LinkedList<>();
		mCurrentState = new ActiveState();
		mChildren = new CopyOnWriteArrayList<>();
		mParent = parent;
		mListeners = new CopyOnWriteArrayList<>();
	}

	@Override
	public String getClassName() {
		return mClassName;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addListener(EntityListener listener) {
		mListeners.add(listener);
	}

	@Override
	public void removeListener(EntityListener listener) {
		mListeners.remove(listener);
	}

	private void notifyChange() {
		for (EntityListener listener : mListeners) {
			listener.onEntityChanged(this);
		}
	}

	@Override
	public void tick() throws CompileException {
		boolean didCompile = false;
		for (ScriptContainer container : mScripts) {
			if (container.compiled == null) {
				container.compiled = container.script.compile(this);
				didCompile = false;
			}
		}
		if (didCompile) {
			notifyChange();
		}
		for (ScriptContainer container : mScripts) {
			container.compiled.tick();
		}
	}

	@Override
	public void addScript(Script script) {
		ScriptContainer container = new ScriptContainer(script);
		mScripts.add(container);

		notifyChange();
	}

	@Override
	public void removeScript(Script script) {
		for (ScriptContainer container : mScripts) {
			if (container.script == script) {
				container.script.removeReloadListener(container);
				mScripts.remove(container);
				notifyChange();
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Script> getScripts() {
		final List<Script> ret = new ArrayList<>();
		for (ScriptContainer sc : mScripts) {
			ret.add(sc.script);
		}
		return ret;
	}

	protected void addPermanentState(State state) {
		mCurrentState.states.add(state);
		notifyChange();
	}

	@Override
	public void addUserState(UserStateDefinition state) {
		addPermanentState(state.createInstance());
	}

	@Override
	public void removeUserState(UserStateDefinition.Instance state) {
		if (mCurrentState.states.remove(state)) {
			notifyChange();
		}
	}

	@Override
	public List<State> getStates() {
		return new ArrayList<>(mCurrentState.states);
	}

	@Override
	public List<Entity> getChildren() {
		return mChildren;
	}

	@Override
	public Entity getParent() {
		return mParent;
	}

	@Override
	public void addChild(Entity actor) {
		mChildren.add(actor);
		notifyChange();
	}
	
	@Override
	public void removeChild(Entity entity) {
		mChildren.remove(entity);
		notifyChange();
	}

	@Override
	public void saveState() {
		mStack.add(mCurrentState.createCopy());
		for (ScriptContainer sc : mScripts) {
			sc.compiled = null;
		}
		notifyChange();
	}

	@Override
	public void restoreState() {
		if (mStack.isEmpty()) {
			System.err.println("no state to restore!!!");
			Thread.dumpStack();
			return;
		}
		mCurrentState = mStack.removeLast();
		mCurrentState.restore();
		notifyChange();
	}
}
