package com.kevlanche.engine.game.state.var;

@SuppressWarnings("serial")
public class TypeException extends RuntimeException {

	public TypeException() {
	}

	public TypeException(String msg) {
		super(msg);
	}

	public TypeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TypeException(Throwable cause) {
		super(cause);
	}
}
