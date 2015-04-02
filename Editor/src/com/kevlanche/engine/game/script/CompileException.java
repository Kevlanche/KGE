package com.kevlanche.engine.game.script;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CompileException extends Exception {

	private final List<String> mRequiredComponents;

	public CompileException(List<String> requiredComponents) {
		mRequiredComponents = requiredComponents;
	}

	public CompileException(List<String> requiredComponents, Throwable cause) {
		super(cause);
		mRequiredComponents = requiredComponents;
	}

	public CompileException(Throwable cause) {
		super(cause);
		mRequiredComponents = new ArrayList<String>();
	}

	@Override
	public String getMessage() {
		return "Missing required component(s): " + mRequiredComponents +" || " + super.getMessage();
	}
}
