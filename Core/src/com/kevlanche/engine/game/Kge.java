package com.kevlanche.engine.game;

public class Kge {

	public static class Time {
		public long currentTimeMillis;
		public float gameTime;
		public float dt;
	}

	public static class Input {
		
		public static final int RIGHT = 1 << 0,
				LEFT = 1 << 1,
				UP = 1 << 2,
				DOWN = 1 << 3,
				SPACE = 1 << 4;

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
	public static class Graphics {
		public int width;
		public int height;
	}


	public Time time = new Time();
	public Input input = new Input();
	public Graphics graphics = new Graphics();
	
	private static Kge sInstance = new Kge();
	
	public static Kge getInstance() {
		return sInstance;
	}
	
}
