package com.kevlanche.engine.game.state.value.variable;

import com.kevlanche.engine.game.actor.SaveStateable;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.value.Value;

public interface Variable extends Value, SaveStateable {

	void set(int value) throws TypeException;

	void set(float value) throws TypeException;

	void set(String value) throws TypeException;

	void set(boolean value) throws TypeException;

	void set(Drawable value) throws TypeException;

	void set(Variable[] value) throws TypeException;

	void copy(Value other) throws TypeException;

	boolean hasDefaultValue();
}
