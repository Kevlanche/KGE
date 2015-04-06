package com.kevlanche.engine.game.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.var.Variable;

public abstract class JavaState implements State {

	protected final List<Variable> mVars = new ArrayList<>();

	private final String mName;

	public JavaState(String name) {
		mName = name;
	}

	@Override
	public List<Variable> getVariables() {
		return new ArrayList<>(mVars);
	}

	//
	// @Override
	// public State<Type> compile(Actor owner) throws CompileException {
	// final JavaState ret = new JavaState(mName);
	// for (ClonableVariable var : mVars) {
	// ret.register(var.copy());
	// }
	// return ret;
	// }
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

	protected <T extends Variable> T register(T var) {
		mVars.add(var);

		return var;
	}

	@Override
	public void saveState() {
		for (Variable var : mVars) {
			var.saveState();
		}
	}

	@Override
	public void restoreState() {
		for (Variable var : mVars) {
			var.restoreState();
		}
	}

	// @Override
	// public CompiledScript createInstance(ScriptOwner context) {
	// try {
	// return mInstanceClass.getDeclaredConstructor(getClass())
	// .newInstance(this);
	// } catch (InstantiationException | IllegalAccessException
	// | IllegalArgumentException | InvocationTargetException
	// | NoSuchMethodException | SecurityException e) {
	// e.printStackTrace();
	// throw new IllegalArgumentException();
	// }
	// }
}
