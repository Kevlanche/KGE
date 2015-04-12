package com.kevlanche.engine.game.script.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyIterator;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyTuple;
import org.python.core.PyType;
import org.python.core.PyTypeDerived;
import org.python.core.adapter.PyObjectAdapter;
import org.python.util.PythonInterpreter;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.script.CompiledScript;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.state.ObservableState;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.StateUtil;
import com.kevlanche.engine.game.state.StateUtil.OwnedState;
import com.kevlanche.engine.game.state.value.variable.ArrayVariable;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.IntVariable;
import com.kevlanche.engine.game.state.value.variable.NamedVariable;
import com.kevlanche.engine.game.state.value.variable.StringVariable;
import com.kevlanche.engine.game.state.value.variable.Variable;

@SuppressWarnings("serial")
public class PythonScript extends BaseScript {

	private static final class DerivedState extends PyTypeDerived {
		private final State owner;

		private DerivedState(PyType subtype, State owner) {
			super(subtype);
			this.owner = owner;
		}

		@Override
		public PyObject __findattr_ex__(String name) {
			NamedVariable var = findVar(name);
			if (var != null) {
				return Py.getAdapter().adapt(var);
			} else {
				return null;
			}
		}

		private NamedVariable findVar(String name) {
			for (NamedVariable var : owner.getVariables()) {
				if (var.getName().equals(name)) {
					return var;
				}
			}
			return null;
		}

		@Override
		public void __setattr__(String name, PyObject value) {
			NamedVariable var = findVar(name);
			if (var != null) {
				setVar(var, value);
			}
		}

		@Override
		protected synchronized Object getJavaProxy() {
			// Have to override this or we get a really strange
			// error
			// http://bugs.jython.org/issue1551
			return this;
		}

		@Override
		public String toString() {
			return "castor " + owner.toString();
		}
	}

	private static final class DerivedArray extends PyTypeDerived {
		private final Variable owner;

		private DerivedArray(PyType subtype, Variable owner) {
			super(subtype);
			this.owner = owner;
		}

		@Override
		public PyObject __findattr_ex__(String name) {
			NamedVariable var = findVar(name);
			if (var != null) {
				return Py.getAdapter().adapt(var);
			} else {
				return null;
			}
		}

		@Override
		public int __len__() {
			return owner.asArray().length;
		}

		@Override
		public PyObject __iter__() {
			return new PyIterator() {

				int index = 0;
				final Variable[] vars = owner.asArray();

				@Override
				public PyObject __iternext__() {
					if (vars.length <= index) {
						return null;
					} else {
						return Py.getAdapter().adapt(vars[index++]);
					}
				}
			};
		}

		@Override
		public PyObject __sub__(PyObject arg0) {
			return super.__sub__(arg0);
		}

		@Override
		public PyObject __getitem__(int key) {
			return super.__getitem__(key);
		}

		@Override
		public PyObject __finditem__(int arg0) {
			return super.__finditem__(arg0);
		}

		@Override
		public PyObject __finditem__(String key) {
			return super.__finditem__(key);
		}

		@Override
		public void __setitem__(PyObject key, PyObject value) {
			if (key instanceof PyInteger) {
				final Variable raw = owner.asArray()[key.asInt()];
				setVar(raw, value);
				return;
			}
			super.__setitem__(key, value);
		}

		@Override
		public PyObject __finditem__(PyObject arg0) {
			if (arg0 instanceof PyInteger) {
				final Variable raw = owner.asArray()[arg0.asInt()];
				return Py.getAdapter().adapt(raw);
			}
			return super.__finditem__(arg0);
		}

		private NamedVariable findVar(String name) {
			// for (NamedVariable var : owner.getVariables()) {
			// if (var.getName().equals(name)) {
			// return var;
			// }
			// }
			return null;
		}

		@Override
		public void __setattr__(String name, PyObject value) {
			NamedVariable var = findVar(name);
			if (var != null) {
				setVar(var, value);
			}
		}

		@Override
		protected synchronized Object getJavaProxy() {
			// Have to override this or we get a really strange
			// error
			// http://bugs.jython.org/issue1551
			return this;
		}

