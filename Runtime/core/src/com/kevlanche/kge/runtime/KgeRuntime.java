package com.kevlanche.kge.runtime;

import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
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
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.Kge.Graphics;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.state.impl.Camera;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.TypeException;

public class KgeRuntime extends ApplicationAdapter {
	SpriteBatch batch;

	private final GameState mState;

	private Stage mStage;

	private final World mWorld;
	Box2DDebugRenderer b2d;
	float updBuf;

	private Camera mDefaultCamera;
	private CamController mCamController;

	public KgeRuntime() {
		final Kge kge = Kge.getInstance();
		kge.physics = new Kge.Physics(new FloatVariable("gravityX", 0f) {
			@Override
			public void set(float value) throws TypeException {
				mWorld.setGravity(new Vector2(value, kge.physics.gravityY
						.asFloat()));
			}

			@Override
			public float asFloat() throws TypeException {
				return mWorld.getGravity().x;
			}
		}, new FloatVariable("gravityY", 0f) {
			@Override
			public void set(float value) throws TypeException {
				mWorld.setGravity(new Vector2(kge.physics.gravityX.asFloat(),
						value));
			}

			@Override
			public float asFloat() throws TypeException {
				return mWorld.getGravity().y;
			}
		});

		mWorld = new World(new Vector2(0f, -9.82f), false);
		final EntityLoaderImpl loader = new EntityLoaderImpl(mWorld);
		final GdxAssetProvider provider = new GdxAssetProvider(new File(
				"C:\\Users\\Anton\\KGE\\SampleGame"), loader);
		mState = new GameState(provider, loader);
	}

	@Override
	public void create() {
		batch = new SpriteBatch();

		((GdxAssetProvider) mState.getAssetProvider()).doLoad();

		mStage = new Stage(new ScalingViewport(Scaling.stretch, 8f, 6f));

		mDefaultCamera = new Camera();
		mCamController = new CamController(mState, mStage, mDefaultCamera);
		mStage.addActor(new Grid(mState, mDefaultCamera));

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
						add(entity);
					}

					private EntityActor add(final Entity entity) {
						final Entity parent = entity.getParent();
						Group parentActor = actorGroup;
						if (parent != null) {
							parentActor = add(parent);
						} else {
							for (Actor actor : actorGroup.getChildren()) {
								if (actor instanceof EntityActor
										&& ((EntityActor) actor)
												.getWrappedEntity().equals(
														entity)) {
									return (EntityActor) actor;
								}
							}
						}

						final EntityActor ret = new EntityActor(mState, entity);
						parentActor.addActor(ret);
						return ret;
					}
				});
			}

			@Override
			public void onEntityRemoved(final Entity entity) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						removeFrom(actorGroup);
					}

					private void removeFrom(final Group actorGroup) {
						for (Actor actor : actorGroup.getChildren()) {
							if (actor instanceof EntityActor) {
								final EntityActor ea = (EntityActor) actor;
								if (ea.getWrappedEntity() == entity) {
									actor.remove();
									return;
								} else {
									removeFrom(ea);
								}
							}
						}
					}
				});
			}

			@Override
			public void onRunningChanged() {
				if (mState.isRunning()) {
					mCamController.reset();
				} else {
					// Triggers an onChanged & updates the camera to editor
					// state
					mDefaultCamera.x.copy(mDefaultCamera.x);
				}
			}
		});

		final InputMultiplexer input = new InputMultiplexer(
				new KgeInputMapper(), mStage) {
			@Override
			public boolean scrolled(int amount) {
				if (mState.isRunning()) {
					return false;
				}

				for (Actor actor : actorGroup.getChildren()) {
					if (actor instanceof EntityActor) {
						final EntityActor ent = (EntityActor) actor;
						if (ent.isBeingPressed()) {
							ent.scroll(amount);
							return true;
						}
					}
				}
				mDefaultCamera.zoom.set(mDefaultCamera.zoom.asFloat() + amount
						/ 10f);
				return true;
			}
		};

		input.addProcessor(new InputAdapter() {
			@Override
			public boolean keyTyped(char character) {
				if (mState.isRunning()) {
					return false;
				}
				if (character == 'd') {
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

		b2d = new Box2DDebugRenderer();
	}

	public GameState getState() {
		return mState;
	}

	@Override
	public void render() {
		mCamController.tick();

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
		final Graphics kgeGraphics = Kge.getInstance().graphics;
		kgeGraphics.width.set(width);
		kgeGraphics.height.set(height);

		mDefaultCamera.width.set(width / PTM_RATIO);
		mDefaultCamera.height.set(height / PTM_RATIO);

		mCamController.tick();
	}
}
