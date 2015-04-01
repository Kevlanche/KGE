package com.kevlanche.engine.game.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kevlanche.engine.game.script.ReloadListener;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptInstance;
import com.kevlanche.engine.game.script.ScriptOwner;

public class BaseActor implements ScriptOwner {

	protected interface InstanceAcessor {
		void set(ScriptInstance value);

		ScriptInstance getValue();
	}

	private class AutodisposedReloadListener implements ReloadListener {

		private final Script script;
		private final InstanceAcessor acc;

		public AutodisposedReloadListener(Script script, InstanceAcessor acc) {
			this.script = script;
			this.acc = acc;
			this.script.addReloadListener(this);
		}

		@Override
		public void onScriptSourceReloaded() {
			acc.set(script.createInstance(BaseActor.this));
		}
	}

	protected final Map<String, ScriptInstance> mInstalledComponents = new HashMap<>();

	private final List<InstanceAcessor> mScripts = new CopyOnWriteArrayList<>();

	private final List<AutodisposedReloadListener> reloadListeners;

	public BaseActor() {
		reloadListeners = new CopyOnWriteArrayList<>();
	}

	public void dispose() {
		for (AutodisposedReloadListener l : reloadListeners) {
			l.script.removeReloadListener(l);
		}
		reloadListeners.clear();
	}

	public void reset() {
		for (InstanceAcessor instance : mScripts) {
			Script src = instance.getValue().getSource();
			ScriptInstance copy = src.createInstance(BaseActor.this);
			instance.set(copy);
		}
	}

	public void update() {
		for (InstanceAcessor instance : mScripts) {
			try {
				instance.getValue().update();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScriptInstance get(String name) {
		return mInstalledComponents.get(name);
	}

	@Override
	public void addScript(final String name, final Script script) {
		final InstanceAcessor defAccessor = new InstanceAcessor() {

			ScriptInstance instance;

			@Override
			public void set(ScriptInstance value) {
				mInstalledComponents.put(name, value);
				instance = value;
			}

			@Override
			public ScriptInstance getValue() {
				return instance;
			}
		};
		addScript(script, defAccessor);
	}

	protected void addScript(Script script, InstanceAcessor defAccessor) {
		defAccessor.set(script.createInstance(this));
		mScripts.add(defAccessor);
		script.addReloadListener(new ReloadListener() {

			@Override
			public void onScriptSourceReloaded() {
				defAccessor.set(script.createInstance(BaseActor.this));
			}
		});
		reloadListeners
				.add(new AutodisposedReloadListener(script, defAccessor));
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
