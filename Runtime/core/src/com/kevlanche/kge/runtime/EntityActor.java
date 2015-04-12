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
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.kge.runtime.GdxAssetProvider.GdxDrawable;

public class EntityActor extends Group {

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

		if (pos == null) {
			System.err.println("Unable to extract pos from " + ent);
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

		// addListener(new InputListener() {
		//
		// final Vector2 touch = new Vector2();
		// final Vector2 tmp = new Vector2();
		//
		// final Vector2 attrTarget = new Vector2();
		//
		// final Vector2 previousFrame = new Vector2();
		// Variable xAttr, yAttr;
		//
		// boolean isAnchor = false;
		//
		// boolean changeInWorldSpace;
		//
		// void transFromLocal(Vector2 vec) {
		// final Group par = getParent();
		// // if (par != null) {
		// // localToParentCoordinates(vec);
		// // } else {
		// localToStageCoordinates(vec);
		// // }
		// }
		//
		// @Override
		// public boolean touchDown(InputEvent event, float x, float y,
		// int pointer, int button) {
		// if (mState.isRunning()) {
		// return false;
		// }
		// if (isBeingPressed()) {
		// return false;
		// }
		//
		// mIsBeingPressed = true;
		// touch.set(x, y);
		// transFromLocal(touch);
		// previousFrame.set(touch);
		//
		// isAnchor = mPhysical.rotation != null
		// && (button == Buttons.MIDDLE);
		// changeInWorldSpace = false;
		// if (mPhysical != null) {
		// if (button == Buttons.RIGHT && mPhysical.size != null) {
		// xAttr = mPhysical.size.width;
		// yAttr = mPhysical.size.height;
		// } else {
		// if (isAnchor) {
		// xAttr = mPhysical.rotation.anchorX;
		// yAttr = mPhysical.rotation.anchorY;
		// } else {
		// xAttr = mPhysical.position.x;
		// yAttr = mPhysical.position.y;
		// }
		// changeInWorldSpace = true;
		// }
		// attrTarget.set(xAttr.asFloat(), yAttr.asFloat());
		//
		// if (mPhysical.physics != null) {
		// mPhysical.physics.staticBody.saveState();
		// mPhysical.physics.staticBody.set(true);
		// }
		// } else {
		// xAttr = null;
		// yAttr = null;
		// }
		// game.setCurrentSelection(mEntity);
		// return true;
		// }
		//
		// @Override
		// public void touchDragged(InputEvent event, float x, float y,
		// int pointer) {
		// previousFrame.set(touch);
		// tmp.set(x, y);
		// transFromLocal(tmp);
		// float diffx = touch.x - tmp.x;
		// float diffy = touch.y - tmp.y;
		// touch.set(tmp);
		//
		// tmp.set(diffx, diffy);
		// stageToLocalCoordinates(tmp);
		// diffx = tmp.x;
		// diffy = tmp.y;
		//
		// System.out.println(diffx + ";" + diffy);
		//
		// if (isAnchor) {
		// float pdx = diffx;
		// float pdy = diffy;
		// final Vector2 diff = new Vector2(-diffx, -diffy);
		// diff.rotate(-mPhysical.rotation.degrees.asFloat());
		// diffx = -diff.x;
		// diffy = -diff.y;
		//
		// mPhysical.position.x.set(mPhysical.position.x.asFloat()
		// + (diffx - pdx));
		// mPhysical.position.y.set(mPhysical.position.y.asFloat()
		// + (diffy - pdy));
		// }
		//
		// if (changeInWorldSpace || mPhysical.rotation == null) {
		// attrTarget.add(-diffx, -diffy);
		// } else {
		// final Vector2 diff = new Vector2(-diffx, -diffy);
		// diff.rotate(-mPhysical.rotation.degrees.asFloat());
		// attrTarget.add(diff);
		// }
		//
		// if (xAttr != null && yAttr != null) {
		// if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
		// xAttr.set(Math.round(attrTarget.x));
		// yAttr.set(Math.round(attrTarget.y));
		// } else {
		// xAttr.set(attrTarget.x);
		// yAttr.set(attrTarget.y);
		// }
		// }
		// }
		//
		// @Override
		// public void touchUp(InputEvent event, float x, float y,
		// int pointer, int button) {
		// mIsBeingPressed = false;
		// float dx = previousFrame.x - touch.x;
		// float dy = previousFrame.y - touch.y;
		// if (mPhysical != null && mPhysical.physics != null) {
		// mPhysical.physics.velocityX.set(dx);
		// mPhysical.physics.velocityY.set(dy);
		// mPhysical.physics.staticBody.restoreState();
		// }
		// }
		// });

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
					xattr = mPhysical.size.width;
					yattr = mPhysical.size.height;
					
					transformSrc = null;
//					transformSrc = EntityActor.this;
//					while (transformSrc.getParent() != null) {
//						transformSrc = transformSrc.getParent();
//					}
				} else {
					xattr = mPhysical.position.x;
					yattr = mPhysical.position.y;
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
				
//				tmp.set(getX() + dx, getY() + dy);
//				transformSrc.stageToLocalCoordinates(tmp);
//				target.add(tmp);
				
				target.add(dx,dy);

				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
					xattr.set(Math.round(target.x));
					yattr.set(Math.round(target.y));
				} else {
					xattr.set(target.x);
					yattr.set(target.y);
				}
				System.out.println(xattr.asFloat());
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
						getRotation(), false);
			}

			super.draw(batch, parentAlpha);
		}
	}

	@Override
	protected void drawDebugBounds(ShapeRenderer shapes) {
		if (!getDebug()
				|| mPhysical == null
				|| (mState.getCurrentSelection() != mPhysical.actor && mPhysical.size != null)) {
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
			setPosition(mPhysical.position.x.asFloat(),
					mPhysical.position.y.asFloat());

			if (mPhysical.size != null) {
				setSize(mPhysical.size.width.asFloat(),
						mPhysical.size.height.asFloat());
			} else {
				setSize(.5f, .5f);
			}

			if (mPhysical.rotation != null) {
				setRotation(mPhysical.rotation.degrees.asFloat());
				setOrigin(mPhysical.rotation.anchorX.asFloat(),
						mPhysical.rotation.anchorY.asFloat());
			} else {
				setRotation(0f);
				setOrigin(0f, 0f);
			}
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
