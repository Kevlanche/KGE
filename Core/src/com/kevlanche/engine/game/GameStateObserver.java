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

public interface GameStateObserver {

	void onEntityAdded(Entity entity);
	void onEntityRemoved(Entity entity);
	void onFocusChanged(Entity newFocus);
	
	void onGenericChange();

}
