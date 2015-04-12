package com.kevlanche.engine.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.State;

public class GameState {

	private final AssetProvider mAssetProvider;
	private final EntityLoader mEntityLoader;
	private boolean mIsRunning = false;
	private List<Entity> mAllActors = new CopyOnWriteArrayList<>();
	private List<GameStateObserver> mObservers = new CopyOnWriteArrayList<>();
	private Entity mCurrentSelection = null;

	public GameState(AssetProvider provider, EntityLoader loader) {
		mAssetProvider = provider;
		mEntityLoader = loader;
	}

	public void tick(float dt) {
		Kge kge = Kge.getInstance();
		kge.time.currentTimeMillis = System.currentTimeMillis();
		if (mIsRunning) {
			kge.time.gameTime += dt;
			kge.time.dt = dt;

			for (Entity actor : mAllActors) {
				try {
					actor.tick(this);
				} catch (CompileException e1) {
					setIsRunning(false);
					e1.printStackTrace();
					
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							JOptionPane.showMessageDialog(null,
									"Compile errorz! " + e1.toString());
						}
					});
				}
			}

			kge.input.afterFrame();
		}
	}

	public AssetProvider getAssetProvider() {
		return mAssetProvider;
	}

	public EntityLoader getEntityLoader() {
		return mEntityLoader;
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	public boolean getIsRunning() {
		return mIsRunning;
	}

	public void setIsRunning(boolean run) {
		if (run == mIsRunning) {
			return;
		}
		mIsRunning = run;

		final Kge kge = Kge.getInstance();
		if (mIsRunning) {
			kge.physics.saveState();
		} else {
			kge.physics.restoreState();
		}
		for (Entity a : mAllActors) {
			if (mIsRunning) {
				a.saveState();
			} else {
				a.restoreState();
			}
		}
		for (GameStateObserver gso : mObservers) {
			gso.onRunningChanged();
		}
		triggerOnChanged();
	}

	public void addObserver(GameStateObserver observer) {
		mObservers.add(observer);
	}

	public void removeObserver(GameStateObserver observer) {
		mObservers.remove(observer);
	}

	public List<Entity> getEntities() {
		return mAllActors;
	}

	public List<State> getStatesByName(String stateName) {
		final List<State> ret = new ArrayList<>();
		for (Entity e : mAllActors) {

			for (State state : e.getStates()) {
				if (state.getName().equals(stateName)) {
					ret.add(state);
				}
			}
		}
		return ret;
	}

	public List<Entity> getEntitiesByClass(String className) {
		// TODO this should probably returns subclasses as well.
		final List<Entity> ret = new ArrayList<>();
		for (Entity e : mAllActors) {
			if (e.getClassName().equals(className)) {
				ret.add(e);
			}
		}
		return ret;
	}

	public void addEntity(Entity entity) {
		mAllActors.add(entity);
		if (mCurrentSelection == null) {
			mCurrentSelection = entity;
		}
		for (GameStateObserver gso : mObservers) {
			gso.onEntityAdded(entity);
		}
		triggerOnChanged();
	}

	public void removeEntity(Entity entity) {
		if (entity == mCurrentSelection) {
			mCurrentSelection = null;
		}
		mAllActors.remove(entity);
		if (entity.getParent() != null) {
			entity.getParent().removeChild(entity);
		}
		entity.dispose();
		for (GameStateObserver gso : mObservers) {
			gso.onEntityRemoved(entity);
		}
		triggerOnChanged();
	}

	public Entity getCurrentSelection() {
		return mCurrentSelection;
	}

	public void setCurrentSelection(Entity currentSelection) {

		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					setCurrentSelection(currentSelection);
				}
			});
			return;
		}
		if (mCurrentSelection == currentSelection) {
			return;
		}
		mCurrentSelection = currentSelection;
		for (GameStateObserver gso : mObservers) {
			gso.onFocusChanged(mCurrentSelection);
		}
		triggerOnChanged();
	}

	public void triggerOnChanged() {
		for (GameStateObserver gso : mObservers) {
			gso.onGenericChange();
		}
	}

	public void clearEntities() {
		while (!mAllActors.isEmpty()) {
			removeEntity(mAllActors.get(0));
		}
	}
}
