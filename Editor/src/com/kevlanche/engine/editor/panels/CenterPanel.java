package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Graphics;

import com.kevlanche.engine.game.actor.Actor;

@SuppressWarnings("serial")
public class CenterPanel extends BasePanel {

	private final Actor mActor;

	public CenterPanel(Actor actor) {
		setBackground(Color.DARK_GRAY);

		mActor = actor;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		final int side = 50;

		g.setColor(Color.MAGENTA);
		g.fillRect((int) mActor.position.x - side / 2, (int) mActor.position.y
				- side / 2, side, side);
		
		repaint();
	}

}
