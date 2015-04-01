package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptProvider;
import com.kevlanche.engine.game.script.ValueType;
import com.kevlanche.engine.game.script.var.ScriptVariable;

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

				final Actor focus = mState.getCurrentSelection();

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
					focus.addScript("pythonTest", opts[sel]);
					mState.triggerOnChanged();
				}
			}
		});
		btnPanel.add(add, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);

		state.addObserver(new Observer() {

			@Override
			public void update(Observable arg0, Object arg1) {
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
		final Actor actor = mState.getCurrentSelection();
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

		for (final ScriptInstance instance : actor.getScripts()) {

			final JPanel instanceHolder = new JPanel();
			instanceHolder.setBorder(BorderFactory.createEtchedBorder());
			instanceHolder.setLayout(new GridBagLayout());

			final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 5);

			final Script script = instance.getSource();
			List<ScriptVariable> vars = script.getVariables();
			List<JTextField> fields = new ArrayList<>();
			AtomicBoolean ignoreUpdates = new AtomicBoolean(false);
			for (final ScriptVariable var : vars) {

				final JTextField jtf = new JTextField(5);
				fields.add(jtf);

				final Consumer<Object> updater = new Consumer<Object>() {

					@Override
					public void accept(Object t) {
						int num;
						try {
							num = Integer.parseInt(t.toString());
						} catch (NumberFormatException e) {
							return;
						}
						script.set(var, num);

						instance.reset(var);
					}
				};
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
							updater.accept(jtf.getText());
						}
					}
				});
				if (var.getType() == ValueType.INTEGER
						|| var.getType() == ValueType.FLOAT) {
					final Consumer<Integer> changeNum = new Consumer<Integer>() {

						@Override
						public void accept(Integer t) {
							final int inc = (int) (Float.parseFloat(String
									.valueOf(instance.getValue(var))) + t);
							updater.accept(inc);
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
						}
					});
				}
				final JPanel varHolder = new JPanel();
				varHolder.setBackground(Color.GRAY);
				varHolder.setBorder(BorderFactory
						.createLineBorder(Color.LIGHT_GRAY));
				varHolder.setLayout(new BorderLayout());
				varHolder.add(new JLabel(var.getName() + " <" + var.getType()
						+ ">"), BorderLayout.WEST);
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
						final ScriptVariable var = vars.get(i);

						String newVal = String.valueOf(instance.getValue(var));
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
}
