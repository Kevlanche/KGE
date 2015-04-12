package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.badlogic.gdx.Gdx;
import com.kevlanche.engine.game.EntityLoader;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.GameStateObserverAdapter;
import com.kevlanche.engine.game.actor.BaseEntity;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.assets.StateDefinition;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptDefinition;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Rendering;

@SuppressWarnings("serial")
public class LeftPanel extends BasePanel {

	private final GameState mState;

	private final JPanel mContentPanel;

	private final JPanel mAssetPanel;

	public LeftPanel(GameState state) {
		setBackground(Color.GRAY);

		mState = state;
		mState.addObserver(new GameStateObserverAdapter() {

			@Override
			public void onGenericChange() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						buildUI();
					}
				});
			}
		});

		setLayout(new BorderLayout());
		mContentPanel = new JPanel();
		add(mContentPanel, BorderLayout.NORTH);

		final JPanel btnPanel = new JPanel(new BorderLayout());
		// final JButton remove = new JButton("-");
		// remove.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// final Entity sel = mState.getCurrentSelection();
		// if (sel != null) {
		// mState.removeEntity(sel);
		// }
		// }
		// });
		// btnPanel.add(remove, BorderLayout.WEST);

		mAssetPanel = new JPanel();
		mAssetPanel.setLayout(new BoxLayout(mAssetPanel, BoxLayout.Y_AXIS));
		final JScrollPane assetPane = new JScrollPane(mAssetPanel);
		final Dimension dimen = new Dimension(20, 300);
		assetPane.setMinimumSize(dimen);
		assetPane.setPreferredSize(dimen);
		add(assetPane, BorderLayout.SOUTH);

		buildUI();
	}

	private class EntityAdder implements Runnable {

		private final Entity mParent;

		public EntityAdder(Entity parent) {
			mParent = parent;
		}

		@Override
		public void run() {
			final AssetProvider assetProvider = mState.getAssetProvider();
			final List<EntityDefinition> availClasses = assetProvider
					.getClasses();

			if (availClasses.isEmpty()) {
				return;
			}

			final EntityDefinition[] opts = availClasses
					.toArray(new EntityDefinition[availClasses.size()]);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					System.out.println("showing popup on "
							+ Thread.currentThread());
					final int sel = JOptionPane.showOptionDialog(
							LeftPanel.this, "What class should be added?",
							"Add class", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
					if (sel >= 0 && sel < opts.length) {

						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								try {
									final EntityDefinition clazz = opts[sel];
									final Entity loaded = new EntityLoader()
											.load(mParent, clazz,
													mState.getAssetProvider());
									addRecursive(loaded);
								} catch (Exception e) {
									Thread.dumpStack();
									e.printStackTrace();
									System.out.println("no entity for you!");
								}
							}

							private void addRecursive(final Entity loaded) {
								mState.addEntity(loaded);
								for (Entity child : loaded.getChildren()) {
									addRecursive(child);
								}
							}
						});
					}
				}
			});

		}
	}

	private void buildUI() {
		mContentPanel.removeAll();

		mContentPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 5, 5);

		for (final Entity a : mState.getEntities()) {
			if (a.getParent() == null) {
				addEntity(gbc, a);
			}
		}

		final JButton addChildless = new JButton("+");
		addChildless.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Gdx.app.postRunnable(new EntityAdder(null));
			}
		});
		mContentPanel.add(addChildless, gbc);
		gbc.gridy++;

		mAssetPanel.removeAll();
		final AssetProvider provider = mState.getAssetProvider();
		for (Drawable asset : provider.getDrawables()) {
			final JPanel repr = new JPanel();
			repr.setLayout(new BorderLayout());
			final JButton btn = new JButton(asset.getName());
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					final Entity focus = mState.getCurrentSelection();
					if (focus != null) {
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								for (State state : focus.getStates()) {
									if (state instanceof Rendering) {
										((Rendering) state).texture.set(asset);
										break;
									}
								}

							}
						});
					}
				}
			});
			repr.add(btn, BorderLayout.CENTER);
			repr.setBorder(BorderFactory
					.createSoftBevelBorder(BevelBorder.RAISED));

			mAssetPanel.add(repr);
		}

		revalidate();
		repaint();
	}

	private void addEntity(final GridBagConstraints gbc, final Entity a) {
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
		final JButton addChild = new JButton("+");
		addChild.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Gdx.app.postRunnable(new EntityAdder(a));
			}
		});
		actorPanel.add(addChild, BorderLayout.EAST);

		mContentPanel.add(actorPanel, gbc);
		gbc.gridy++;

		final List<Entity> children = a.getChildren();
		if (children != null && !children.isEmpty()) {
			gbc.insets.left += 10;

			for (Entity child : children) {
				addEntity(gbc, child);
			}

			gbc.insets.left -= 10;
		}
	}

	private final class MouseHandler extends MouseAdapter {
		private final JPanel actorPanel;
		private final Entity mActor;
		private boolean mHover = false;
		private boolean mClick = false;

		private MouseHandler(JPanel actorPanel, Entity actor) {
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
