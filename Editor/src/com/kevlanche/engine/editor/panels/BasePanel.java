package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BasePanel extends JPanel {

	public BasePanel() {
		setBackground(Color.GRAY);

		Dimension dimension = new Dimension(200, 200);
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
	}
}
