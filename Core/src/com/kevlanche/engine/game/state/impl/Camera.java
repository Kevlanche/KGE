package com.kevlanche.engine.game.state.impl;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public class Camera extends JavaState {

	public static final String NAME = "camera";

	public final FloatVariable x, y, width, height, zoom, up;

	public Camera() {
		this(new X(), new Y(), new Width(), new Height(), new Zoom(), new Up());
	}

	public Camera(X x, Y y, Width width, Height height, Zoom zoom, Up up) {
		super(NAME);

		this.x = register(x);
		this.y = register(y);
		this.width = register(width);
		this.height = register(height);
		this.zoom = register(zoom);
		this.up = register(up);
	}

	@Override
	public boolean canBeShared() {
		return true;
	}

	public static class X extends FloatVariable {

		public X() {
			super("x", 0);
		}
	}

	public static class Y extends FloatVariable {

		public Y() {
			super("y", 0);
		}
	}

	public static class Zoom extends FloatVariable {

		public Zoom() {
			super("zoom", 1f);
		}

		@Override
		public void set(float value) throws TypeException {
			super.set(Math.max(.1f, Math.min(2f, value)));
		}
	}

	public static class Up extends FloatVariable {

		public Up() {
			super("up", 90f);
		}
	}

	public static class Width extends ClampedSize {

		public Width() {
			super("width", 12);
		}
	}

	public static class Height extends ClampedSize {

		public Height() {
			super("height", 12);
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
