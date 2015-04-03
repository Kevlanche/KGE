package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.GameStateObserverAdapter;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptProvider;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.StateUtil;
import com.kevlanche.engine.game.state.StateUtil.FoundState;
import com.kevlanche.engine.game.state.var.ValueType;
import com.kevlanche.engine.game.state.var.Variable;

@SuppressWarnings("serial")
public class RightPanel extends BasePanel {

	private GameState mState;
	Timer mPoller = null;
	private final JPanel mAttributeHolder;

	public RightPanel(GameState state, final ScriptProvider provider) {
		mState = state;

		setBackground(Color.GRAY);
		setLayout(new BorderLayout());

		mAttributeHolder = new JPanel();
		add(mAttributeHolder, BorderLayout.NORTH);

		final JPanel btnPanel = new JPanel(new BorderLayout());
		final JButton add = new JButton("+");
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final List<Script> availScripts = provider.getScripts();

				if (availScripts.isEmpty()) {
					return;
				}

				final Entity focus = mState.getCurrentSelection();

				if (focus == null) {
					return;
				}
				final Script[] opts = availScripts
						.toArray(new Script[availScripts.size()]);

				final int sel = JOptionPane.showOptionDialog(RightPanel.this,
						"What script should be added?", "Add script",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);

				if (sel >= 0 && sel < opts.length) {
					focus.addScript(opts[sel]);
					// focus.addScript("pythonTest", opts[sel]);
					mState.triggerOnChanged();
				}
			}
		});
		btnPanel.add(add, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);

		state.addObserver(new GameStateObserverAdapter() {

			@Override
			public void onGenericChange() {
				if (mPoller != null) {
					mPoller.stop();
				}
				mAttributeHolder.removeAll();
				buildUi();
				revalidate();
				repaint();
			}
		});
		buildUi();
	}

	private void buildUi() {
		final Entity actor = mState.getCurrentSelection();
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
		}
		mAttributeHolder.setLayout(new GridBagLayout());

		final GridBagConstraints rootGbc = new GridBagConstraints(1, 1, 1, 1,
				1.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 5, 5);

		List<FoundState> availStates = StateUtil.recursiveFindStates(actor);
		for (FoundState fs : availStates) {
			if (fs.owner == actor) {
				addState(rootGbc, fs.state);
			}
		}
		mAttributeHolder.add(new JSeparator(), rootGbc);
		rootGbc.gridy++;
		for (FoundState fs : availStates) {
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
		List<JTextField> fields = new ArrayList<>();
		AtomicBoolean ignoreUpdates = new AtomicBoolean(false);
		for (final Variable var : vars) {

			final JTextField jtf = new JTextField(5);
			fields.add(jtf);

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
			final JPanel varHolder = new JPanel();
			varHolder.setBackground(Color.GRAY);
			varHolder.setBorder(BorderFactory
					.createLineBorder(Color.LIGHT_GRAY));
			varHolder.setLayout(new BorderLayout());
			varHolder.add(
					new JLabel(var.getName() + " <" + var.getType() + ">"),
					BorderLayout.WEST);
			varHolder.add(jtf, BorderLayout.EAST);

			instanceHolder.add(varHolder, gbc);
			gbc.gridy++;

		}
		mPoller = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ignoreUpdates.set(true);
				for (int i = 0; i < fields.size(); i++) {
					final JTextField jtf = fields.get(i);
					if (jtf.hasFocus()) {
						continue;
					}
					final Variable var = vars.get(i);

					final String newVal = var.asString();
					if (!newVal.equals(jtf.getText())) {
						jtf.setText(newVal);
					}
				}
				ignoreUpdates.set(false);
			}
		});
		mPoller.start();

		mAttributeHolder.add(instanceHolder, rootGbc);
		rootGbc.gridy++;
	}
}
