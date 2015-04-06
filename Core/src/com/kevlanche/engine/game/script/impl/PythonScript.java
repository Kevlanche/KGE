package com.kevlanche.engine.game.script.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyTuple;
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
import com.kevlanche.engine.game.script.Script;
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

					// @Override
					// protected synchronized Object getJavaProxy() {
					// // Have to override this or we get a really strange
					// // error
					// // http://bugs.jython.org/issue1551
					// return this;
					// }
					//
					// @Override
					// public String toString() {
					// return "castor " + owner.toString();
					// }
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
		private final List<Interpolator> mInterpolators;

		public Instance(Entity owner) throws CompileException {
			mOwner = owner;
			mTickFunc = reload();
			mInterpolators = new ArrayList<>();
		}

		private PyObject reload() throws CompileException {

			final List<FoundState> allReachableStates = StateUtil
					.recursiveFindStates(mOwner);
			try {
				try (PythonInterpreter pi = new PythonInterpreter()) {
					pi.set("kge", Py.getAdapter().adapt(Kge.getInstance()));
					pi.set("owner",
							Py.getAdapter().adapt(
									new ScriptAccessor(mOwner,
											PythonScript.this, this)));
					for (FoundState state : allReachableStates) {
						pi.set(state.state.getName(),
								Py.getAdapter().adapt(state.state));
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
			for (int i = 0; i < mInterpolators.size(); i++) {
				if (mInterpolators.get(i).update(Kge.getInstance().time.dt)) {
					mInterpolators.remove(i);
					i--;
				}
			}
		}
	}

	public static class ScriptAccessor {

		private final Entity mTarget;
		private final Script mSelf;
		private final Instance mInstance;

		public ScriptAccessor(Entity target, Script self, Instance instance) {
			mTarget = target;
			mSelf = self;
			mInstance = instance;
		}

		public void removeSelf() {
			System.out.println("Remove " + mSelf + " from " + mTarget);
			mTarget.removeScript(mSelf);
		}

		public void addScript(Object name) {
			System.out.println("Add script! " + name + " to " + mTarget);
		}
		
		public void finishInterpolation(PyFunction function) {
			for (Interpolator interpolator : mInstance.mInterpolators) {
				if (interpolator.mSetter.equals(function)) {
					interpolator.callSetter(1f);
					mInstance.mInterpolators.remove(interpolator);
					return;
				}
			}
		}

		public void interpolate(PyDictionary attrs) {
			final Object startVal = attrs.get("start");
			final Object endVal = attrs.get("end");
			final Object duration = attrs.get("duration");
			final Object callback = attrs.get("callback");

			if (!(startVal instanceof Number || startVal instanceof PyTuple)) {
				throw Py.RuntimeError("'start' must be specified as a number of typle");
			}
			if (!(endVal instanceof Number || endVal instanceof PyTuple)) {
				throw Py.RuntimeError("'end' must be specified as a number of typle");
			}
			if (!(duration instanceof Number)) {
				throw Py.RuntimeError("'duration' must be specified as a number");
			}
			if (!(callback instanceof PyFunction)) {
				throw Py.RuntimeError("'callback' must be specified as a function");
			}

			final float[] start, end;

			if (startVal instanceof PyTuple) {
				PyTuple startTuple = (PyTuple) startVal;
				if (!(endVal instanceof PyTuple)) {
					throw Py.RuntimeError("Both arguments must be tuples if one of them are");
				}
				PyTuple endTuple = (PyTuple) endVal;

				if (startTuple.size() != endTuple.size()) {
					throw Py.RuntimeError("Start/end arguments must be same length");
				}

				start = new float[startTuple.size()];
				end = new float[endTuple.size()];
				for (int i = 0; i < start.length; i++) {
					start[i] = ((Number) startTuple.get(i))
							.floatValue();
					end[i] = ((Number) endTuple.get(i)).floatValue();
				}
			} else {
				start = new float[1];
				end = new float[1];

				start[0] = ((Number) startVal).floatValue();
				end[0] = ((Number) endVal).floatValue();
			}

			mInstance.mInterpolators.add(new Interpolator(start, end,
					((Number) duration).floatValue(), (PyFunction) callback));
		}
	}

	private static class Interpolator {
		private float[] mMin;
		private float[] mMax;
		private PyFunction mSetter;

		private float mPassedTime, mDuration;
		final PyFloat[] mCallTmp;

		public Interpolator(float[] min, float[] max, float duration,
				PyFunction setter) {
			mMin = min;
			mMax = max;
			mCallTmp = new PyFloat[mMin.length];
			mSetter = setter;
			mDuration = duration;
		}

		public boolean update(float dt) {
			mPassedTime += dt;

			final float rel = mPassedTime
					/ (mDuration == 0f ? 0.001f : mDuration);

			if (rel >= 1f) {
				callSetter(1f);
				return true;
			} else {
				callSetter(rel);
				return false;
			}
		}

		private void callSetter(float relative) {
			for (int i = 0; i < mMin.length; i++) {
				mCallTmp[i] = new PyFloat(mMin[i] + (mMax[i] - mMin[i])
						* relative);
			}
			mSetter.__call__(mCallTmp);
		}
	}
}
