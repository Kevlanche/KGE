package com.kevlanche.engine.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.kevlanche.engine.editor.panels.CenterPanel;
import com.kevlanche.engine.editor.panels.LeftPanel;
import com.kevlanche.engine.editor.panels.RightPanel;
import com.kevlanche.engine.editor.panels.TopPanel;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.DefaultEntity;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptProvider;
import com.kevlanche.engine.game.script.impl.PythonScript;

public class Editor {

	public static class PythonClassPathScript extends PythonScript {

		private String mPath;

		public PythonClassPathScript(String classPath, final GameState game)
				throws IOException {
			super(new Streamable() {

				@Override
				public InputStream read() throws IOException {
					return Editor.class.getResourceAsStream(classPath);
				}

				@Override
				public String toString() {
					return classPath;
				}
			});

			mPath = classPath;

			AtomicInteger scriptState;
			try (InputStream is = mSrc.read()) {
				scriptState = new AtomicInteger(read(is).hashCode());
			}
			new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try (InputStream is = mSrc.read()) {
						int newState = read(is).hashCode();
						if (newState != scriptState.get()) {
							scriptState.set(newState);
							System.out.println("Script change detected!");
							notifyReload();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}).start();
		}

		@Override
		public String toString() {
			return mPath;
		}

	}

	private static String read(InputStream resourceAsStream) {
		try (final Scanner sc = new Scanner(resourceAsStream)) {
			return sc.useDelimiter("\\Z").next();
		}
	}

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

		final List<Script> sc = new ArrayList<>();

		final GameState state = new GameState();

		sc.add(new PythonClassPathScript("/python/controller.py", state) {

			@Override
			public String toString() {
				return "Basic 2D controller";
			}
		});

		final ScriptProvider provider = new ScriptProvider() {

			@Override
			public List<Script> getScripts() {
				return sc;
			}
		};

		state.addEntity(new DefaultEntity(null));

		final JPanel content = new JPanel();
		content.setBackground(Color.DARK_GRAY);
		content.setLayout(new BorderLayout());
		content.add(new LeftPanel(state), BorderLayout.WEST);
		content.add(new TopPanel(state), BorderLayout.NORTH);
		content.add(new CenterPanel(state), BorderLayout.CENTER);
		content.add(new RightPanel(state, provider), BorderLayout.EAST);
		frame.setContentPane(content);

		frame.setLocationByPlatform(true);
		frame.setSize(800, 600);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}
