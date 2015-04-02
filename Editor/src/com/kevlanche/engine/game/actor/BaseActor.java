package com.kevlanche.engine.game.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.script.ReloadListener;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.State;

public class BaseActor implements Actor {

	protected class ScriptContainer implements ReloadListener {
		public final Script script;
		public CompiledScript compiled;

		public ScriptContainer(Script script) {
			this.script = script;

			script.addReloadListener(this);
		}

		@Override
		public void onScriptSourceReloaded() {
			try {
				compiled = script.compile(BaseActor.this);
			} catch (CompileException e) {
				e.printStackTrace();
				System.err.println("Unable to recompile " + script);
			}
		}
	}

	private class ActiveState {
		public final List<State> states = new ArrayList<>();

		ActiveState createCopy() throws CompileException {
			final ActiveState ret = new ActiveState();
			for (State state : states) {
				ret.states.add(state.compile(BaseActor.this));
			}
			return ret;
		}
	}

	protected final Map<String, CompiledScript> mInstalledComponents = new HashMap<>();

	private final List<ScriptContainer> mScripts;
	private final LinkedList<ActiveState> mStack;
	private ActiveState mCurrentState;

	private final List<Actor> mChildren;
	private final Actor mParent;

	public BaseActor(Actor parent) {
		mScripts = new CopyOnWriteArrayList<>();
		mStack = new LinkedList<>();
		mCurrentState = new ActiveState();
		mChildren = new CopyOnWriteArrayList<>();
		mParent = parent;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void tick() throws CompileException {
		for (ScriptContainer container : mScripts) {
			if (container.compiled == null) {
				container.compiled = container.script.compile(this);
			}
		}
		for (ScriptContainer container : mScripts) {
			container.compiled.tick();
		}
	}

	@Override
	public void addScript(Script script) {
		ScriptContainer container = new ScriptContainer(script);
		mScripts.add(container);
	}

	@Override
	public void removeScript(Script script) {
		for (ScriptContainer container : mScripts) {
			if (container.script == script) {
				container.script.removeReloadListener(container);
				mScripts.remove(container);
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

	@Override
	public void addState(State state) {
		mCurrentState.states.add(state);
	}

	@Override
	public List<State> getStates() {
		return mCurrentState.states;
	}

	@Override
	public List<Actor> getChildren() {
		return mChildren;
	}

	@Override
	public Actor getParent() {
		return mParent;
	}

	@Override
	public void addActor(Actor actor) {
		mChildren.add(actor);
	}

	@Override
	public void saveState() throws CompileException {
		mStack.add(mCurrentState.createCopy());
		for (ScriptContainer sc : mScripts) {
			sc.compiled = null;
		}
	}

	@Override
	public void restoreState() {
		if (mStack.isEmpty()) {
			System.err.println("no state to restore!!!");
			Thread.dumpStack();
			return;
		}
		mCurrentState = mStack.removeLast();

	}
}
