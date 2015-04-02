package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Actor;

@SuppressWarnings("serial")
public class TopPanel extends BasePanel {

	public TopPanel(final GameState state) {
		setBackground(Color.BLACK);

		setPreferredSize(new Dimension(50, 50));

		final AbstractButton toggle = new JButton("Start");
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.setIsRunning(!state.getIsRunning());
				
				if (state.getIsRunning()) {
					toggle.setText("Stop");
				} else {
					toggle.setText("Start");
				}
//				for (Actor a : state.getAllActors()) {
//					a.reset();
//				}
//				state.setIsRunning(false);
			}
		});
		add(toggle);
//
//		final AbstractButton start = new JButton("Start");
//		start.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				for (Actor a : state.getAllActors()) {
//					a.reset();
//				}
//				state.setIsRunning(true);
//			}
//		});
//		add(start);
	}
}
