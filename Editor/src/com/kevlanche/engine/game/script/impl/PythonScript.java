package com.kevlanche.engine.game.script.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyJavaType;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.core.PyType;
import org.python.core.PyTypeDerived;
import org.python.core.adapter.PyObjectAdapter;
import org.python.util.PythonInterpreter;

import com.kevlanche.engine.editor.Editor.Streamable;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.var.Variable;

@SuppressWarnings("serial")
public class PythonScript extends BaseScript {

	protected final Streamable mSrc;

	static {
		PySystemState.initialize();
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof Actor;
			}

			@Override
			public PyObject adapt(Object o) {
				final Actor owner = (Actor) o;
				final PyType ret = new PyTypeDerived(PyType.fromClass(
						owner.getClass(), true)) {

					@Override
					public PyObject __findattr_ex__(String name) {
						for (State state : owner.getStates()) {
							if (state.getName().equals(name)) {
								return Py.getAdapter().adapt(state);
							}

						}
						return null;
					}

//					@Override
//					protected synchronized Object getJavaProxy() {
//						// Have to override this or we get a really strange
//						// error
//						// http://bugs.jython.org/issue1551
//						return this;
//					}
//
//					@Override
//					public String toString() {
//						return "mumpppp" + owner.toString();
//					}
				};
				return ret;
			}
		});
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof State;
			}

			@Override
			public PyObject adapt(Object o) {
				final State owner = (State) o;
				final PyType ret = new PyTypeDerived(PyType.fromClass(
						owner.getClass(), true)) {

					@Override
					public PyObject __findattr_ex__(String name) {
						Variable var = findVar(name);
						if (var != null) {
							return Py.getAdapter().adapt(var);
						} else {
							return null;
						}
					}

					private Variable findVar(String name) {
						for (Variable var : owner.getVariables()) {
							if (var.getName().equals(name)) {
								return var;
							}
						}
						return null;
					}

					@Override
					public void __setattr__(String name, PyObject value) {
						Variable var = findVar(name);
						if (var != null) {
							setVar(var, value);
						}
					}

//					@Override
//					protected synchronized Object getJavaProxy() {
//						// Have to override this or we get a really strange
//						// error
//						// http://bugs.jython.org/issue1551
//						return this;
//					}
//
//					@Override
//					public String toString() {
//						return "castor " + owner.toString();
//					}
				};
				return ret;
			}
		});
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof Variable;
			}

			@Override
			public PyObject adapt(Object o) {
				final Variable owner = (Variable) o;

				final PyObject ret;

				switch (owner.getType()) {
				case FLOAT:
					ret = Py.getAdapter().adapt(owner.asFloat());
					break;
				case INTEGER:
					ret = Py.getAdapter().adapt(owner.asInt());
					break;
				case STRING:
					ret = Py.getAdapter().adapt(owner.asString());
					break;
				default:
					System.err.println("Attempted to write illegal type \""
							+ owner.getType() + "\" on var \"" + owner);
					throw new RuntimeException("Illegal write");
				}
				return ret;
			}
		});
	}

	private static void setVar(final Variable owner, PyObject value) {
		switch (owner.getType()) {
		case FLOAT:
			owner.set((float) value.asDouble());
			break;
		case INTEGER:
			owner.set(value.asInt());
			break;
		case STRING:
			owner.set(value.asString());
			break;
		default:
			System.err.println("Attempted to write illegal type \""
					+ owner.getType() + "\" on var \"" + owner);
			throw new RuntimeException("Illegal write");
		}
	}

	public PythonScript(Streamable src) {
		mSrc = src;
	}

	@Override
	public CompiledScript compile(Actor owner) throws CompileException {
		return new Instance(owner);
	}

	private class Instance extends BaseScriptInstance {

		private Actor mOwner;
		private PyObject mTickFunc;

		public Instance(Actor owner) throws CompileException {
			mOwner = owner;
			mTickFunc = reload();
		}

		private void recursiveFindStates(Actor src, List<State> out) {
			outer: for (State state : src.getStates()) {

				final String stateName = state.getName();
				for (State existing : out) {
					if (existing.getName().equals(stateName)) {
						continue outer;
					}
				}
				out.add(state);
			}
			final Actor parent = src.getParent();
			if (parent != null) {
				recursiveFindStates(parent, out);
			}
		}

		private PyObject reload() throws CompileException {

			final List<State> allReachableStates = new ArrayList<>();
			recursiveFindStates(mOwner, allReachableStates);
			try {
				try (PythonInterpreter pi = new PythonInterpreter()) {
					pi.set("kge", Py.getAdapter().adapt(Kge.getInstance()));
					for (State state : allReachableStates) {
						pi.set(state.getName(), Py.getAdapter().adapt(state));
					}

					try (InputStream in = mSrc.read()) {
						pi.execfile(in, mSrc.toString());
					}
					return pi.eval("tick");
				}
			} catch (Exception e) {
				Thread.dumpStack();
				e.printStackTrace();
				throw new CompileException(e);
			}
		}

		@Override
		public void tick() {
			System.out.println("tick tock?");
			try {
				mTickFunc.__call__();
			} catch (Exception e) {
				Thread.dumpStack();
				e.printStackTrace();
			}
		}
	}
}
