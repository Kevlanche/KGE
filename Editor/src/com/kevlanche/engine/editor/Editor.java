package com.kevlanche.engine.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.kevlanche.engine.editor.panels.CenterPanel;
import com.kevlanche.engine.editor.panels.LeftPanel;
import com.kevlanche.engine.editor.panels.RightPanel;
import com.kevlanche.engine.editor.panels.TopPanel;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptProvider;
import com.kevlanche.engine.game.script.lua.LuaScript;
import com.kevlanche.engine.game.script.lua.SimpleScriptLoader.Streamable;

public class Editor {

	public static class ClassPathScript extends LuaScript {

		private String mPath;

		public ClassPathScript(String classPath) throws IOException {
			super(new Streamable() {

				@Override
				public InputStream read() throws IOException {
					return Editor.class.getResourceAsStream(classPath);
				}
			});

			mPath = classPath;
		}

		@Override
		public String toString() {
			return mPath;
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

		sc.add(new ClassPathScript("/lua/test.lua"));
		sc.add(new ClassPathScript("/lua/transform.lua"));
		sc.add(new ClassPathScript("/lua/move.lua"));
		
		final ScriptProvider provider = new ScriptProvider() {
			
			@Override
			public List<Script> getScripts() {
				return sc;
			}
		};

		final GameState state = new GameState();
		state.addActor(new Actor());

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
