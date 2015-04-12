package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kevlanche.kge.runtime.KgeRuntime;

@SuppressWarnings("serial")
public class CenterPanel extends BasePanel {

	public CenterPanel(KgeRuntime runtime) {
		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout());
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		final Canvas cv = new Canvas();
		new LwjglApplication(runtime, cv);
		add(cv, BorderLayout.CENTER);
	}
}
