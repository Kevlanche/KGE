package com.kevlanche.engine.game.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.variable.NamedVariable;
import com.kevlanche.engine.game.state.value.variable.ObservableVariable;

public class JavaState implements ObservableState,
		ObservableVariable.ChangeListener {

	protected final Map<String,NamedVariable> mVars = new HashMap<>();

	private final String mName;
	private long mLastModified;

	public JavaState(String name) {
		mName = name;
	}

	@Override
	public NamedVariable get(String key) {
		return mVars.get(key);
	}
	@Override
	public Set<String> keySet() {
		return mVars.keySet();
	}

	@Override
	public State compile(GameState game, Entity owner) throws CompileException {
		// Type ret = newInstance();
		// for (int i = 0; i < mVars.size(); i++) {
		// ret.mVars.get(i).copy(mVars.get(i));
		// }
		// return ret;
		return this;
	}

	@Override
	public String getName() {
		return mName;
	}

	protected <T extends NamedVariable> T register(T var) {
		onChanged();
		mVars.put(var.getName(), var);
		var.setChangeListener(this);
		return var;
	}

	@Override
	public void onChanged(ObservableVariable var) {
		onChanged();
	}

	private void onChanged() {
		mLastModified = System.nanoTime();
	}

	@Override
	public long getLastModified() {
		return mLastModified;
	}

	@Override
	public void saveState() {
		for (NamedVariable var : mVars.values()) {
			var.saveState();
		}
		onChanged();

	}

	@Override
	public void restoreState() {
		for (NamedVariable var : mVars.values()) {
			var.restoreState();
		}
		onChanged();
	}

	@Override
	public String toString() {
		return mName + ": " + mVars;
	}
	
	@Override
	public boolean canBeShared() {
		return false;
	}
}
