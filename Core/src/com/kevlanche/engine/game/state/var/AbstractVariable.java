package com.kevlanche.engine.game.state.var;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.script.CompileException;

public abstract class AbstractVariable extends AbstractValue implements
		Variable {

	private List<Object> mStack = new ArrayList<>();
	private final String mName;

	public AbstractVariable(String name) {
		mName = name;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public void set(int value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(float value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(String value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(boolean value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public void set(Drawable value) throws TypeException {
		throw new TypeException();
	}

	@Override
	public Variable compile(Entity owner) throws CompileException {
		return this;
	}

	@Override
	public void copy(Variable other) throws TypeException {
		switch (getType()) {
		case INTEGER:
			set(other.asInt());
			break;
		case FLOAT:
			set(other.asFloat());
			break;
		case STRING:
			set(other.asString());
			break;
		case BOOL:
			set(other.asBool());
			break;
		case DRAWABLE:
			set(other.asDrawable());
			break;
		default:
			throw new TypeException("Don't know how to copy " + getType());
		}

	}

	@Override
	public void saveState() {
		switch (getType()) {
		case INTEGER:
			mStack.add(asInt());
			break;
		case FLOAT:
			mStack.add(asFloat());
			break;
		case STRING:
			mStack.add(asString());
			break;
		case BOOL:
			mStack.add(asBool());
			break;
		case DRAWABLE:
			mStack.add(asDrawable());
			break;
		default:
			throw new TypeException("Don't know how to save " + getType());
		}
	}

	@Override
	public void restoreState() {
		if (mStack.isEmpty()) {
			System.out.println("Nothing to restore on " + this);
			return;
		}
		final Object toRestore = mStack.remove(mStack.size() - 1);
		switch (getType()) {
		case INTEGER:
			set((int) toRestore);
			break;
		case FLOAT:
			set((float) toRestore);
			break;
		case STRING:
			set((String) toRestore);
			break;
		case BOOL:
			set((boolean) toRestore);
			break;
		case DRAWABLE:
			set((Drawable) toRestore);
			break;
		default:
			throw new TypeException("Don't know how to save " + getType());
		}
	}
}