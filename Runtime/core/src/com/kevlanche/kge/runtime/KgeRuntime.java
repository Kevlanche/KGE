package com.kevlanche.kge.runtime;

import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.GameStateObserverAdapter;
import com.kevlanche.engine.game.actor.Entity;

public class KgeRuntime extends ApplicationAdapter {
	SpriteBatch batch;

	private final GameState mState;

	private Stage mStage;

	public static World mWorld;
	Box2DDebugRenderer b2d;
	float updBuf;

	public KgeRuntime() {
		mState = new GameState(new GdxAssetProvider(new File(
				"C:\\Users\\Anton\\KGE\\SampleGame")));
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		((GdxAssetProvider) mState.getAssetProvider()).doLoad();

		mStage = new Stage(new ScalingViewport(Scaling.stretch, 8f, 6f));
		mStage.addActor(new Grid());

		final Group actorGroup = new Group();
		mStage.addActor(actorGroup);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		for (Entity ent : mState.getEntities()) {
			actorGroup.addActor(new EntityActor(mState, ent));
		}
		mState.addObserver(new GameStateObserverAdapter() { // TODO remove on
															// destroy

			@Override
			public void onEntityAdded(final Entity entity) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						actorGroup.addActor(new EntityActor(mState, entity));
					}
				});
			}

			@Override
			public void onEntityRemoved(final Entity entity) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						for (Actor actor : actorGroup.getChildren()) {
							if (actor instanceof EntityActor
									&& ((EntityActor) actor).getWrappedEntity() == entity) {
								actor.remove();
								return;
							}
						}
					}
				});
			}
		});

		final InputMultiplexer input = new InputMultiplexer(
				new KgeInputMapper(), mStage) {
			@Override
			public boolean scrolled(int amount) {
				for (Actor actor : actorGroup.getChildren()) {
					if (actor instanceof EntityActor) {
						final EntityActor ent = (EntityActor) actor;
						if (ent.isBeingPressed) {

							ent.scroll(amount);
							return true;
						}
					}
				}
				final OrthographicCamera og = (OrthographicCamera) mStage
						.getCamera();
				og.zoom = Math
						.max(0.1f, Math.min(2.0f, og.zoom + amount / 10f));
				return true;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return super.mouseMoved(screenX, screenY);
			}
		};

		input.addProcessor(new InputAdapter() {
			@Override
			public boolean keyTyped(char character) {
				if (mState.isRunning()) {
					return false;
				}
				if (character == 'a') {
					Vector2 spawn = new Vector2(Gdx.input.getX(), Gdx.input
							.getY());
					mStage.screenToStageCoordinates(spawn);

					final BoxedEntity ent = new BoxedEntity(null, mWorld);
					ent.position.x.set(spawn.x);
					ent.position.y.set(spawn.y);

					mState.addEntity(ent);
				} else if (character == 'd') {
					final Entity focus = mState.getCurrentSelection();
					if (focus != null) {
						mState.removeEntity(focus);
					}
				} else {
					return false;
				}
				return true;
			}
		});

		Gdx.input.setInputProcessor(input);

		mWorld = new World(new Vector2(0f, 0f), false);
		b2d = new Box2DDebugRenderer();
		mState.addEntity(new BoxedEntity(null, mWorld));
	}

	public GameState getState() {
		return mState;
	}

	@Override
	public void render() {
		if (mState.isRunning()) {
			updBuf += Gdx.graphics.getDeltaTime();
			final float SIMUL_DT = 1f / 60f;
			while (updBuf > SIMUL_DT) {
				mWorld.step(SIMUL_DT, 3, 3);
				updBuf -= SIMUL_DT;
			}
		}

		mState.tick(Gdx.graphics.getDeltaTime());
		mStage.act();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mStage.setDebugAll(true);
		mStage.draw();

		b2d.render(mWorld, mStage.getCamera().combined);
	}

	public static final float PTM_RATIO = 32f;

	@Override
	public void resize(int width, int height) {
		mStage.getViewport()
				.setWorldSize(width / PTM_RATIO, height / PTM_RATIO);
		mStage.getViewport().setScreenSize(width, height);
		mStage.getViewport().apply(false);
	}
}
