package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public class Size extends JavaState {

	public static final String NAME = "size";
	public final FloatVariable width, height;

	public Size() {
		this(new Width(), new Height());
	}

	public Size(Width width, Height height) {
		super(NAME);

		this.width = register(width);
		this.height = register(height);
	}

	public static class Width extends ClampedSize {

		public Width() {
			super("width", 4);
		}
	}

	public static class Height extends ClampedSize {

		public Height() {
			super("height", 4);
		}

	}

	private static class ClampedSize extends FloatVariable {

		public ClampedSize(String name, float defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public void set(float value) throws TypeException {
			super.set(Math.max(.25f, value));
		}
	}
}
