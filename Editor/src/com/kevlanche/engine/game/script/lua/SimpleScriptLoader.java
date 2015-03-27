package com.kevlanche.engine.game.script.lua;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp.AnonFuncDef;
import org.luaj.vm2.ast.Exp.BinopExp;
import org.luaj.vm2.ast.Exp.Constant;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

import com.kevlanche.engine.game.script.ScriptOwner;
import com.kevlanche.engine.game.script.ValueType;
import com.kevlanche.engine.game.script.var.ScriptVariable;

public class SimpleScriptLoader {

	public interface Streamable {
		InputStream read() throws IOException;
	}

	public static List<ScriptVariable> getVariables(Streamable stream)
			throws IOException {
		try {
			final LuaParser parser;
			final Chunk chunk;
			try (InputStream is = stream.read()) {
				parser = new LuaParser(is);
				chunk = parser.Chunk();
			}

			final Map<String, Integer> integers = new HashMap<>();
			final Map<String, String> strings = new HashMap<>();
			// Print out line info for all function definitions.
			chunk.accept(new Visitor() {
				public void visit(AnonFuncDef exp) {
					System.out.println("Anonymous function definition at "
							+ exp.beginLine + "." + exp.beginColumn + ","
							+ exp.endLine + "." + exp.endColumn);
				}

				public void visit(FuncDef stat) {
					System.out.println("Function definition '"
							+ stat.name.name.name + "' at " + stat.beginLine
							+ "." + stat.beginColumn + "," + stat.endLine + "."
							+ stat.endColumn);

					System.out.println("\tName location " + stat.name.beginLine
							+ "." + stat.name.beginColumn + ","
							+ stat.name.endLine + "." + stat.name.endColumn);
				}

				public void visit(LocalFuncDef stat) {
					System.out.println("Local function definition '"
							+ stat.name.name + "' at " + stat.beginLine + "."
							+ stat.beginColumn + "," + stat.endLine + "."
							+ stat.endColumn);
				}

				@Override
				public void visit(Assign stat) {
					for (int i = 0; i < stat.vars.size(); i++) {
						final Object lhs = stat.vars.get(0);
						final Object rhs = stat.exps.get(0);

						if (lhs instanceof NameExp && rhs instanceof BinopExp) {
							final NameExp name = (NameExp) lhs;
							final BinopExp val = (BinopExp) rhs;

							if (val.lhs instanceof NameExp
									&& val.rhs instanceof Constant) {
								final NameExp rhsName = (NameExp) val.lhs;
								final Constant rhsConst = (Constant) val.rhs;

								if (rhsConst.value.isinttype()) {
									integers.put(rhsName.name.name,
											rhsConst.value.checkint());
								} else if (rhsConst.value.isstring()) {
									strings.put(rhsName.name.name,
											rhsConst.value.checkjstring());
								}
							}
						}
						System.out.println(stat.vars.get(0) + " = "
								+ stat.exps.get(0));
					}
					super.visit(stat);
				}

			});

			System.out.println("Found vars: ");
			System.out.println(integers);
			System.out.println(strings);

			final List<ScriptVariable> vars = new ArrayList<ScriptVariable>();
			for (final Entry<String, Integer> ent : integers.entrySet()) {
				vars.add(new ScriptVariable() {

					@Override
					public ValueType getType() {
						return ValueType.INTEGER;
					}

					@Override
					public String getName() {
						return ent.getKey();
					}

					@Override
					public Object getDefaultValue() {
						return ent.getValue();
					}
				});
			}
			for (final Entry<String, String> ent : strings.entrySet()) {
				vars.add(new ScriptVariable() {

					@Override
					public ValueType getType() {
						return ValueType.STRING;
					}

					@Override
					public String getName() {
						return ent.getKey();
					}

					@Override
					public Object getDefaultValue() {
						return ent.getValue();
					}
				});
			}
			return vars;
		} catch (ParseException e) {
			try {
				System.out.println("parse failed: " + e.getMessage() + "\n"
						+ "Token Image: '" + e.currentToken.image + "'\n"
						+ "Location: " + e.currentToken.beginLine + ":"
						+ e.currentToken.beginColumn + "-"
						+ e.currentToken.endLine + ","
						+ e.currentToken.endColumn);
			} catch (Exception leSigh) {
				System.out.println("parse failed: " + e);
			}
			throw new IOException(stream + " le sucks");
		}
	}

	public static LuaScript.Instance load(LuaScript parent, ScriptOwner ctx,
			Streamable stream) throws IOException {
		final Globals g = JsePlatform.standardGlobals();

		g.load(new KgeBinding());
		try (InputStream is = stream.read()) {
			g.load(new InputStreamReader(is), String.valueOf(stream)).call();

			return new LuaScript.Instance(parent, ctx, g, getVariables(stream));
		}
	}

	private static String read(InputStream resourceAsStream) {
		try (final Scanner sc = new Scanner(resourceAsStream)) {
			return sc.useDelimiter("\\Z").next();
		}
	}

	private static class KgeBinding extends TwoArgFunction {

		public KgeBinding() {
		}

		public LuaValue call(LuaValue modname, LuaValue env) {
			LuaValue library = tableOf();
			library.set("debug", new sinh());
			// library.set("cosh", new cosh());
			env.set("kge", library);
			env.get("package").get("loaded").set("kge", library);
			return library;
		}

		static class sinh extends OneArgFunction {
			public LuaValue call(LuaValue x) {
				System.out.println("Called w/ " + x);
				return LuaValue.valueOf(5);
				// return LuaValue.valueOf(Math.sinh(x.checkdouble()));
			}
		}

		static class cosh extends OneArgFunction {
			public LuaValue call(LuaValue x) {
				return LuaValue.valueOf(Math.cosh(x.checkdouble()));
			}
		}
	}
}
