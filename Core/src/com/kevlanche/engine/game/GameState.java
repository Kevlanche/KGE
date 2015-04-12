package com.kevlanche.engine.game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.script.CompileException;

public class GameState {

	private AssetProvider mAssetProvider;
	private boolean mIsRunning = false;
	private List<Entity> mAllActors = new CopyOnWriteArrayList<>();
	private List<GameStateObserver> mObservers = new CopyOnWriteArrayList<>();
	private Entity mCurrentSelection = null;

	public GameState(AssetProvider provider) {
		mAssetProvider = provider;
	}

	public void tick(float dt) {
		Kge kge = Kge.getInstance();
		kge.time.currentTimeMillis = System.currentTimeMillis();
		if (mIsRunning) {
			kge.time.gameTime += dt;
			kge.time.dt = dt;

			for (Entity actor : mAllActors) {
				try {
					actor.tick();
				} catch (CompileException e1) {
					mIsRunning = false;
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Compile errorz! " + e1.toString());
				}
			}

			kge.input.afterFrame();
		}
	}

	public AssetProvider getAssetProvider() {
		return mAssetProvider;
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

		for (Entity a : mAllActors) {
			if (mIsRunning) {
				a.saveState();
			} else {
				a.restoreState();
			}
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
