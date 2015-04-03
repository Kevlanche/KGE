package com.kevlanche.engine.game.script.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyType;
import org.python.core.PyTypeDerived;
import org.python.core.adapter.PyObjectAdapter;
import org.python.util.PythonInterpreter;

import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.StateUtil;
import com.kevlanche.engine.game.state.StateUtil.FoundState;
import com.kevlanche.engine.game.state.var.Variable;

@SuppressWarnings("serial")
public class PythonScript extends BaseScript {

	public interface Streamable {
		InputStream read() throws IOException;
	}

	protected final Streamable mSrc;

	static {
		PySystemState.initialize();
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof Entity;
			}

			@Override
			public PyObject adapt(Object o) {
				final Entity owner = (Entity) o;
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
	public CompiledScript compile(Entity owner) throws CompileException {
		return new Instance(owner);
	}

	private class Instance extends BaseScriptInstance {

		private Entity mOwner;
		private PyObject mTickFunc;

		public Instance(Entity owner) throws CompileException {
			mOwner = owner;
			mTickFunc = reload();
		}


		private PyObject reload() throws CompileException {

			final List<FoundState> allReachableStates = StateUtil.recursiveFindStates(mOwner);
			try {
				try (PythonInterpreter pi = new PythonInterpreter()) {
					pi.set("kge", Py.getAdapter().adapt(Kge.getInstance()));
					for (FoundState state : allReachableStates) {
						pi.set(state.state.getName(), Py.getAdapter().adapt(state.state));
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
			try {
				mTickFunc.__call__();
			} catch (Exception e) {
				Thread.dumpStack();
				e.printStackTrace();
			}
		}
	}
}
