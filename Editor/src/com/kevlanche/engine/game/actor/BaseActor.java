package com.kevlanche.engine.game.actor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;

public class BaseActor implements ScriptOwner {

	private final Map<String, Object> mInstalledComponents = new HashMap<>();

	private final List<InstanceAcessor> mScripts = new CopyOnWriteArrayList<>();

	public BaseActor() {

	}

	protected interface InstanceAcessor {
		void set(ScriptInstance value);

		ScriptInstance getValue();
	}

	public void reset() {
		for (InstanceAcessor instance : mScripts) {
			Script src = instance.getValue().getSource();
			ScriptInstance copy = src.createInstance(BaseActor.this);
			instance.set(copy);
		}
	}
	
	public void update(float dt) {
		for (InstanceAcessor instance : mScripts) {
			try {
				instance.getValue().update(dt);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void installComponent(String name, Object value) {
		mInstalledComponents.put(name, CoerceJavaToLua.coerce(value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String name) {
		return mInstalledComponents.get(name);
	}

	@Override
	public void addScript(Script script) {
		final InstanceAcessor defAccessor = new InstanceAcessor() {

			ScriptInstance instance;

			@Override
			public void set(ScriptInstance value) {
				instance = value;
			}

			@Override
			public ScriptInstance getValue() {
				return instance;
			}
		};
		addScript(script, defAccessor);
	}

	protected void addScript(Script script, InstanceAcessor setter) {
		setter.set(script.createInstance(this));
		mScripts.add(setter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<ScriptInstance> getScripts() {
		final List<ScriptInstance> ret = new ArrayList<>();
		for (InstanceAcessor iw : mScripts) {
			ret.add(iw.getValue());
		}
		return ret;
	}
}
