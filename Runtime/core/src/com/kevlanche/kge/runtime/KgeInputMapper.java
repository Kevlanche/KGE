package com.kevlanche.kge.runtime;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.kevlanche.engine.game.Kge;

public class KgeInputMapper extends InputAdapter {

	private final Map<Integer, Integer> mKeyMappings;

	public KgeInputMapper() {
		mKeyMappings = new HashMap<>();
		mKeyMappings.put(Keys.RIGHT, Kge.Input.RIGHT);
		mKeyMappings.put(Keys.LEFT, Kge.Input.LEFT);
		mKeyMappings.put(Keys.UP, Kge.Input.UP);
		mKeyMappings.put(Keys.DOWN, Kge.Input.DOWN);
		mKeyMappings.put(Keys.SPACE, Kge.Input.SPACE);
	}

	@Override
	public boolean keyDown(int keycode) {
		return process(keycode, true);
	}

	@Override
	public boolean keyUp(int keycode) {
		return process(keycode, false);
	}

	private boolean process(int keycode, boolean isPressing) {
		final Integer mapping = mKeyMappings.get(keycode);
		if (mapping == null) {
			return false;
		} else {
			Kge.getInstance().input.setPressing(mapping, isPressing);
			return true;
		}
	}
}
