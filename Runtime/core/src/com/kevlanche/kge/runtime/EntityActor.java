package com.kevlanche.kge.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityListener;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.engine.game.state.var.Variable;

public class EntityActor extends Actor {

	private static class PhysicalEntity {
		public Entity actor;
		public Position position;
		public Rotation rotation;
		public Size size;
	}

	private static PhysicalEntity wrapEntity(Entity ent) {
		Position pos = null;
		Size size = null;
		Rotation rotation = null;

		for (State state : ent.getStates()) {
			if (state instanceof Position) {
				pos = (Position) state;
			}
			if (state instanceof Size) {
				size = (Size) state;
			}
			if (state instanceof Rotation) {
				rotation = (Rotation) state;
			}

		}

		if (pos == null || size == null || rotation == null) {
			System.err.println("Unable to extract pos/size from " + ent);
			return null;
		}
		final PhysicalEntity ret = new PhysicalEntity();
		ret.actor = ent;
		ret.position = pos;
		ret.size = size;
		ret.rotation = rotation;

		return ret;
	}

	private final Entity mEntity;
	private PhysicalEntity mPhysical;
	private final Texture mImg;

	public EntityActor(Entity entity) {
		mEntity = entity;
		mImg = new Texture("badlogic.jpg");

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

			final Vector2 attrTarget = new Vector2();
			Variable xAttr, yAttr;

			boolean rightClick = false;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				rightClick = button == Buttons.RIGHT;
				touch.set(x, y);
				localToStageCoordinates(touch);

				if (mPhysical != null) {
					if (rightClick) {
						xAttr = mPhysical.size.width;
						yAttr = mPhysical.size.height;
					} else {
						xAttr = mPhysical.position.x;
						yAttr = mPhysical.position.y;
					}
					attrTarget.set(xAttr.asFloat(), yAttr.asFloat());
				} else {
					xAttr = null;
					yAttr = null;
				}
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				tmp.set(x, y);
				localToStageCoordinates(tmp);
				float diffx = touch.x - tmp.x;
				float diffy = touch.y - tmp.y;
				touch.set(tmp);

				attrTarget.add(-diffx, -diffy);

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
		});
	}

	public Entity getWrappedEntity() {
		return mEntity;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (mPhysical != null) {
			super.draw(batch, parentAlpha);
			batch.draw(mImg, getX(), getY(), getOriginX(), getOriginY(),
					getWidth(), getHeight(), getScaleX(), getScaleY(),
					getRotation(), 0, 0, mImg.getWidth(), mImg.getHeight(),
					false, false);
		}
	}

	@Override
	protected void drawDebugBounds(ShapeRenderer shapes) {
		if (!getDebug()) {
			return;
		} 
		shapes.set(ShapeType.Line);
		shapes.setColor(getStage().getDebugColor());
		shapes.rect(getX(), getY(), getOriginX(), getOriginY(),
				getWidth(), getHeight(), getScaleX(), getScaleY(),
				getRotation());
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (mPhysical != null) {
			setBounds(mPhysical.position.x.asFloat(),
					mPhysical.position.y.asFloat(),
					mPhysical.size.width.asFloat(),
					mPhysical.size.height.asFloat());

			setRotation(mPhysical.rotation.degrees.asFloat());
		}
	}

}
