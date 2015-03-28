package com.kevlanche.engine.game.script.lua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;
import com.kevlanche.engine.game.script.lua.SimpleScriptLoader.Streamable;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class LuaScript implements Script {

	private final Streamable mSrc;
	private final Map<String, Object> mCustomValues;
	private final List<ScriptVariable> mVariables;

	public LuaScript(Streamable src) throws IOException {
		mSrc = src;
		mVariables = SimpleScriptLoader.getVariables(src);
		mCustomValues = new HashMap<String, Object>();
	}

	@Override
	public void set(ScriptVariable variable, Object value) {
		mCustomValues.put(variable.getName(), value);
	}

	@Override
	public List<ScriptVariable> getVariables() {
		return mVariables;
	}

	@Override
	public Object get(ScriptVariable variable) {
		return mCustomValues.get(variable.getName());
	}

	@Override
	public Instance createInstance(ScriptOwner ctx) {
		try {
			Instance ret = SimpleScriptLoader.load(this, ctx, mSrc);
			for (ScriptVariable var : getVariables()) {

				set(ret, var);
			}
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void set(Instance ret, ScriptVariable var) {
		final Object val = mCustomValues.getOrDefault(var.getName(),
				var.getDefaultValue());

		System.out.println("Setting " + var.getName() + " = " + val);

		switch (var.getType()) {
		case INTEGER:
			try {
				if (val instanceof Integer) {
					ret.mGlobals.set(var.getName(), (Integer) val);
				} else {
					ret.mGlobals.set(var.getName(),
							Integer.parseInt(val.toString()));
				}
			} catch (RuntimeException e) {
				ret.mGlobals.set(var.getName(), Integer.parseInt(var
						.getDefaultValue().toString()));
			}

			break;
		case STRING:
			ret.mGlobals.set(var.getName(),
					LuaValue.valueOf(val.toString()));
			break;
		}
	}

	static class Instance extends BaseScriptInstance {

		int modCount;
		int lastModCount;

		Globals mGlobals;

		private LuaScript mParent;

		private ScriptOwner mContext;

		Instance(LuaScript parent, ScriptOwner context, Globals g,
				List<ScriptVariable> vars) {
			mGlobals = g;
			mContext = context;
			mParent = parent;

			try {
				mGlobals.get("onCreate").call(new LuaValueProxy(context) {

					private final LuaValue fallback = CoerceJavaToLua
							.coerce(context);

					private LuaValue thisCoerced = CoerceJavaToLua.coerce(this);

					@Override
					public LuaValue get(LuaValue key) {
						if (key instanceof LuaString
								&& key.toString().equals("install")) {

							return new LuaValueProxy(this) {

								public LuaValue call(LuaValue arg1,
										LuaValue arg2) {
									context.installComponent(arg2.toString(),
											mGlobals);
									return null;
								};
							};
						}
						final Object def = context.get(key.toString());
						if (def != null) {
							return (LuaValue) def;
						}
						return fallback.get(key);
					}

				});
			} catch (LuaError e) {
				e.printStackTrace();
				// No onCreate function defined. No probz
			}

		}

		@Override
		public LuaScript getSource() {
			return mParent;
		}

		@Override
		public Object getValue(ScriptVariable var) {
			return mGlobals.get(LuaValue.valueOf(var.getName()));
		}

		@Override
		public void update(float dt) {
			try {
				mGlobals.get("update").call(LuaValue.valueOf(dt));
			} catch (LuaError e) {
				e.printStackTrace();
			}
		}

		@Override
		public void reset(ScriptVariable var) {
			mParent.set(this, var);		
		}
	}
}