package com.kevlanche.engine.editor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.kevlanche.engine.game.script.Script;

public class ScriptParser {

	public Script parse(File file) throws IOException {
		return parse(new FileInputStream(file));
	}

	public Script parse(InputStream is) throws IOException {
		final String content;

		final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
		final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();

		int res = ToolProvider.getSystemJavaCompiler().run(is, stdOut, stdErr,
				"");
		System.out.println("compajled? " + res);
		System.out.println("stdOut : " + new String(stdOut.toByteArray()));
		System.out.println("stdErr : " + new String(stdErr.toByteArray()));
		return null;
	}
}
