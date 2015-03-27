package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.Timer;

import com.kevlanche.engine.game.actor.Actor;

@SuppressWarnings("serial")
public class TopPanel extends BasePanel {

	private final Actor mActor;

	private boolean mIsRunning;

	public TopPanel(Actor actor) {
		setBackground(Color.BLACK);

		mActor = actor;

		setPreferredSize(new Dimension(50, 50));
		new Timer(5, new ActionListener() {

			long lastTime = System.currentTimeMillis();

			@Override
			public void actionPerformed(ActionEvent e) {
				final long currTime = System.currentTimeMillis();
				final long dt = currTime - lastTime;

				if (mIsRunning) {
					final float fdt = dt / 1000f;
					mActor.update(fdt);
				}

				lastTime = currTime;
			}
		}).start();

		final AbstractButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mIsRunning = false;
			}
		});
		add(stop);

		final AbstractButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mActor.reset();
				mIsRunning = true;
			}
		});
		add(start);
	}
}
