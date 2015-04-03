package com.kevlanche.kge.runtime;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.GameStateObserverAdapter;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Entity;

public class KgeRuntime extends ApplicationAdapter {
	SpriteBatch batch;

	private final GameState mState;

	private Stage mStage;

	public KgeRuntime(GameState state) {
		mState = state;
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		mStage = new Stage(new ScalingViewport(Scaling.stretch, 8f, 6f));
		mStage.addActor(new Grid());

		final Group actorGroup = new Group();
		mStage.addActor(actorGroup);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		for (Entity ent : mState.getEntities()) {
			actorGroup.addActor(new EntityActor(ent));
		}
		mState.addObserver(new GameStateObserverAdapter() { // TODO remove on
															// destroy

			@Override
			public void onEntityAdded(Entity entity) {
				actorGroup.addActor(new EntityActor(entity));
			}

			@Override
			public void onEntityRemoved(Entity entity) {
				for (Actor actor : mStage.getActors()) {
					if (actor instanceof EntityActor
							&& ((EntityActor) actor).getWrappedEntity() == entity) {
						actor.remove();
						break;
					}
				}
			}
		});

		final InputMultiplexer input = new InputMultiplexer(new KgeInputMapper(),
				mStage);
		Gdx.input.setInputProcessor(input);
	}

	@Override
	public void render() {
		mStage.act();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// batch.begin();
		// batch.draw(img, 0, 0);
		mStage.setDebugAll(true);
		mStage.draw();
		// batch.end();
	}

	@Override
	public void resize(int width, int height) {
		mStage.getViewport().setWorldSize(width / 32f, height / 32f);
		mStage.getViewport().setScreenSize(width, height);
		mStage.getViewport().apply(false);
	}
}
