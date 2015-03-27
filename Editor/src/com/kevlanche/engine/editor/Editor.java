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

import com.kevlanche.engine.editor.panels.CenterPanel;
import com.kevlanche.engine.editor.panels.LeftPanel;
import com.kevlanche.engine.editor.panels.RightPanel;
import com.kevlanche.engine.editor.panels.TopPanel;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.Script;
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
		final JFrame frame = new JFrame("Kevlanche Engine Editor v."
				+ Integer.MIN_VALUE);

		final List<Script> sc = new ArrayList<>();

		sc.add(new ClassPathScript("/lua/test.lua"));
		sc.add(new ClassPathScript("/lua/transform.lua"));

		final Actor actor = new Actor();
		actor.addScript(new ClassPathScript("/lua/test.lua"));

		final JPanel content = new JPanel();
		content.setBackground(Color.DARK_GRAY);
		content.setLayout(new BorderLayout());
		content.add(new LeftPanel(), BorderLayout.WEST);
		content.add(new TopPanel(actor), BorderLayout.NORTH);
		content.add(new CenterPanel(actor), BorderLayout.CENTER);
		content.add(new RightPanel(actor), BorderLayout.EAST);
		frame.setContentPane(content);

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