		@Override
		public String toString() {
			return "arraywraoppoerr " + owner.toString();
		}
	}

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
				final PyType ret = new DerivedState(PyType.fromClass(
						owner.getClass(), true), owner);
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
				case ARRAY:
					ret = new DerivedArray(PyType.fromClass(owner.getClass(),
							true), owner);
					// ret = Py.getAdapter().adapt(owner.asArray());
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

	private static Variable toVariable(Object var) {

		if (var instanceof Number) {
			if (var instanceof Integer) {
				return new IntVariable(((Number) var).intValue());
			} else {
				return new FloatVariable(((Number) var).floatValue());
			}
		} else if (var instanceof String) {
			return new StringVariable((String) var);
		} else if (var instanceof PyObject) {
			PyObject po = (PyObject) var;

			if (po instanceof PyInteger) {
				return new IntVariable(po.asInt());
			} else if (po.isNumberType()) {
				return new FloatVariable((float) po.asDouble());
			} else if (po instanceof PyList) {
				PyList pyList = (PyList) po;
				final int len = pyList.size();
				final Variable[] arr = new Variable[len];
				for (int i = 0; i < arr.length; i++) {
					final Object obj = pyList.get(i);
					final Variable conv = toVariable(obj);
					arr[i] = conv;
				}
				return new ArrayVariable(arr);
			} else {
				return new StringVariable(po.asString());
			}
		}

		throw Py.RuntimeError("Unknonwn type \"" + var + "\"");
	}

	private static void setVar(final Variable owner, PyObject value) {
		owner.copy(toVariable(value));
	}

	public PythonScript(String name, Streamable src) {
		super(name);
		mSrc = src;
	}

	@Override
	public CompiledScript compile(GameState game, Entity owner)
			throws CompileException {
		return new Instance(game, owner);
	}

	private class Instance extends BaseScriptInstance {

		private Entity mOwner;
		private final List<Updateable> mTickers;

		final List<OwnedState> allReachableStates;

		private final GameState mGame;

		public Instance(GameState game, Entity owner) throws CompileException {
			mGame = game;
			mOwner = owner;
			mTickers = new ArrayList<>();
			allReachableStates = new ArrayList<>();
			reload();
		}

		private void reload() throws CompileException {

			try {
				allReachableStates.clear();
				allReachableStates
						.addAll(StateUtil.recursiveFindStates(mOwner));

				try (PythonInterpreter pi = new PythonInterpreter()) {
					pi.set("kge", Py.getAdapter().adapt(Kge.getInstance()));
					pi.set("owner",
							Py.getAdapter().adapt(
									new ScriptAccessor(mOwner,
											PythonScript.this, this)));

					try (InputStream in = mSrc.read()) {
						pi.execfile(in, mSrc.toString());
					}
					PyObject createFunc = pi.eval("create");
					createFunc.__call__();
				}
			} catch (Exception e) {
				Thread.dumpStack();
				e.printStackTrace();
				throw new CompileException(e);
			}
		}

		@Override
		public void tick() {
			for (int i = 0; i < mTickers.size(); i++) {
				if (!mTickers.get(i).tick(Kge.getInstance().time.dt)) {
					mTickers.remove(i);
					i--;
				}
			}
		}

		public class ScriptAccessor {

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
				for (Updateable upd : mInstance.mTickers) {
					if (upd instanceof Interpolator) {
						Interpolator interpolator = (Interpolator) upd;
						if (interpolator.mSetter.equals(function)) {
							interpolator.callSetter(1f);
							mInstance.mTickers.remove(upd);
						}
						return;
					}
				}
			}

			public PyObject getState(String name) {
				for (OwnedState state : allReachableStates) {
					if (state.state.getName().equals(name)) {
						return Py.getAdapter().adapt(state.state);
					}
				}
				throw Py.RuntimeError("No such state \"" + name + "\"");
			}

			public void scheduleUpdate(PyFunction func) {
				System.out.println("Scheduled update on " + func);
				mTickers.add(new Updateable() {

					@Override
					public boolean tick(float dt) {
						try {
							func.__call__();
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							Thread.dumpStack();
							return false;
						}
					}
				});
			}

			public PyObject getEntitiesWithType(String type) {
				final List<Entity> entities = mGame.getEntitiesByClass(type);
				final PyObject[] ret = new PyObject[entities.size()];

				for (int i = 0; i < ret.length; i++) {
					final Entity ent = entities.get(i);
					ret[i] = Py.getAdapter().adapt(new EntityStateGetter(ent));
				}
				return new PyList(ret);
			}

