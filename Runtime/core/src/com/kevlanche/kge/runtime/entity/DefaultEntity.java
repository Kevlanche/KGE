package com.kevlanche.kge.runtime.entity;

import com.kevlanche.engine.game.actor.BaseEntity;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Rendering.DrawableSrc;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.kge.runtime.GdxAssetProvider;

public class DefaultEntity extends BaseEntity {

	static int uidCtr = 0;

	private final String mName;

	public final Position position;

	public DefaultEntity(Entity parent) {
		super("defafultEntity", parent);

		mName = "Actor " + (++uidCtr);

		addPermanentState(position = new Position());
		addPermanentState(new Size());
		addPermanentState(new Rotation());
		// addPermanentState(new Rendering(new DrawableSrc(
		// GdxAssetProvider.DEFAULT_IMAGE)));
	}

	@Override
	public String toString() {
		return mName;
	}
}
