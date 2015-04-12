package com.kevlanche.engine.game.state;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.variable.ObservableVariable;
import com.kevlanche.engine.game.state.value.variable.Variable;

public class JavaState implements ObservableState,
		ObservableVariable.ChangeListener {

	protected final List<Variable> mVars = new ArrayList<>();

	private final String mName;
	private long mLastModified;

	public JavaState(String name) {
		mName = name;
	}

	@Override
	public List<Variable> getVariables() {
		return new ArrayList<>(mVars);
	}

	@Override
	public State compile(Entity owner) throws CompileException {
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

	protected <T extends ObservableVariable> T register(T var) {
		onChanged();
		mVars.add(var);
		var.setChangeListener(this);
		return var;
	}

	@Override
	public void onChanged(ObservableVariable var) {
		onChanged();
	}

	private void onChanged() {
		mLastModified = System.currentTimeMillis();
	}

	@Override
	public long getLastModified() {
		return mLastModified;
	}

	@Override
	public void saveState() {
		for (Variable var : mVars) {
			var.saveState();
		}
		onChanged();

	}

	@Override
	public void restoreState() {
		for (Variable var : mVars) {
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
