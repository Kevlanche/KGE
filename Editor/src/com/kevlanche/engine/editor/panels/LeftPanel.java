package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Actor;

@SuppressWarnings("serial")
public class LeftPanel extends BasePanel {

	private final GameState mState;

	private final JPanel mContentPanel;
	public LeftPanel(GameState state) {
		setBackground(Color.GRAY);

		mState = state;
		mState.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				buildUI();
			}
		});
		setLayout(new BorderLayout());
		mContentPanel = new JPanel();
		add(mContentPanel, BorderLayout.NORTH);
		
		final JPanel btnPanel = new JPanel(new BorderLayout());
		final JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final Actor sel = mState.getCurrentSelection();
				if (sel != null) {
					mState.removeActor(sel);
				}
			}
		});
		btnPanel.add(remove, BorderLayout.WEST);
		final JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final Actor actor = new Actor();
				mState.addActor(actor);
			}
		});
		btnPanel.add(add, BorderLayout.EAST);
		add(btnPanel, BorderLayout.SOUTH);
		
		buildUI();
	}

	private void buildUI() {
		mContentPanel.removeAll();

		mContentPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 5, 5);

		for (Actor a : mState.getAllActors()) {
			final JPanel actorPanel = new JPanel(new BorderLayout());

			if (mState.getCurrentSelection() == a) {
				actorPanel.setBackground(Color.MAGENTA);
			} else {

				final MouseAdapter ma = new MouseHandler(actorPanel, a);
				actorPanel.addMouseListener(ma);
				actorPanel.addMouseMotionListener(ma);
			}

			final JLabel nameLabel = new JLabel(a.toString());
			actorPanel.add(nameLabel, BorderLayout.CENTER);

			mContentPanel.add(actorPanel, gbc);
			gbc.gridy++;
		}
		revalidate();
		repaint();
	}

	private final class MouseHandler extends MouseAdapter {
		private final JPanel actorPanel;
		private final Actor mActor;
		private boolean mHover = false;
		private boolean mClick = false;
		
		private MouseHandler(JPanel actorPanel, Actor actor) {
			this.actorPanel = actorPanel;
			this.mActor = actor;
			
			updateBg();
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			setHover(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setHover(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			setClicked(true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			setClicked(false);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			mState.setCurrentSelection(mActor);
		}

		private void setClicked(boolean b) {
			mClick = b;
			updateBg();
		}

		private void setHover(boolean b) {
			mHover = b;
			updateBg();
		}

		private void updateBg() {
			if (mHover != mClick) {
				actorPanel.setBackground(Color.DARK_GRAY);
			} else {
				actorPanel.setBackground(Color.LIGHT_GRAY);
			}
		}
	}
}
