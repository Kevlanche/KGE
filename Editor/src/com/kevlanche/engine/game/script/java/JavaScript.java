package com.kevlanche.engine.game.script.java;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.script.BaseScriptInstance;
import com.kevlanche.engine.game.script.Script;
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

	public JavaScript() {
	}

	protected interface Acessor {
		void setValue(Object value);

		Object getValue();
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

	protected abstract class IntAccessor implements Acessor {

		@Override
		public void setValue(Object value) {
			setInt(toInt(value));
		}

		protected abstract void setInt(Integer value);
	}

	public class Instance extends BaseScriptInstance {

		private final List<BoundVar> mVars = new ArrayList<>();

		public Instance() {
			for (ScriptVariable sv : JavaScript.this.getVariables()) {
				for (BoundVar bv : mVars) {
					if (bv.var == sv) {
						bv.accessor.setValue(JavaScript.this.get(sv));
					}
				}
			}

		}

		protected int bindInt(ScriptVariable var, IntAccessor acc) {
			mVars.add(new BoundVar(var, acc));
			return toInt(JavaScript.this.get(var));
		}

		@Override
		public Object getValue(ScriptVariable sv) {
			for (BoundVar var : mVars) {
				if (var.var == sv) {
					return var.accessor.getValue();
				}

			}
			System.err.println("No accessor for " + sv);
			return "0";
		}

		@Override
		public void reset(ScriptVariable var) {
			for (BoundVar bv : mVars) {
				if (bv.var == var) {
					bv.accessor.setValue(JavaScript.this.get(var));
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
