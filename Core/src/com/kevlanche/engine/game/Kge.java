package com.kevlanche.engine.game;

import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.IntVariable;

public class Kge {

	public static class Time {
		public long currentTimeMillis;
		public float gameTime;
		public float dt;
	}

	public static class Input {

		public static final int RIGHT = 1 << 0, LEFT = 1 << 1, UP = 1 << 2,
				DOWN = 1 << 3, SPACE = 1 << 4;

		private int mIsPressing;
		private int didPress;
		private int didRelease;

		public boolean didPress(int key) {
			return (didPress & key) != 0;
		}

		public boolean isPressing(int key) {
			return (mIsPressing & key) != 0;
		}

		public boolean didRelease(int key) {
			return (didRelease & key) != 0;
		}

		public void setPressing(int key, boolean isPressing) {
			if (isPressing) {
				mIsPressing |= key;
				didPress |= key;
				didRelease &= ~key;
			} else {
				mIsPressing &= ~key;
				didPress &= ~key;
				didRelease |= key;
			}

		}

		void afterFrame() {
			didPress = 0;
			didRelease = 0;
		}
	}

	public static class Graphics extends JavaState {
		public final IntVariable width, height;

		public Graphics() {
			super("graphics");

			width = register(new IntVariable("width", 1));
			height = register(new IntVariable("height", 1));
		}
	}

	public static class Physics extends JavaState {
		public final FloatVariable gravityX, gravityY;

		public Physics() {
			this(new FloatVariable("gravityX", 0f), new FloatVariable(
					"gravityY", -9.82f));
		}

		public Physics(FloatVariable gravityX, FloatVariable gravityY) {
			super("physics");

			this.gravityX = register(gravityX);
			this.gravityY = register(gravityY);
		}

	}

	public Time time = new Time();
	public Input input = new Input();
	public Physics physics = new Physics();
	public Graphics graphics = new Graphics();

	private static Kge sInstance = new Kge();

	public static Kge getInstance() {
		return sInstance;
	}

}
