package com.kevlanche.kge.runtime.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.kevlanche.engine.game.actor.BaseEntity;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.CompileException;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Camera.Up;
import com.kevlanche.engine.game.state.impl.Physics;
import com.kevlanche.engine.game.state.impl.Transform;
import com.kevlanche.engine.game.state.impl.Transform.Height;
import com.kevlanche.engine.game.state.impl.Transform.Rotation;
import com.kevlanche.engine.game.state.impl.Transform.Width;
import com.kevlanche.engine.game.state.impl.Transform.X;
import com.kevlanche.engine.game.state.impl.Transform.Y;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.TypeException;
import com.kevlanche.kge.runtime.Collision;

public class BasePhysicsEntity extends BaseEntity {

	private final World mWorld;

	private Body mBody;

	public final Transform transform;

	private Physics mPhysics;

	final Vector2 mCurrSize = new Vector2();

	public BasePhysicsEntity(Entity parent, World world) {
		this("base", parent, world);
	}

	public BasePhysicsEntity(String className, Entity parent, World world) {
		super(className, parent);
		mWorld = world;

		// rebuildBody(0f, 0f, 4f, 2f, 0f, Physics.FixedRotation.DEFAULT_VALUE,
		// Physics.Static.DEFAULT_VALUE);

		// simulate = new Transform.SimulatePhysics() {
		//
		// {
		// set(false);
		// }
		//
		// public void set(boolean value) throws TypeException {
		// super.set(value);
		// rebuildBody(x.asFloat(), y.asFloat(), width.asFloat(),
		// height.asFloat(), rotation.asFloat(), simulate.asBool());
		// };
		// };

		final X x = new X() {

			public void set(float value) throws TypeException {
				if (mBody == null) {
					super.set(value);
				} else {
					mBody.setTransform(
							value,
							transform.y.asFloat(),
							MathUtils.degreesToRadians
									* transform.rotation.asFloat());
				}
			};

			@Override
			public float asFloat() throws TypeException {
				if (mBody == null) {
					return super.asFloat();
				} else {
					return mBody.getTransform().getPosition().x;
				}
			}
		};

		final Y y = new Y() {

			public void set(float value) throws TypeException {
				if (mBody == null) {
					super.set(value);
				} else {
					mBody.setTransform(
							transform.x.asFloat(),
							value,
							MathUtils.degreesToRadians
									* transform.rotation.asFloat());
				}
			};

			@Override
			public float asFloat() throws TypeException {
				if (mBody == null) {
					return super.asFloat();
				} else {
					return mBody.getTransform().getPosition().y;
				}
			}
		};
		final Rotation rotation = new Rotation() {

			public void set(float value) throws TypeException {
				if (mBody == null) {
					super.set(value);
				} else {
					mBody.setTransform(transform.x.asFloat(),
							transform.y.asFloat(), MathUtils.degreesToRadians
									* value);
				}
			};

			@Override
			public float asFloat() throws TypeException {
				if (mBody == null) {
					return super.asFloat();
				} else {
					return MathUtils.radiansToDegrees
							* mBody.getTransform().getRotation();
				}
			}
		};

		final Width width = new Width() {
			public void set(float value) throws TypeException {
				if (mBody == null) {
					super.set(value);
				} else {
					rebuildBody(x.asFloat(), y.asFloat(), value,
							transform.height.asFloat(), rotation.asFloat());
				}
			};

			@Override
			public float asFloat() throws TypeException {
				if (mBody == null) {
					return super.asFloat();
				} else {
					return mCurrSize.x;
				}
			}
		};
		final Height height = new Transform.Height() {
			public void set(float value) throws TypeException {
				if (mBody == null) {
					super.set(value);
				} else {
					rebuildBody(transform.x.asFloat(), transform.y.asFloat(),
							transform.width.asFloat(), value,
							transform.rotation.asFloat());
				}
			};

			@Override
			public float asFloat() throws TypeException {
				if (mBody == null) {
					return super.asFloat();
				} else {
					return mCurrSize.y;
				}
			}
		};

		addPermanentState(transform = new Transform(x, y, width, height,
				rotation));
	}

