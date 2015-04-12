package com.kevlanche.engine.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kevlanche.engine.editor.panels.CenterPanel;
import com.kevlanche.engine.editor.panels.LeftPanel;
import com.kevlanche.engine.editor.panels.RightPanel;
import com.kevlanche.engine.editor.panels.TopPanel;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.kge.runtime.KgeRuntime;

public class Editor {

	public static void main(String[] args) throws IOException {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					new Editor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Editor() throws Exception {
		final JFrame frame = new JFrame("Kevlanche Engine Editor v."
				+ Integer.MIN_VALUE);

		final KgeRuntime runtime = new KgeRuntime();
		final GameState state = runtime.getState(); 

//		state.addEntity(new DefaultEntity(null));

		final JPanel content = new JPanel();
		content.setBackground(Color.DARK_GRAY);
		content.setLayout(new BorderLayout());
		content.add(new LeftPanel(state), BorderLayout.WEST);
		content.add(new TopPanel(state), BorderLayout.NORTH);
		content.add(new CenterPanel(runtime), BorderLayout.CENTER);
		content.add(new RightPanel(state), BorderLayout.EAST);
		frame.setContentPane(content);

		frame.setLocationByPlatform(true);
		frame.setSize(1200,800);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}
