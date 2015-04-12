package com.kevlanche.engine.game.script;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseScript implements Script {

	private final List<ReloadListener> mListeners;

	private final String mName;

	public BaseScript(String name) {
		mName = name;
		mListeners = new CopyOnWriteArrayList<>();
	}

	protected void notifyReload() {
		for (ReloadListener listener : mListeners) {
			listener.onScriptSourceReloaded();
		}
	}

	@Override
	public String getName() {
		return mName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addReloadListener(ReloadListener listener) {
		mListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeReloadListener(ReloadListener listener) {
		mListeners.remove(listener);
	}
}
