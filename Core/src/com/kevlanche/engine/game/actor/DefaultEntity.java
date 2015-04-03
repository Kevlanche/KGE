package com.kevlanche.engine.game.actor;

import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;

public class DefaultEntity extends BaseEntity {

	static int uidCtr = 0;
	
	private final String mName;
	
	public DefaultEntity(Entity parent) {
		super(parent);
		
		mName = "Actor " + (++uidCtr);
		
		addState(new Position());
		addState(new Size());
		addState(new Rotation());
	}
	
	@Override
	public String toString() {
		return mName;
	}
}