			public class EntityStateGetter {

				private final Entity mEntity;
				private final List<OwnedState> mEntityStates;

				EntityStateGetter(Entity ent) {
					mEntity = ent;
					mEntityStates = StateUtil.recursiveFindStates(ent);
				}

				public PyObject getState(String name) {
					for (OwnedState state : mEntityStates) {
						if (state.state.getName().equals(name)) {
							return Py.getAdapter().adapt(state.state);
						}
					}
					throw Py.RuntimeError("No such state \"" + name + "\"");
				}

				@Override
				public String toString() {
					return "StateGetter for " + mEntity;
				}
			}

			public void addChangeListener(PyObject state, PyFunction callback) {
				if (!(state instanceof DerivedState)) {
					throw Py.RuntimeError("Invalid state '" + state
							+ "', can't listen to it for changes");
				}
				final DerivedState der = (DerivedState) state;

				if (!(der.owner instanceof ObservableState)) {
					throw Py.RuntimeError("State '" + state
							+ "' isn't obserable");
				}

				final ObservableState actualState = (ObservableState) der.owner;
				for (Updateable upd : mInstance.mTickers) {
					if (upd instanceof StateChangeListener
							&& ((StateChangeListener) upd).actualState
									.equals(actualState)) {
						((StateChangeListener) upd).cancelled = true;
					}
				}

				mInstance.mTickers.add(new StateChangeListener(actualState,
						callback));
			}

			private class StateChangeListener implements Updateable {
				private final ObservableState actualState;
				long lastChanged;
				boolean cancelled = false;
				private final PyFunction callback;

				private StateChangeListener(ObservableState actualState,
						PyFunction callback) {
					this.actualState = actualState;
					this.callback = callback;
					lastChanged = actualState.getLastModified();
				}

				@Override
				public boolean tick(float dt) {
					if (cancelled) {
						return false;
					}

					final long newMod = actualState.getLastModified();
					if (lastChanged == newMod) {
						return true;
					}
					lastChanged = newMod;
					try {
						callback.__call__();
						lastChanged = actualState.getLastModified();
						return true;
					} catch (Exception e) {
						Thread.dumpStack();
						e.printStackTrace();
						return false;
					}
				}
			}

			public PyObject createPhysicsBody(PyDictionary attrs) {

				return null;
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
						start[i] = ((Number) startTuple.get(i)).floatValue();
						end[i] = ((Number) endTuple.get(i)).floatValue();
					}
				} else {
					start = new float[1];
					end = new float[1];

					start[0] = ((Number) startVal).floatValue();
					end[0] = ((Number) endVal).floatValue();
				}

				for (Updateable upd : mInstance.mTickers) {
					if (upd instanceof Interpolator) {
						final Interpolator it = (Interpolator) upd;
						if (it.mSetter.equals(callback)) {
							if (Arrays.equals(it.mMax, end)) {
								System.out
										.println("Same end goal as existing interpolator. Not doing anything");
								return;
							}
							((Interpolator) upd).cancel();
						}

					}
				}

				mInstance.mTickers
						.add(new Interpolator(start, end, ((Number) duration)
								.floatValue(), (PyFunction) callback));
			}
		}
	}

	public interface Updateable {
		boolean tick(float dt);
	}

	private static class Interpolator implements Updateable {
		private float[] mMin;
		private float[] mMax;
		private PyFunction mSetter;

		private float mPassedTime, mDuration;
		final PyFloat[] mCallTmp;

		private boolean isCancelled;

		public Interpolator(float[] min, float[] max, float duration,
				PyFunction setter) {
			mMin = min;
			mMax = max;
			mCallTmp = new PyFloat[mMin.length];
			mSetter = setter;
			mDuration = duration;
			isCancelled = false;
		}

		public boolean tick(float dt) {
			if (isCancelled) {
				return false;
			}
			mPassedTime += dt;

			final float rel = mPassedTime
					/ (mDuration == 0f ? 0.001f : mDuration);

			if (rel >= 1f) {
				callSetter(1f);
				return false;
			} else {
				callSetter(rel);
				return true;
			}
		}

		void cancel() {
			isCancelled = true;
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
