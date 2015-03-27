package com.kevlanche.engine.game.script.lua;

import org.luaj.vm2.Buffer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaValueProxy extends LuaValue {

	protected final LuaValue proxyTarget;

	private LuaValue thisCoerced;

	LuaValueProxy(Object fallback) {
		this.proxyTarget = CoerceJavaToLua.coerce(fallback);
		thisCoerced = CoerceJavaToLua.coerce(this);
	}

	@Override
	public LuaValue get(LuaValue key) {
		return proxyTarget.get(key);
	}

	@Override
	public LuaValue getmetatable() {
		return proxyTarget.getmetatable();
	}

	@Override
	public void set(LuaValue key, LuaValue value) {
		proxyTarget.set(key, value);
	}

	@Override
	public String typename() {
		return proxyTarget.typename();
	}

	@Override
	public int type() {
		return proxyTarget.type();
	}

	@Override
	public Object optuserdata(Object defval) {
		return proxyTarget.optuserdata(defval);
	}

	@Override
	public Object optuserdata(Class c, Object defval) {
		return proxyTarget.optuserdata(c, defval);
	}

	@Override
	public Object optuserdata(int i, Class c, Object defval) {
		return proxyTarget.optuserdata(i, c, defval);
	}

	@Override
	public Object optuserdata(int i, Object defval) {

		return proxyTarget.optuserdata(i, defval);
	}

	@Override
	public boolean isboolean() {

		return proxyTarget.isboolean();
	}

	@Override
	public boolean isclosure() {

		return proxyTarget.isclosure();
	}

	@Override
	public boolean isfunction() {

		return proxyTarget.isfunction();
	}

	@Override
	public boolean isint() {

		return proxyTarget.isint();
	}

	@Override
	public boolean isinttype() {

		return proxyTarget.isinttype();
	}

	@Override
	public boolean islong() {

		return proxyTarget.islong();
	}

	@Override
	public boolean isnil() {

		return proxyTarget.isnil();
	}

	@Override
	public boolean isnumber() {

		return proxyTarget.isnumber();
	}

	@Override
	public boolean isstring() {

		return proxyTarget.isstring();
	}

	@Override
	public boolean isthread() {

		return proxyTarget.isthread();
	}

	@Override
	public boolean istable() {

		return proxyTarget.istable();
	}

	@Override
	public boolean isuserdata() {

		return proxyTarget.isuserdata();
	}

	@Override
	public boolean isuserdata(Class c) {

		return proxyTarget.isuserdata(c);
	}

	@Override
	public boolean toboolean() {

		return proxyTarget.toboolean();
	}

	@Override
	public byte tobyte() {

		return proxyTarget.tobyte();
	}

	@Override
	public char tochar() {

		return proxyTarget.tochar();
	}

	@Override
	public double todouble() {

		return proxyTarget.todouble();
	}

	@Override
	public float tofloat() {

		return proxyTarget.tofloat();
	}

	@Override
	public int toint() {

		return proxyTarget.toint();
	}

	@Override
	public long tolong() {

		return proxyTarget.tolong();
	}

	@Override
	public short toshort() {

		return proxyTarget.toshort();
	}

	@Override
	public String tojstring() {

		return proxyTarget.tojstring();
	}

	@Override
	public Object touserdata() {

		return proxyTarget.touserdata();
	}

	@Override
	public Object touserdata(Class c) {

		return proxyTarget.touserdata(c);
	}

	@Override
	public String toString() {

		return proxyTarget.toString();
	}

	@Override
	public LuaValue tonumber() {

		return proxyTarget.tonumber();
	}

	@Override
	public LuaValue tostring() {

		return proxyTarget.tostring();
	}

	@Override
	public boolean optboolean(boolean defval) {

		return proxyTarget.optboolean(defval);
	}

	@Override
	public LuaClosure optclosure(LuaClosure defval) {

		return proxyTarget.optclosure(defval);
	}

	@Override
	public double optdouble(double defval) {

		return proxyTarget.optdouble(defval);
	}

	@Override
	public LuaFunction optfunction(LuaFunction defval) {

		return proxyTarget.optfunction(defval);
	}

	@Override
	public int optint(int defval) {

		return proxyTarget.optint(defval);
	}

	@Override
	public LuaInteger optinteger(LuaInteger defval) {

		return proxyTarget.optinteger(defval);
	}

	@Override
	public long optlong(long defval) {

		return proxyTarget.optlong(defval);
	}

	@Override
	public LuaNumber optnumber(LuaNumber defval) {

		return proxyTarget.optnumber(defval);
	}

	@Override
	public String optjstring(String defval) {

		return proxyTarget.optjstring(defval);
	}

	@Override
	public LuaString optstring(LuaString defval) {

		return proxyTarget.optstring(defval);
	}

	@Override
	public LuaTable opttable(LuaTable defval) {

		return proxyTarget.opttable(defval);
	}

	@Override
	public LuaThread optthread(LuaThread defval) {

		return proxyTarget.optthread(defval);
	}

	@Override
	public LuaValue optvalue(LuaValue defval) {

		return proxyTarget.optvalue(defval);
	}

	@Override
	public boolean checkboolean() {

		return proxyTarget.checkboolean();
	}

	@Override
	public LuaClosure checkclosure() {

		return proxyTarget.checkclosure();
	}

	@Override
	public double checkdouble() {

		return proxyTarget.checkdouble();
	}

	@Override
	public LuaFunction checkfunction() {

		return proxyTarget.checkfunction();
	}

	@Override
	public Globals checkglobals() {

		return proxyTarget.checkglobals();
	}

	@Override
	public int checkint() {

		return proxyTarget.checkint();
	}

	@Override
	public LuaInteger checkinteger() {

		return proxyTarget.checkinteger();
	}

	@Override
	public long checklong() {

		return proxyTarget.checklong();
	}

	@Override
	public LuaNumber checknumber() {

		return proxyTarget.checknumber();
	}

	@Override
	public LuaNumber checknumber(String msg) {

		return proxyTarget.checknumber(msg);
	}

	@Override
	public String checkjstring() {

		return proxyTarget.checkjstring();
	}

	@Override
	public LuaString checkstring() {

		return proxyTarget.checkstring();
	}

	@Override
	public LuaTable checktable() {

		return proxyTarget.checktable();
	}

	@Override
	public LuaThread checkthread() {

		return proxyTarget.checkthread();
	}

	@Override
	public Object checkuserdata() {

		return proxyTarget.checkuserdata();
	}

	@Override
	public Object checkuserdata(Class c) {

		return proxyTarget.checkuserdata(c);
	}

	@Override
	public LuaValue checknotnil() {

		return proxyTarget.checknotnil();
	}

	@Override
	public boolean isvalidkey() {

		return proxyTarget.isvalidkey();
	}

	@Override
	protected LuaValue argerror(String expected) {

		return super.argerror(expected);
	}

	@Override
	protected LuaValue typerror(String expected) {

		return super.typerror(expected);
	}

	@Override
	protected LuaValue unimplemented(String fun) {

		return super.unimplemented(fun);
	}

	@Override
	protected LuaValue illegal(String op, String typename) {

		return super.illegal(op, typename);
	}

	@Override
	protected LuaValue lenerror() {

		return super.lenerror();
	}

	@Override
	protected LuaValue aritherror() {

		return super.aritherror();
	}

	@Override
	protected LuaValue aritherror(String fun) {

		return super.aritherror(fun);
	}

	@Override
	protected LuaValue compareerror(String rhs) {

		return super.compareerror(rhs);
	}

	@Override
	protected LuaValue compareerror(LuaValue rhs) {

		return super.compareerror(rhs);
	}

	@Override
	public LuaValue get(int key) {

		return proxyTarget.get(key);
	}

	@Override
	public LuaValue get(String key) {

		return proxyTarget.get(key);
	}

	@Override
	public void set(int key, LuaValue value) {

		proxyTarget.set(key, value);
	}

	@Override
	public void set(int key, String value) {

		proxyTarget.set(key, value);
	}

	@Override
	public void set(String key, LuaValue value) {

		proxyTarget.set(key, value);
	}

	@Override
	public void set(String key, double value) {

		proxyTarget.set(key, value);
	}

	@Override
	public void set(String key, int value) {

		proxyTarget.set(key, value);
	}

	@Override
	public void set(String key, String value) {

		proxyTarget.set(key, value);
	}

	@Override
	public LuaValue rawget(LuaValue key) {

		return proxyTarget.rawget(key);
	}

	@Override
	public LuaValue rawget(int key) {

		return proxyTarget.rawget(key);
	}

	@Override
	public LuaValue rawget(String key) {

		return proxyTarget.rawget(key);
	}

	@Override
	public void rawset(LuaValue key, LuaValue value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(int key, LuaValue value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(int key, String value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(String key, LuaValue value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(String key, double value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(String key, int value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawset(String key, String value) {

		proxyTarget.rawset(key, value);
	}

	@Override
	public void rawsetlist(int key0, Varargs values) {

		proxyTarget.rawsetlist(key0, values);
	}

	@Override
	public void presize(int i) {

		proxyTarget.presize(i);
	}

	@Override
	public Varargs next(LuaValue index) {

		return proxyTarget.next(index);
	}

	@Override
	public Varargs inext(LuaValue index) {

		return proxyTarget.inext(index);
	}

	@Override
	public LuaValue load(LuaValue library) {

		return proxyTarget.load(library);
	}

	@Override
	public LuaValue arg(int index) {

		return proxyTarget.arg(index);
	}

	@Override
	public int narg() {

		return proxyTarget.narg();
	}

	@Override
	public LuaValue arg1() {

		return proxyTarget.arg1();
	}

	@Override
	public LuaValue setmetatable(LuaValue metatable) {

		return proxyTarget.setmetatable(metatable);
	}

	@Override
	public LuaValue call() {

		return proxyTarget.call();
	}

	@Override
	public LuaValue call(LuaValue arg) {

		return proxyTarget.call(arg);
	}

	@Override
	public LuaValue call(String arg) {

		return proxyTarget.call(arg);
	}

	@Override
	public LuaValue call(LuaValue arg1, LuaValue arg2) {

		return proxyTarget.call(arg1, arg2);
	}

	@Override
	public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
		return proxyTarget.call(arg1, arg2, arg3);
	}

	@Override
	public LuaValue method(String name) {

		return proxyTarget.method(name);
	}

	@Override
	public LuaValue method(LuaValue name) {

		return proxyTarget.method(name);
	}

	@Override
	public LuaValue method(String name, LuaValue arg) {

		return proxyTarget.method(name, arg);
	}

	@Override
	public LuaValue method(LuaValue name, LuaValue arg) {

		return proxyTarget.method(name, arg);
	}

	@Override
	public LuaValue method(String name, LuaValue arg1, LuaValue arg2) {

		return proxyTarget.method(name, arg1, arg2);
	}

	@Override
	public LuaValue method(LuaValue name, LuaValue arg1, LuaValue arg2) {

		return proxyTarget.method(name, arg1, arg2);
	}

	@Override
	public Varargs invoke() {

		return proxyTarget.invoke();
	}

	@Override
	public Varargs invoke(Varargs args) {

		return proxyTarget.invoke(args);
	}

	@Override
	public Varargs invoke(LuaValue arg, Varargs varargs) {

		return proxyTarget.invoke(arg, varargs);
	}

	@Override
	public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {

		return proxyTarget.invoke(arg1, arg2, varargs);
	}

	@Override
	public Varargs invoke(LuaValue[] args) {

		return proxyTarget.invoke(args);
	}

	@Override
	public Varargs invoke(LuaValue[] args, Varargs varargs) {

		return proxyTarget.invoke(args, varargs);
	}

	@Override
	public Varargs invokemethod(String name) {

		return proxyTarget.invokemethod(name);
	}

	@Override
	public Varargs invokemethod(LuaValue name) {

		return proxyTarget.invokemethod(name);
	}

	@Override
	public Varargs invokemethod(String name, Varargs args) {

		return proxyTarget.invokemethod(name, args);
	}

	@Override
	public Varargs invokemethod(LuaValue name, Varargs args) {

		return proxyTarget.invokemethod(name, args);
	}

	@Override
	public Varargs invokemethod(String name, LuaValue[] args) {

		return proxyTarget.invokemethod(name, args);
	}

	@Override
	public Varargs invokemethod(LuaValue name, LuaValue[] args) {

		return proxyTarget.invokemethod(name, args);
	}

	@Override
	protected LuaValue callmt() {

		return super.callmt();
	}

	@Override
	public LuaValue not() {

		return proxyTarget.not();
	}

	@Override
	public LuaValue neg() {

		return proxyTarget.neg();
	}

	@Override
	public LuaValue len() {

		return proxyTarget.len();
	}

	@Override
	public int length() {

		return proxyTarget.length();
	}

	@Override
	public int rawlen() {

		return proxyTarget.rawlen();
	}

	@Override
	public boolean equals(Object obj) {

		return proxyTarget.equals(obj);
	}

	@Override
	public LuaValue eq(LuaValue val) {

		return proxyTarget.eq(val);
	}

	@Override
	public boolean eq_b(LuaValue val) {

		return proxyTarget.eq_b(val);
	}

	@Override
	public LuaValue neq(LuaValue val) {

		return proxyTarget.neq(val);
	}

	@Override
	public boolean neq_b(LuaValue val) {

		return proxyTarget.neq_b(val);
	}

	@Override
	public boolean raweq(LuaValue val) {

		return proxyTarget.raweq(val);
	}

	@Override
	public boolean raweq(LuaUserdata val) {

		return proxyTarget.raweq(val);
	}

	@Override
	public boolean raweq(LuaString val) {

		return proxyTarget.raweq(val);
	}

	@Override
	public boolean raweq(double val) {

		return proxyTarget.raweq(val);
	}

	@Override
	public boolean raweq(int val) {

		return proxyTarget.raweq(val);
	}

	@Override
	public LuaValue add(LuaValue rhs) {

		return proxyTarget.add(rhs);
	}

	@Override
	public LuaValue add(double rhs) {

		return proxyTarget.add(rhs);
	}

	@Override
	public LuaValue add(int rhs) {

		return proxyTarget.add(rhs);
	}

	@Override
	public LuaValue sub(LuaValue rhs) {

		return proxyTarget.sub(rhs);
	}

	@Override
	public LuaValue sub(double rhs) {

		return proxyTarget.sub(rhs);
	}

	@Override
	public LuaValue sub(int rhs) {

		return proxyTarget.sub(rhs);
	}

	@Override
	public LuaValue subFrom(double lhs) {

		return proxyTarget.subFrom(lhs);
	}

	@Override
	public LuaValue subFrom(int lhs) {

		return proxyTarget.subFrom(lhs);
	}

	@Override
	public LuaValue mul(LuaValue rhs) {

		return proxyTarget.mul(rhs);
	}

	@Override
	public LuaValue mul(double rhs) {

		return proxyTarget.mul(rhs);
	}

	@Override
	public LuaValue mul(int rhs) {

		return proxyTarget.mul(rhs);
	}

	@Override
	public LuaValue pow(LuaValue rhs) {

		return proxyTarget.pow(rhs);
	}

	@Override
	public LuaValue pow(double rhs) {

		return proxyTarget.pow(rhs);
	}

	@Override
	public LuaValue pow(int rhs) {

		return proxyTarget.pow(rhs);
	}

	@Override
	public LuaValue powWith(double lhs) {

		return proxyTarget.powWith(lhs);
	}

	@Override
	public LuaValue powWith(int lhs) {

		return proxyTarget.powWith(lhs);
	}

	@Override
	public LuaValue div(LuaValue rhs) {

		return proxyTarget.div(rhs);
	}

	@Override
	public LuaValue div(double rhs) {

		return proxyTarget.div(rhs);
	}

	@Override
	public LuaValue div(int rhs) {

		return proxyTarget.div(rhs);
	}

	@Override
	public LuaValue divInto(double lhs) {

		return proxyTarget.divInto(lhs);
	}

	@Override
	public LuaValue mod(LuaValue rhs) {

		return proxyTarget.mod(rhs);
	}

	@Override
	public LuaValue mod(double rhs) {

		return proxyTarget.mod(rhs);
	}

	@Override
	public LuaValue mod(int rhs) {

		return proxyTarget.mod(rhs);
	}

	@Override
	public LuaValue modFrom(double lhs) {

		return proxyTarget.modFrom(lhs);
	}

	@Override
	protected LuaValue arithmt(LuaValue tag, LuaValue op2) {

		return super.arithmt(tag, op2);
	}

	@Override
	protected LuaValue arithmtwith(LuaValue tag, double op1) {

		return super.arithmtwith(tag, op1);
	}

	@Override
	public LuaValue lt(LuaValue rhs) {

		return proxyTarget.lt(rhs);
	}

	@Override
	public LuaValue lt(double rhs) {

		return proxyTarget.lt(rhs);
	}

	@Override
	public LuaValue lt(int rhs) {

		return proxyTarget.lt(rhs);
	}

	@Override
	public boolean lt_b(LuaValue rhs) {

		return proxyTarget.lt_b(rhs);
	}

	@Override
	public boolean lt_b(int rhs) {

		return proxyTarget.lt_b(rhs);
	}

	@Override
	public boolean lt_b(double rhs) {

		return proxyTarget.lt_b(rhs);
	}

	@Override
	public LuaValue lteq(LuaValue rhs) {

		return proxyTarget.lteq(rhs);
	}

	@Override
	public LuaValue lteq(double rhs) {

		return proxyTarget.lteq(rhs);
	}

	@Override
	public LuaValue lteq(int rhs) {

		return proxyTarget.lteq(rhs);
	}

	@Override
	public boolean lteq_b(LuaValue rhs) {

		return proxyTarget.lteq_b(rhs);
	}

	@Override
	public boolean lteq_b(int rhs) {

		return proxyTarget.lteq_b(rhs);
	}

	@Override
	public boolean lteq_b(double rhs) {

		return proxyTarget.lteq_b(rhs);
	}

	@Override
	public LuaValue gt(LuaValue rhs) {

		return proxyTarget.gt(rhs);
	}

	@Override
	public LuaValue gt(double rhs) {

		return proxyTarget.gt(rhs);
	}

	@Override
	public LuaValue gt(int rhs) {

		return proxyTarget.gt(rhs);
	}

	@Override
	public boolean gt_b(LuaValue rhs) {

		return proxyTarget.gt_b(rhs);
	}

	@Override
	public boolean gt_b(int rhs) {

		return proxyTarget.gt_b(rhs);
	}

	@Override
	public boolean gt_b(double rhs) {

		return proxyTarget.gt_b(rhs);
	}

	@Override
	public LuaValue gteq(LuaValue rhs) {

		return proxyTarget.gteq(rhs);
	}

	@Override
	public LuaValue gteq(double rhs) {

		return proxyTarget.gteq(rhs);
	}

	@Override
	public LuaValue gteq(int rhs) {

		return proxyTarget.gteq(rhs);
	}

	@Override
	public boolean gteq_b(LuaValue rhs) {

		return proxyTarget.gteq_b(rhs);
	}

	@Override
	public boolean gteq_b(int rhs) {

		return proxyTarget.gteq_b(rhs);
	}

	@Override
	public boolean gteq_b(double rhs) {

		return proxyTarget.gteq_b(rhs);
	}

	@Override
	public LuaValue comparemt(LuaValue tag, LuaValue op1) {

		return proxyTarget.comparemt(tag, op1);
	}

	@Override
	public int strcmp(LuaValue rhs) {

		return proxyTarget.strcmp(rhs);
	}

	@Override
	public int strcmp(LuaString rhs) {

		return proxyTarget.strcmp(rhs);
	}

	@Override
	public LuaValue concat(LuaValue rhs) {

		return proxyTarget.concat(rhs);
	}

	@Override
	public LuaValue concatTo(LuaValue lhs) {

		return proxyTarget.concatTo(lhs);
	}

	@Override
	public LuaValue concatTo(LuaNumber lhs) {

		return proxyTarget.concatTo(lhs);
	}

	@Override
	public LuaValue concatTo(LuaString lhs) {

		return proxyTarget.concatTo(lhs);
	}

	@Override
	public Buffer buffer() {

		return proxyTarget.buffer();
	}

	@Override
	public Buffer concat(Buffer rhs) {

		return proxyTarget.concat(rhs);
	}

	@Override
	public LuaValue concatmt(LuaValue rhs) {

		return proxyTarget.concatmt(rhs);
	}

	@Override
	public LuaValue and(LuaValue rhs) {

		return proxyTarget.and(rhs);
	}

	@Override
	public LuaValue or(LuaValue rhs) {

		return proxyTarget.or(rhs);
	}

	@Override
	public boolean testfor_b(LuaValue limit, LuaValue step) {

		return proxyTarget.testfor_b(limit, step);
	}

	@Override
	public LuaString strvalue() {

		return proxyTarget.strvalue();
	}

	@Override
	public LuaValue strongvalue() {
		return proxyTarget.strongvalue();
	}

	@Override
	public LuaValue metatag(LuaValue tag) {
		return proxyTarget.metatag(tag);
	}

	@Override
	protected LuaValue checkmetatag(LuaValue tag, String reason) {
		return super.checkmetatag(tag, reason);
	}

	@Override
	public Varargs onInvoke(Varargs args) {

		return proxyTarget.onInvoke(args);
	}

	@Override
	public void initupvalue1(LuaValue env) {
		proxyTarget.initupvalue1(env);
	}

	@Override
	public Varargs subargs(int start) {
		return proxyTarget.subargs(start);
	}
}
