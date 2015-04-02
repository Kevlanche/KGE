package com.kevlanche.engine.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.script.CompileException;

public class GameState extends Observable {
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

					for (Actor actor : mAllActors) {
						try {
							actor.tick();
						} catch (CompileException e1) {
							mIsRunning = false;
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null, "Compile errorz! " + e1.toString());
						}
					}

					kge.input.afterFrame();
				}

				lastTime = currTime;
			}
		}).start();
	}
	
	private boolean mIsRunning = false;
	private List<Actor> mAllActors = new CopyOnWriteArrayList<>();
	private Actor mCurrentSelection = null;

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
		
		for (Actor a : mAllActors) {
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

	public List<Actor> getAllActors() {
		return mAllActors;
	}

	public void addActor(Actor actor) {
		mAllActors.add(actor);
		if (mCurrentSelection == null) {
			mCurrentSelection = actor;
		}
		triggerOnChanged();
	}

	public void removeActor(Actor actor) {
		if (actor == mCurrentSelection) {
			mCurrentSelection = null;
		}
		mAllActors.remove(actor);
		actor.dispose();
		triggerOnChanged();
	}

	public Actor getCurrentSelection() {
		return mCurrentSelection;
	}

	public void setCurrentSelection(Actor currentSelection) {
		if (mCurrentSelection == currentSelection) {
			return;
		}
		mCurrentSelection = currentSelection;
		triggerOnChanged();
	}

	public void triggerOnChanged() {
		setChanged();
		notifyObservers();
	}

}