	private void rebuildBody(float x, float y, float width, float height,
			float angleDegrees) {
		if (mBody != null) {
			mBody.getWorld().destroyBody(mBody);
		}
		if (mPhysics == null) {
			System.err.println("No physics set. Can't build body");
			return;
		}

		BodyDef bd = new BodyDef();

		bd.fixedRotation = mPhysics.fixedRotation.asBool();
		bd.type = mPhysics.dynamic.asBool() ? BodyType.DynamicBody
				: BodyType.StaticBody;
		bd.position.set(x, y);
		bd.angle = MathUtils.degreesToRadians * angleDegrees;
		bd.linearDamping = 0.2f;
		mBody = mWorld.createBody(bd);
		FixtureDef fd = new FixtureDef();
		fd.density = 0.2f;
		fd.filter.categoryBits = Collision.PLAYER;
		fd.filter.maskBits = Collision.WORLD | Collision.PLAYER;
		fd.restitution = 0.2f;
		fd.friction = 0.2f;
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(width / 2, height / 2, new Vector2(width / 2, height / 2),
				0f);
		fd.shape = ps;
		mBody.createFixture(fd);
		ps.dispose();

		mCurrSize.set(width, height);
	}

	@Override
	protected void addPermanentState(State state) {
		super.addPermanentState(state);
		if (state instanceof Physics) {
			mPhysics = (Physics) state;

			final FloatVariable velx = new Physics.VelocityX() {

				@Override
				public float asFloat() throws TypeException {
					return mBody.getLinearVelocity().x;
				}

				@Override
				public void set(float value) throws TypeException {
					mBody.setLinearVelocity(value, mPhysics.velocityY.asFloat());
				}
			};
			final FloatVariable vely = new Physics.VelocityX() {

				@Override
				public float asFloat() throws TypeException {
					return mBody.getLinearVelocity().y;
				}

				@Override
				public void set(float value) throws TypeException {
					mBody.setLinearVelocity(mPhysics.velocityX.asFloat(), value);
				}
			};
			mPhysics.intercept(velx, vely);

			rebuildBody(transform.x.asFloat(), transform.y.asFloat(),
					transform.width.asFloat(), transform.height.asFloat(),
					transform.rotation.asFloat());
		}
	}

	@Override
	public void dispose() {
		if (mBody != null) {
			mBody.getWorld().destroyBody(mBody);
		}
		super.dispose();
	}

	long lastPhysicsChange;

	@Override
	public void tick() throws CompileException {
		super.tick();

		// if (mPhysics != null && mBody != null) {
		// System.out.println(mWorld.getGravity());
		// final long newChange = mPhysics.getLastModified();
		//
		// final boolean hasDynamic = mBody.getType() == BodyType.DynamicBody;
		// final boolean hasFixedRotation = mBody.isFixedRotation();
		//
		// // If true, a non-velocity variable changed.
		// final boolean nonSpeedDiff = (mPhysics.dynamic.asBool() != hasDynamic
		// || mPhysics.fixedRotation
		// .asBool() != hasFixedRotation);
		//
		// if (newChange != lastPhysicsChange) {
		// // A script modified one or more of the parameters.
		// if (nonSpeedDiff) {
		// rebuildBody(transform.x.asFloat(), transform.y.asFloat(),
		// transform.width.asFloat(),
		// transform.height.asFloat(),
		// transform.rotation.asFloat());
		// } else {
		// mBody.setLinearVelocity(mPhysics.velocityX.asFloat(),
		// mPhysics.velocityY.asFloat());
		// }
		// lastPhysicsChange = newChange;
		// } else {
		// final Vector2 currSpeed = mBody.getLinearVelocity();
		// if (currSpeed.x != mPhysics.velocityX.asFloat()
		// || currSpeed.y != mPhysics.velocityY.asFloat()) {
		// mPhysics.velocityX.set(currSpeed.x);
		// mPhysics.velocityY.set(currSpeed.y);
		// }
		// if (nonSpeedDiff) {
		// mPhysics.dynamic.set(hasDynamic);
		// mPhysics.fixedRotation.set(hasFixedRotation);
		// }
		// }
		// }
	}
}
