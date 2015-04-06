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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityListener;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Physics;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.engine.game.state.var.Variable;
import com.kevlanche.kge.runtime.GdxAssetProvider.GdxDrawable;

public class EntityActor extends Actor {

	private static class PhysicalEntity {
		public Entity actor;
		public Position position;
		public Rotation rotation;
		public Physics physics;
		public Rendering render;
		public Size size;
	}

	private static PhysicalEntity wrapEntity(Entity ent) {
		Position pos = null;
		Size size = null;
		Rotation rotation = null;
		Physics physics = null;
		Rendering texture = null;

		for (State state : ent.getStates()) {
			if (state instanceof Position) {
				pos = (Position) state;
			} else if (state instanceof Size) {
				size = (Size) state;
			} else if (state instanceof Rotation) {
				rotation = (Rotation) state;
			} else if (state instanceof Physics) {
				physics = (Physics) state;
			} else if (state instanceof Rendering) {
				texture = (Rendering) state;
			}
		}

		if (pos == null || size == null || rotation == null || physics == null
				|| texture == null) {
			System.err.println("Unable to extract pos/size from " + ent);
			return null;
		}
		final PhysicalEntity ret = new PhysicalEntity();
		ret.actor = ent;
		ret.position = pos;
		ret.size = size;
		ret.rotation = rotation;
		ret.physics = physics;
		ret.render = texture;

		return ret;
	}

	private final Entity mEntity;
	private PhysicalEntity mPhysical;
	private GameState mState;

	public boolean isBeingPressed;

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

			final Vector2 touch = new Vector2();
			final Vector2 tmp = new Vector2();
			final Vector2 originalTouch = new Vector2();

			final Vector2 attrTarget = new Vector2();

			final Vector2 previousFrame = new Vector2();
			Variable xAttr, yAttr;

			boolean isMid = false;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				isBeingPressed = true;
				touch.set(x, y);
				localToStageCoordinates(touch);
				previousFrame.set(touch);

				originalTouch.set(mPhysical.rotation.anchorX.asFloat()
						* getWidth(), mPhysical.rotation.anchorY.asFloat()
						* getHeight());
				localToStageCoordinates(originalTouch);

				isMid = (button == Buttons.MIDDLE);
				if (mPhysical != null) {
					if (button == Buttons.RIGHT) {
						xAttr = mPhysical.size.width;
						yAttr = mPhysical.size.height;
					} else {
						xAttr = mPhysical.position.x;
						yAttr = mPhysical.position.y;
					}
					attrTarget.set(xAttr.asFloat(), yAttr.asFloat());

					mPhysical.physics.staticBody.saveState();
					mPhysical.physics.staticBody.set(true);
				} else {
					xAttr = null;
					yAttr = null;
				}
				game.setCurrentSelection(mEntity);
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				previousFrame.set(touch);
				tmp.set(x, y);
				localToStageCoordinates(tmp);
				float diffx = touch.x - tmp.x;
				float diffy = touch.y - tmp.y;
				touch.set(tmp);
				attrTarget.add(-diffx, -diffy);

				if (isMid) {

					Vector2 world = new Vector2(touch);
					localToStageCoordinates(world);

					final float origRot = getRotation();
					setRotation(0f);

					Vector2 origToLocal = new Vector2(originalTouch);
					stageToLocalCoordinates(origToLocal);
					System.out.println("lt: " + origToLocal);
					// local(origToLocal);

					// float offx = origToLocal.x - xAttr.asFloat();
					// float offy = origToLocal.y - yAttr.asFloat();

					// System.out.println(getX() +", " + origToLocal.x);
					Vector2 toSet = new Vector2(origToLocal.x / getWidth(),
							origToLocal.y / getHeight());
					toSet.rotate(origRot);

					mPhysical.rotation.anchorX.set(toSet.x);
					mPhysical.rotation.anchorY.set(toSet.y);
					// mPhysical.rotation.anchorY.set(-offy / getHeight());

					updateLocationStuff();

					stageToLocalCoordinates(world);
					touch.set(world);

					setRotation(origRot);
					updateLocationStuff();
				}

				if (xAttr != null && yAttr != null) {
					if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						xAttr.set(Math.round(attrTarget.x));
						yAttr.set(Math.round(attrTarget.y));
					} else {
						xAttr.set(attrTarget.x);
						yAttr.set(attrTarget.y);
					}
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				isBeingPressed = false;
				float dx = previousFrame.x - touch.x;
				float dy = previousFrame.y - touch.y;
				if (mPhysical != null) {
					mPhysical.physics.velocityX.set(dx);
					mPhysical.physics.velocityY.set(dy);
				}
				mPhysical.physics.staticBody.restoreState();
			}
		});

	}

	public Entity getWrappedEntity() {
		return mEntity;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (mPhysical != null) {
			super.draw(batch, parentAlpha);

			Drawable d = mPhysical.render.texture.asDrawable();
			if (!(d instanceof GdxDrawable)) {
				System.err.println("Non-gdx drawable? " + d);
				return;
			}
			final GdxDrawable gd = (GdxDrawable) d;
			final TextureRegion region = gd.texture;

			batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
					getWidth(), getHeight(), getScaleX(), getScaleY(),
					getRotation(), false);
		}
	}

	@Override
	protected void drawDebugBounds(ShapeRenderer shapes) {
		if (!getDebug() || mPhysical == null
				|| mState.getCurrentSelection() != mPhysical.actor) {
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

		if (mState.isRunning()) {
			getStage().getCamera().position.set(getX(), getY(), 0f);
			getStage().getCamera().update();
		}
	}

	private void updateLocationStuff() {
		if (mPhysical != null) {
			setBounds(mPhysical.position.x.asFloat(),
					mPhysical.position.y.asFloat(),
					mPhysical.size.width.asFloat(),
					mPhysical.size.height.asFloat());

			setRotation(mPhysical.rotation.degrees.asFloat());
			setOrigin(mPhysical.rotation.anchorX.asFloat() * getWidth(),
					mPhysical.rotation.anchorY.asFloat() * getHeight());
		}
	}

	public void scroll(int amount) {
		final float currRot = mPhysical.rotation.degrees.asFloat();
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
			final int round = 15;
			final int rounded = round * Math.round(currRot / round);
			System.out.println(currRot + ",m " + rounded);
			final float newVal = rounded + (amount < 0 ? round : -round);
			System.out.println("==> " + newVal);
			mPhysical.rotation.degrees.set(Math.round(newVal));
		} else {
			mPhysical.rotation.degrees.set(currRot - 2 * amount);
		}
	}

}
