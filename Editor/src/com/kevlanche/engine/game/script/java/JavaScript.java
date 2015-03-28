package com.kevlanche.engine.game.script.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;
import com.kevlanche.engine.game.script.var.IntVariable;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public abstract class JavaScript implements Script {

	private class BoundVar {
		public final ScriptVariable var;
		public final Acessor accessor;

		public BoundVar(ScriptVariable var, Acessor accessor) {
			this.var = var;
			this.accessor = accessor;
		}

	}

	private final List<BoundVar> mVars = new ArrayList<>();
	Map<ScriptVariable, Object> values = new HashMap<>();
	private final Class<? extends ScriptInstance> mInstanceClass;

	public JavaScript(Class<? extends ScriptInstance> instanceClass) {
		mInstanceClass = instanceClass;
	}

	@Override
	public List<ScriptVariable> getVariables() {
		final List<ScriptVariable> ret = new ArrayList<>();
		for (BoundVar bv : mVars) {
			ret.add(bv.var);
		}
		return ret;
	}

	@Override
	public void set(ScriptVariable variable, Object value) {
		values.put(variable, value);
	}

	@Override
	public Object get(ScriptVariable variable) {
		Object setVar = values.get(variable);
		return setVar == null ? variable.getDefaultValue() : setVar;
	}

	@Override
	public ScriptInstance createInstance(ScriptOwner context) {
		try {
			return mInstanceClass.getDeclaredConstructor(getClass())
					.newInstance(this);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	protected ScriptVariable registerVar(String name, Object defaultValue) {
		try {
			final Field field = mInstanceClass.getDeclaredField(name);
			field.setAccessible(true);

			final String typeName = field.getType().getName();
			final Acessor ac = new Acessor() {

				@Override
				public void setValue(Object target, Object value) {
					try {
						field.set(target, value);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						throw new IllegalArgumentException(e);
					}
				}

				@Override
				public Object getValue(Object target) {
					try {
						return field.get(target);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						throw new IllegalArgumentException(e);
					}
				}
			};
			final ScriptVariable var;
			if (typeName.equals("int")) {
				var = new IntVariable(name, toInt(defaultValue));
			} else {
				throw new IllegalArgumentException("Illegal type \"" + typeName
						+ "\" on field \"" + name + "\"");
			}
			final BoundVar bv = new BoundVar(var, ac);
			mVars.add(bv);
			return bv.var;
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	protected interface Acessor {
		void setValue(Object target, Object value);

		Object getValue(Object target);
	}

	private int toInt(Object value) {
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

	public class Instance extends BaseScriptInstance {

		public Instance() {
			for (ScriptVariable sv : JavaScript.this.getVariables()) {
				for (BoundVar bv : mVars) {
					if (bv.var == sv) {
						bv.accessor.setValue(this, JavaScript.this.get(sv));
					}
				}
			}

		}

		@Override
		public Object getValue(ScriptVariable sv) {
			for (BoundVar var : mVars) {
				if (var.var == sv) {
					return var.accessor.getValue(this);
				}

			}
			System.err.println("No accessor for " + sv);
			return "0";
		}

		@Override
		public void reset(ScriptVariable var) {
			for (BoundVar bv : mVars) {
				if (bv.var == var) {
					bv.accessor.setValue(this, JavaScript.this.get(var));
				}
			}
		}
		
		@Override
		public Script getSource() {
			return JavaScript.this;
		}

		@Override
		public void update(float dt) {

		}
	}
}
