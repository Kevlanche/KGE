package com.kevlanche.engine.game.state.value.variable;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.value.AbstractValue;
import com.kevlanche.engine.game.state.value.Value;
import com.kevlanche.engine.game.state.value.ValueType;

public abstract class AbstractVariable extends AbstractValue implements
		ObservableVariable {

	private List<Object> mStack = new ArrayList<>();
	private final String mName;
	private ChangeListener mListener;

	private final Object mDefaultValue;

	public AbstractVariable(String name, Object defaultValue) {
		mName = name;
		mDefaultValue = defaultValue;
	}

	@Override
	public void setChangeListener(ChangeListener listener) {
		mListener = listener;
	}

	protected void onChanged() {
		if (mListener != null) {
			mListener.onChanged(this);
		}
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
	public String toString() {
		return getName() + "=" + asString();
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
	public void copy(Value other) throws TypeException {
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
			if (other.getType() == ValueType.DRAWABLE) {
				set(other.asDrawable());
			} else {
				set(other.asString());
			}
			break;
		default:
			throw new TypeException("Don't know how to copy " + getType());
		}
		onChanged();

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
		onChanged();
	}

	@Override
	public boolean hasDefaultValue() {
		switch (getType()) {
		case INTEGER:
			return mDefaultValue.equals(asInt());
		case FLOAT:
			return mDefaultValue.equals(asFloat());
		case STRING:
			return mDefaultValue.equals(asString());
		case BOOL:
			return mDefaultValue.equals(asBool());
		case DRAWABLE:
			return mDefaultValue.equals(asDrawable());
		default:
			throw new TypeException("Don't know how to evaluate " + getType());
		}
	}
}