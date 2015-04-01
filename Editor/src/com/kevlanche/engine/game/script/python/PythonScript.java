package com.kevlanche.engine.game.script.python;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyTableCode;
import org.python.core.PyTuple;
import org.python.core.PyType;
import org.python.core.PyTypeDerived;
import org.python.core.adapter.PyObjectAdapter;
import org.python.util.PythonInterpreter;

import com.kevlanche.engine.editor.Editor.Streamable;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.script.BaseScript;
import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;
import com.kevlanche.engine.game.script.var.IntVariable;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class PythonScript extends BaseScript {

	protected final Streamable mSrc;

	private final List<PyVar> mVars;

	// private String mClsName;

	static {
		PySystemState.initialize();
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof ScriptOwner;
			}

			@Override
			public PyObject adapt(Object o) {
				final ScriptOwner owner = (ScriptOwner) o;
				final PyType ret = new PyTypeDerived(PyType.fromClass(
						owner.getClass(), true)) {

					@Override
					public PyObject __findattr_ex__(String name) {
						return Py.getAdapter().adapt(owner.get(name));
					}
				};
				return ret;
			}
		});
		Py.getAdapter().addPreClass(new PyObjectAdapter() {

			@Override
			public boolean canAdapt(Object o) {
				return o instanceof ScriptInstance;
			}

			@Override
			public PyObject adapt(Object o) {
				final ScriptInstance owner = (ScriptInstance) o;
				final PyType ret = new PyTypeDerived(PyType.fromClass(
						owner.getClass(), true)) {

					@Override
					public PyObject __findattr_ex__(String name) {
						for (ScriptVariable var : owner.getSource()
								.getVariables()) {
							if (var.getName().equals(name)) {
								return Py.getAdapter().adapt(
										owner.getValue(var));
							}
						}
						return super.__findattr_ex__(name);
					}

					@Override
					public void __setattr__(String name, PyObject value) {
						for (ScriptVariable var : owner.getSource()
								.getVariables()) {
							if (var.getName().equals(name)) {

								switch (var.getType()) {
								case FLOAT:
									owner.setValue(var,
											(float) value.asDouble());
									break;
								case INTEGER:
									owner.setValue(var, value.asInt());
									break;
								case STRING:
									owner.setValue(var, value.asString());
								}
								return;
							}
						}
						// super.__setattr__(name, value);
					}
				};
				return ret;
			}
		});
	}

	private PythonInterpreter loadDefault() throws IOException {
		PythonInterpreter pi = new PythonInterpreter();

		pi.set("kge", Py.getAdapter().adapt(Kge.getInstance()));
		try (InputStream in = mSrc.read()) {
			pi.execfile(in, mSrc.toString());
		}
		return pi;
	}

	public PythonScript(Streamable src) throws IOException {
		mSrc = src;
		mVars = new ArrayList<>();

		// final Properties props = new Properties();
		//
		// PythonInterpreter.initialize(System.getProperties(), props, new
		// String[0]);
		PythonInterpreter pi = loadDefault();
		// pi.exec("import sys");
		// pi.exec("sys.path.append(\"C:\\Users\\Anton\\KGE\\Editor\\libs\\Lib\")");

		PyObject funcObj = pi.get("create");

		if (funcObj == null || !(funcObj instanceof PyFunction)) {
			throw new IOException("Must define create!");
		}

		PyFunction func = (PyFunction) funcObj;
		PyTableCode pyTableCode = (PyTableCode) func.__code__;
		String[] names = pyTableCode.co_varnames;
		PyTuple defs = (PyTuple) func.getDefaults();

		if (defs.size() != pyTableCode.co_argcount - 1) {
			throw new IOException("All vars must have a default value");
		}
		for (int i = 0; i < defs.size(); i++) {
			Object obj = defs.get(i);

			if (obj instanceof PyDictionary) {
				throw new IOException("TODO add min/max support");
			}
			if (obj instanceof Integer) {
				mVars.add(new PyVar(
						new IntVariable(names[i + 1], (Integer) obj)));
			} else {
				throw new IOException("Unknown type on \"" + obj + "\": "
						+ obj.getClass());
			}
			System.out.println(obj);
		}

		// PyStringMap psm = (PyStringMap) pi.getLocals();
		// PyObject bl = pi.getSystemState().getBuiltins();
		// PyList list = psm.items();
		// PyType mainCls = null;
		// for (int i = 0; i < list.size(); i++) {
		// PyTuple item = (PyTuple) list.get(i);
		// final Object key = item.get(0);
		// final Object val = item.get(1);
		//
		// if (key instanceof String && val instanceof PyType) {
		//
		// final PyType type = (PyType) val;
		// PyObject bajs = type.getBase();
		// System.out.println("found class " + key);
		// System.out.println("base = " + bajs);
		// if (((PyType) bajs).getName().equals("KgeScript")) {
		// System.out.println("THIS IS IT");
		// PyDictProxy dict = (PyDictProxy) type.getDict();
		// final PyFunction constr = (PyFunction) dict.dictproxy_get(
		// new PyString("__init__"), null);
		// System.out.println("hmm");
		// PyTableCode ptc = (PyTableCode) constr.__code__;
		//
		// mClsName = type.getName();
		//
		// String[] varnames = ptc.co_varnames;
		//
		// if (varnames.length != constr.__defaults__.length + 1) {
		// throw new IOException(
		// "All arguments (except 'self') must have an optional attribute");
		// }
		// for (int j = 1; j < varnames.length; j++) {
		// final String name = varnames[j];
		// final PyObject value = constr.__defaults__[j - 1];
		//
		// if (value instanceof PyInteger) {
		// mVars.add(new PyVar(new IntVariable(name,
		// ((PyInteger) value).asInt())));
		// } else {
		// throw new IOException("Invalid type on " + value);
		// }
		// }
		//
		// // PyObject cons = pi.eval(type.getName() +"()");
		// // System.out.println(cons);
		// }
		// // mVars.add(new PyVar(new IntVariable(key.toString(),
		// // (Integer) val)));
		// }
		// System.out.println(i + " = " + item);
		// }
	}

	@Override
	public ScriptInstance createInstance(ScriptOwner owner) {

		// final StringBuilder cmd = new StringBuilder(mClsName + "(");
		// boolean first = true;
		// for (PyVar pyVar : mVars) {
		// if (pyVar.value != null) {
		// cmd.append(pyVar.value.toString());
		// } else {
		// cmd.append(pyVar.var.getDefaultValue());
		// }
		// if (!first) {
		// cmd.append(",");
		// }
		// first = false;
		//
		// }
		// cmd.append(")");
		//
		// try (InputStream in = mSrc.read()) {
		// pi.execfile(in, mSrc.toString());
		// } catch (IOException e) {
		// e.printStackTrace();
		// throw new IllegalArgumentException(e);
		// }
		// PyObject created = pi.eval(cmd.toString());
		return new Instance(owner);
	}

	@Override
	public void set(ScriptVariable variable, Object value) {
		for (PyVar var : mVars) {
			if (var.var == variable) {
				var.value = value;
				return;
			}
		}
	}

	@Override
	public Object get(ScriptVariable variable) {
		for (PyVar var : mVars) {
			if (var.var == variable) {
				if (var.value != null) {
					return var.value;
				} else {
					return var.var.getDefaultValue();
				}
			}
		}
		throw new IllegalArgumentException("No such var (" + variable + ")");
	}

	@Override
	public List<ScriptVariable> getVariables() {
		final List<ScriptVariable> ret = new ArrayList<>();
		for (PyVar var : mVars) {
			ret.add(var.var);
		}
		return ret;
	}

	protected int toInt(Object value) {
		if (value instanceof Integer) {
			return ((Integer) value);
		} else {
			try {
				return (Integer.parseInt(String.valueOf(value)));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				return 0;
			}
		}
	}

	private class Instance extends BaseScriptInstance {

		private ScriptOwner mOwner;
		private PyObject mInterpreter;

		public Instance(ScriptOwner owner) {
			mOwner = owner;
			mInterpreter = reload();
		}

		private PyObject reload() {
			PythonInterpreter pi;

			try {
				pi = loadDefault();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
			final PyObject[] args = new PyObject[mVars.size() + 1];
			args[0] = Py.getAdapter().adapt(mOwner);

			for (int i = 0; i < args.length - 1; i++) {
				final PyVar pyVar = mVars.get(i);

				switch (pyVar.var.getType()) {
				case INTEGER:
				default:
					args[i + 1] = new PyInteger(
							toInt(pyVar.value != null ? pyVar.value
									: pyVar.var.getDefaultValue()));
				}
			}

			PyFunction create = (PyFunction) pi.get("create");
			return create.__call__(args);
		}

		@Override
		public Script getSource() {
			return PythonScript.this;
		}

		@Override
		public Object getValue(ScriptVariable var) {
			return PythonScript.this.get(var);
			// return mInterpreter.__findattr__(var.getName());
		}

		@Override
		public void reset(ScriptVariable var) {
			mInterpreter = reload();
		}

		@Override
		public void update() {
			try {
				if (mInterpreter instanceof PyDictionary) {
					Object updateFunc = ((PyDictionary) mInterpreter)
							.get("update");
					if (!(updateFunc instanceof PyFunction)) {
						System.out.println("no update declared :(");
						return;
					}
					final PyFunction update = (PyFunction) updateFunc;
					update.__call__();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void saveState() {

		}

		@Override
		public void setValue(ScriptVariable var, Object value) {
			getSource().set(var, value);
			mInterpreter = reload();
		}
	}

	private class PyVar {
		public final ScriptVariable var;
		public Object value;

		public PyVar(ScriptVariable var) {
			this.var = var;
		}
	}
}
