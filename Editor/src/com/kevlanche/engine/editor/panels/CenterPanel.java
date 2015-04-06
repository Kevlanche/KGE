package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Entity;
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
