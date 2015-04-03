package com.kevlanche.engine.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.script.CompileException;

public class GameState {
	public GameState() {
		new Timer(15, new ActionListener() {

			long lastTime = System.currentTimeMillis();

			@Override
			public void actionPerformed(ActionEvent e) {
				final long currTime = System.currentTimeMillis();
				final long dt = currTime - lastTime;

				Kge kge = Kge.getInstance();
				kge.time.currentTimeMillis = System.currentTimeMillis();
				if (mIsRunning) {
					final float fdt = dt / 1000f;
					kge.time.gameTime += fdt;
					kge.time.dt = fdt;

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

				lastTime = currTime;
			}
		}).start();
	}

	private boolean mIsRunning = false;
	private List<Entity> mAllActors = new CopyOnWriteArrayList<>();
	private List<GameStateObserver> mObservers = new CopyOnWriteArrayList<>();
	private Entity mCurrentSelection = null;

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
				try {
					a.saveState();
				} catch (CompileException e) {
					e.printStackTrace();
				}
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

}
