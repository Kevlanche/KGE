package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.GameStateObserverAdapter;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityListener;
import com.kevlanche.engine.game.assets.StateDefinition;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptDefinition;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.StateUtil;
import com.kevlanche.engine.game.state.StateUtil.OwnedState;
import com.kevlanche.engine.game.state.value.ValueType;
import com.kevlanche.engine.game.state.value.variable.Variable;

@SuppressWarnings("serial")
public class RightPanel extends BasePanel implements EntityListener {

	private GameState mState;
	Timer mPoller = null;
	private final JPanel mAttributeHolder;
	private Entity mLastEntity;

	public RightPanel(GameState state) {
		mState = state;

		setBackground(Color.GRAY);
		setLayout(new BorderLayout());

		mAttributeHolder = new JPanel();
		add(mAttributeHolder, BorderLayout.NORTH);

		final JPanel btnPanel = new JPanel(new BorderLayout());

		final JButton addState = new JButton("+State");
		addState.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final List<StateDefinition> availStates = mState
						.getAssetProvider().getAvailableStates();

				if (availStates.isEmpty()) {
					return;
				}

				final Entity focus = mState.getCurrentSelection();

				if (focus == null) {
					return;
				}
				final UserStateDefinition[] opts = availStates
						.toArray(new UserStateDefinition[availStates.size()]);

				final int sel = JOptionPane.showOptionDialog(RightPanel.this,
						"What state should be added?", "Add state",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);

				if (sel >= 0 && sel < opts.length) {
					focus.addUserState(opts[sel]);
					mState.triggerOnChanged();
				}
			}
		});
		btnPanel.add(addState, BorderLayout.WEST);

		final JButton addScript = new JButton("+Script");
		addScript.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final List<ScriptDefinition> availScripts = mState
						.getAssetProvider().getScripts();

				if (availScripts.isEmpty()) {
					return;
				}

				final Entity focus = mState.getCurrentSelection();

				if (focus == null) {
					return;
				}
				final ScriptDefinition[] opts = availScripts
						.toArray(new ScriptDefinition[availScripts.size()]);

				final int sel = JOptionPane.showOptionDialog(RightPanel.this,
						"What script should be added?", "Add script",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);

				if (sel >= 0 && sel < opts.length) {
					focus.addScript(opts[sel].createInstance());
					mState.triggerOnChanged();
				}
			}
		});
		btnPanel.add(addScript, BorderLayout.EAST);
		add(btnPanel, BorderLayout.SOUTH);

		state.addObserver(new GameStateObserverAdapter() {

			@Override
			public void onGenericChange() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						buildUi();
					}
				});
			}
		});
		buildUi();
	}

	private void buildUi() {
		if (mPoller != null) {
			mPoller.stop();
		}
		mAttributeHolder.removeAll();

		if (mLastEntity != null) {
			mLastEntity.removeListener(this);
		}

		final Entity actor = mState.getCurrentSelection();
		mLastEntity = actor;
		if (actor == null) {
			mAttributeHolder.setLayout(new BorderLayout());
			mAttributeHolder.add(new JPanel() {

				public void paint(java.awt.Graphics g) {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(Color.WHITE);
					g.drawString("No selection", 5, 5);
				};
			}, BorderLayout.CENTER);
			return;
		} else {
			mLastEntity.addListener(this);
		}
		mAttributeHolder.setLayout(new GridBagLayout());

		final GridBagConstraints rootGbc = new GridBagConstraints(1, 1, 1, 1,
				1.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 5, 5);

		List<OwnedState> availStates = StateUtil.recursiveFindStates(actor);
		for (OwnedState fs : availStates) {
			if (fs.owner == actor) {
				addState(rootGbc, fs.state);
			}
		}
		mAttributeHolder.add(new JSeparator(), rootGbc);
		rootGbc.gridy++;
		for (OwnedState fs : availStates) {
			if (fs.owner != actor) {
				addState(rootGbc, fs.state);
			}
		}
		mAttributeHolder.add(new JSeparator(), rootGbc);
		rootGbc.gridy++;

		final JPanel instanceHolder = new JPanel();

		instanceHolder.setBorder(BorderFactory.createTitledBorder("Scripts"));
		instanceHolder.setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 2, 5);

		for (final Script script : actor.getScripts()) {

			final JPanel varHolder = new JPanel();
			varHolder.setBackground(Color.GRAY);
			varHolder.setBorder(BorderFactory
					.createLineBorder(Color.LIGHT_GRAY));
			varHolder.setLayout(new BorderLayout());
			varHolder.add(new JLabel(script.toString()), BorderLayout.WEST);

			final JButton rm = new JButton("x");
			rm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					actor.removeScript(script);
					mState.triggerOnChanged();
				}
			});
			varHolder.add(rm, BorderLayout.EAST);

			instanceHolder.add(varHolder, gbc);
			gbc.gridy++;

		}
		mAttributeHolder.add(instanceHolder, rootGbc);
		rootGbc.gridy++;

		revalidate();
		repaint();
	}

	private void addState(final GridBagConstraints rootGbc, final State state) {
		final JPanel instanceHolder = new JPanel();

		instanceHolder.setBorder(BorderFactory.createTitledBorder(state
				.getName()));
		instanceHolder.setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 2, 2), 2, 5);

		List<Variable> vars = state.getVariables();
		List<Component> fields = new ArrayList<>();
		AtomicBoolean ignoreUpdates = new AtomicBoolean(false);
		for (final Variable var : vars) {
			final Component editor;

			if (var.getType() == ValueType.BOOL) {
				JCheckBox cb = new JCheckBox();
				editor = cb;

				cb.setSelected(var.asBool());

				cb.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						var.set(cb.isSelected());
					}
				});

			} else {
				JTextField jtf = new JTextField(5);
				editor = jtf;

				jtf.getDocument().addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						update();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						update();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						update();
					}

					void update() {
						if (!ignoreUpdates.get()) {
							try {
								final float f = Float.parseFloat(jtf.getText());
								var.set(f);
							} catch (NumberFormatException e) {
								// Ignore
							}
						}
					}
				});
				if (var.getType() == ValueType.INTEGER
						|| var.getType() == ValueType.FLOAT) {
					final Consumer<Integer> changeNum = new Consumer<Integer>() {

						@Override
						public void accept(Integer t) {
							var.set(var.asFloat() + t);
						}
					};
					jtf.addKeyListener(new KeyAdapter() {

						public void keyPressed(KeyEvent e) {
							switch (e.getKeyCode()) {
							case KeyEvent.VK_UP:
								changeNum.accept(1);
								break;
							case KeyEvent.VK_DOWN:
								changeNum.accept(-1);
								break;

							}
						};
					});
					jtf.addMouseWheelListener(new MouseWheelListener() {

						@Override
						public void mouseWheelMoved(MouseWheelEvent arg0) {
							int uts = arg0.getUnitsToScroll();
							changeNum.accept(-uts);
							jtf.setText(var.asString());
						}
					});
				}
			}
			fields.add(editor);

			final JPanel varHolder = new JPanel();
			varHolder.setBackground(Color.GRAY);
			varHolder.setBorder(BorderFactory
					.createLineBorder(Color.LIGHT_GRAY));
			varHolder.setLayout(new BorderLayout());
			varHolder.add(
					new JLabel(var.getName() + " <" + var.getType() + ">"),
					BorderLayout.WEST);
			varHolder.add(editor, BorderLayout.EAST);

			instanceHolder.add(varHolder, gbc);
			gbc.gridy++;

		}
		mPoller = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ignoreUpdates.set(true);
				for (int i = 0; i < fields.size(); i++) {
					final Variable var = vars.get(i);
					final Component comp = fields.get(i);
					if (comp instanceof JTextField) {
						if (comp.hasFocus()) {
							continue;
						}

						final String newVal;
						if (var.getType() == ValueType.FLOAT) {
							newVal = String.format("%.2f", var.asFloat());
						} else {
							newVal = var.asString();
						}
						final JTextField jtf = (JTextField) comp;
						if (!newVal.equals(jtf.getText())) {
							jtf.setText(newVal);
						}
					} else if (comp instanceof JCheckBox) {
						boolean sel = var.asBool();
						final JCheckBox jcb = (JCheckBox) comp;
						if (sel != jcb.isSelected()) {
							jcb.setSelected(sel);
						}
					}
				}
				ignoreUpdates.set(false);
			}
		});
		mPoller.start();

		mAttributeHolder.add(instanceHolder, rootGbc);
		rootGbc.gridy++;

		revalidate();
		repaint();
	}

	@Override
	public void onEntityChanged(Entity entity) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				System.out.println("onEntityChanged??? "
						+ Thread.currentThread());
				buildUi();
			}
		});
	}
}
