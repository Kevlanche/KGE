package com.kevlanche.kge.runtime.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.kge.runtime.KgeRuntime;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new KgeRuntime(new GameState()), config);
	}
}
