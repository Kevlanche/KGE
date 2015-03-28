package com.kevlanche.engine.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import com.kevlanche.engine.game.actor.Actor;

public class GameState extends Observable {
	public GameState() {
		new Timer(5, new ActionListener() {

			long lastTime = System.currentTimeMillis();

			@Override
			public void actionPerformed(ActionEvent e) {
				final long currTime = System.currentTimeMillis();
				final long dt = currTime - lastTime;

				if (mIsRunning) {
					final float fdt = dt / 1000f;

					for (Actor actor : mAllActors) {
						actor.update(fdt);
					}
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

	public void setIsRunning(boolean run) {
		mIsRunning = run;
		setChanged();
		notifyObservers();
	}

	public List<Actor> getAllActors() {
		return mAllActors;
	}

	public void addActor(Actor actor) {
		mAllActors.add(actor);
		setChanged();
		notifyObservers();
	}

	public Actor getCurrentSelection() {
		return mCurrentSelection;
	}

	public void setCurrentSelection(Actor currentSelection) {
		mCurrentSelection = currentSelection;
		setChanged();
		notifyObservers();
	}

}
