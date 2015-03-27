package com.kevlanche.engine.editor.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.kevlanche.engine.editor.Editor.ClassPathScript;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.var.ScriptVariable;

@SuppressWarnings("serial")
public class RightPanel extends BasePanel {

	public RightPanel(Actor actor) {
		setBackground(Color.GRAY);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		for (final ScriptInstance instance : actor.getScripts()) {

			final JPanel instanceHolder = new JPanel();
			instanceHolder.setBorder(BorderFactory.createEtchedBorder());
			instanceHolder.setLayout(new GridBagLayout());

			final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1,
					1.0, 1.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 5);

			final Script script = instance.getSource();
			for (final ScriptVariable var : script.getVariables()) {

				final JTextField jtf = new JTextField(5);

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
						script.set(var, jtf.getText());
					}
				});
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

			add(instanceHolder);

		}
	}

}
