package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Level;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.kge.runtime.EntityLoaderImpl;

@SuppressWarnings("serial")
public class TopPanel extends BasePanel {

	private final GameState mState;

	private String lastName;

	public TopPanel(GameState state) {
		mState = state;

		setBackground(Color.BLACK);

		setPreferredSize(new Dimension(50, 50));

		final AbstractButton load = new JButton("Load...");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Gdx.app.postRunnable(new LevelSelector());
			}
		});
		add(load);

		final AbstractButton save = new JButton("Save...");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String name = JOptionPane.showInputDialog(TopPanel.this,
						"Level name?", lastName == null ? "" : lastName);

				if (name != null) {
					lastName = name;
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							try {
								mState.getAssetProvider().storeAsLevel(
										mState.getEntities(), name);
							} catch (IOException e) {
								Thread.dumpStack();
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		add(save);

		final AbstractButton toggle = new JButton("Start");
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mState.setIsRunning(!state.getIsRunning());

				if (state.getIsRunning()) {
					toggle.setText("Stop");
				} else {
					toggle.setText("Start");
				}
			}
		});
		add(toggle);
	}

	private class LevelSelector implements Runnable {

		@Override
		public void run() {
			final AssetProvider assetProvider = mState.getAssetProvider();
			final List<Level> availLevels = assetProvider.getLevels();

			if (availLevels.isEmpty()) {
				return;
			}

			final Level[] opts = availLevels.toArray(new Level[availLevels
					.size()]);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					System.out.println("showing popup on "
							+ Thread.currentThread());
					final int sel = JOptionPane.showOptionDialog(TopPanel.this,
							"What level should be loaded?", "Load level",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
					if (sel >= 0 && sel < opts.length) {
						lastName = opts[sel].getName();

						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								try {
									final Level lvl = opts[sel];

									lvl.load(mState);
								} catch (Exception e) {
									Thread.dumpStack();
									e.printStackTrace();
									System.out.println("invalid level :(");
								}
							}
						});
					}
				}
			});

		}
	}
}
