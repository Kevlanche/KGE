package com.kevlanche.kge.runtime.desktop;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.kge.runtime.GdxAssetProvider;
import com.kevlanche.kge.runtime.KgeRuntime;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final GdxAssetProvider assetProvider = new GdxAssetProvider(new File(
				"C:\\Users\\Anton\\KGE\\SampleGame\\textures"));
		new LwjglApplication(new KgeRuntime(new GameState(assetProvider)),
				config);
	}
}
