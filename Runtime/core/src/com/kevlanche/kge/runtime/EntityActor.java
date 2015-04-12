package com.kevlanche.kge.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityListener;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Physics;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Transform;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.kge.runtime.GdxAssetProvider.GdxDrawable;

public class EntityActor extends Group {

	private static class PhysicalEntity {
		public Entity actor;
		public Transform transform;
		public Physics physics;
		public Rendering render;
	}

	private static PhysicalEntity wrapEntity(Entity ent) {
		Transform transform = null;
		Physics physics = null;
		Rendering texture = null;

		for (State state : ent.getStates()) {
			if (state instanceof Transform) {
				transform = (Transform) state;
			} else if (state instanceof Physics) {
				physics = (Physics) state;
			} else if (state instanceof Rendering) {
				texture = (Rendering) state;
			}
		}

		if (transform == null) {
			System.err.println("Unable to extract transform from " + ent);
			return null;
		}
		final PhysicalEntity ret = new PhysicalEntity();
		ret.actor = ent;
		ret.transform = transform;
		ret.physics = physics;
		ret.render = texture;

		return ret;
	}

	private final Entity mEntity;
	private PhysicalEntity mPhysical;
	private GameState mState;

	private boolean mIsBeingPressed;

	public EntityActor(final GameState game, Entity entity) {
		mEntity = entity;
		mState = game;

		mPhysical = wrapEntity(mEntity);
		setTouchable(Touchable.enabled);

		mEntity.addListener(new EntityListener() {

			@Override
			public void onEntityChanged(Entity entity) {
				mPhysical = wrapEntity(mEntity);
			}
		});

		addListener(new InputListener() {

			Vector2 last = new Vector2();
			Vector2 tmp = new Vector2();
			FloatVariable xattr, yattr;

			Vector2 target = new Vector2();

			Actor transformSrc;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (mState.isRunning()) {
					return false;
				}

				if (isBeingPressed()) {
					return false;
				}

				mIsBeingPressed = true;

				if (button == Buttons.RIGHT) {
					xattr = mPhysical.transform.width;
					yattr = mPhysical.transform.height;

					transformSrc = null;
				} else {
					xattr = mPhysical.transform.x;
					yattr = mPhysical.transform.y;
					transformSrc = EntityActor.this;
				}
				target.set(xattr.asFloat(), yattr.asFloat());

				last.set(x, y);

				doTrans(last);
				game.setCurrentSelection(mEntity);
				return true;
			}

			private void doTrans(Vector2 vec) {
				if (transformSrc != null) {
					transformSrc.localToParentCoordinates(vec);
				}
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				tmp.set(x, y);
				doTrans(tmp);
				float dx = tmp.x - last.x;
				float dy = tmp.y - last.y;
				last.set(tmp);

				target.add(dx, dy);

				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
					xattr.set(Math.round(target.x));
					yattr.set(Math.round(target.y));
				} else {
					xattr.set(target.x);
					yattr.set(target.y);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				mIsBeingPressed = false;
			}
		});

	}

	protected boolean isBeingPressed() {
		if (mIsBeingPressed) {
			return true;
		} else {
			for (Actor child : getChildren()) {
				if (child instanceof EntityActor
						&& ((EntityActor) child).isBeingPressed()) {
					return true;
				}
			}
		}
		return false;
	}

	public Entity getWrappedEntity() {
		return mEntity;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (mPhysical != null) {
			if (mPhysical.render != null) {
				Drawable d = mPhysical.render.texture.asDrawable();
				if (!(d instanceof GdxDrawable)) {
					System.err.println("Non-gdx drawable? " + d);
					return;
				}
				final GdxDrawable gd = (GdxDrawable) d;
				final TextureRegion region = gd.texture;

				batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
						getWidth(), getHeight(), getScaleX(), getScaleY(),
						getRotation());
			}

			super.draw(batch, parentAlpha);
		}
	}

	@Override
	protected void drawDebugBounds(ShapeRenderer shapes) {
		if (!getDebug() || mPhysical == null
				|| (mState.getCurrentSelection() != mPhysical.actor)) {
			return;
		}
		shapes.set(ShapeType.Line);
		shapes.setColor(getStage().getDebugColor());
		shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(),
				getHeight(), getScaleX(), getScaleY(), getRotation());
		shapes.circle(getX() + getOriginX(), getY() + getOriginY(), 0.25f);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		updateLocationStuff();
	}

	private void updateLocationStuff() {
		if (mPhysical != null) {
			setPosition(mPhysical.transform.x.asFloat(),
					mPhysical.transform.y.asFloat());

			setSize(mPhysical.transform.width.asFloat(),
					mPhysical.transform.height.asFloat());

			setRotation(mPhysical.transform.rotation.asFloat());
			setOrigin(mPhysical.transform.anchorX.asFloat(),
					mPhysical.transform.anchorY.asFloat());
		} else {
			setPosition(0f, 0f);
			setSize(.5f, .5f);
			setRotation(0f);
			setOrigin(0f, 0f);
		}
	}

	public void scroll(int amount) {
		if (!mIsBeingPressed) {
			for (Actor child : getChildren()) {
				if (child instanceof EntityActor) {
					((EntityActor) child).scroll(amount);
				}
			}
			return;
		}
		final float currRot = mPhysical.transform.rotation.asFloat();
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
			final int round = 15;
			final int rounded = round * Math.round(currRot / round);
			final float newVal = rounded + (amount < 0 ? round : -round);
			mPhysical.transform.rotation.set(Math.round(newVal));
		} else {
			mPhysical.transform.rotation.set(currRot - 2 * amount);
		}
	}
}
