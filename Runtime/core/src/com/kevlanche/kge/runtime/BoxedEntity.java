package com.kevlanche.kge.runtime;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.kevlanche.engine.game.actor.BaseEntity;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.state.impl.Physics;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.engine.game.state.var.DrawableVariable;
import com.kevlanche.engine.game.state.var.FloatVariable;
import com.kevlanche.engine.game.state.var.TypeException;

public class BoxedEntity extends BaseEntity {

	static int uidCtr = 0;

	private final String mName;

	private Body mBody;

	final Position position;
	final Size size;
	final Rotation rotation;
	final Physics physics;

	final Vector2 mCurrSize = new Vector2();

	public BoxedEntity(Entity parent, World world) {
		super(parent);

		mName = "Box2D entity " + (++uidCtr);

		rebuildBody(world, 0f, 0f, 4f, 2f, 0f,
				Physics.FixedRotation.DEFAULT_VALUE,
				Physics.Static.DEFAULT_VALUE);

		final FloatVariable x = new FloatVariable("x", 0f) {
			public void set(float value) throws TypeException {
				final Transform trans = mBody.getTransform();
				mBody.setTransform(value, trans.getPosition().y,
						trans.getRotation());
			};

			@Override
			public float asFloat() throws TypeException {
				return mBody.getTransform().getPosition().x;
			}
		};

		final FloatVariable y = new FloatVariable("y", 0f) {
			public void set(float value) throws TypeException {
				final Transform trans = mBody.getTransform();
				mBody.setTransform(trans.getPosition().x, value,
						trans.getRotation());
			};

			@Override
			public float asFloat() throws TypeException {
				return mBody.getTransform().getPosition().y;
			}
		};
		final Rotation.Degrees degrees = new Rotation.Degrees() {
			public void set(float value) throws TypeException {
				final Vector2 trans = mBody.getTransform().getPosition();
				mBody.setTransform(trans.x, trans.y, MathUtils.degreesToRadians
						* value);
			};

			@Override
			public float asFloat() throws TypeException {
				return MathUtils.radiansToDegrees
						* mBody.getTransform().getRotation();
			}
		};

		final Physics.Static isStatic = new Physics.Static() {
			public boolean asBool() throws TypeException {
				return mBody.getType() == BodyType.StaticBody;
			};

			public void set(boolean value) throws TypeException {
				mBody.setType(value ? BodyType.StaticBody
						: BodyType.DynamicBody);
			};
		};

		final Physics.VelocityX velocityX = new Physics.VelocityX() {
			public void set(float value) throws TypeException {
				mBody.setLinearVelocity(value, mBody.getLinearVelocity().y);
			};

			@Override
			public float asFloat() throws TypeException {
				return mBody.getLinearVelocity().x;
			}
		};
		final Physics.VelocityY velocityY = new Physics.VelocityY() {
			public void set(float value) throws TypeException {
				mBody.setLinearVelocity(mBody.getLinearVelocity().x, value);
			};

			@Override
			public float asFloat() throws TypeException {
				return mBody.getLinearVelocity().y;
			}
		};
		final Physics.FixedRotation fixedRotation = new Physics.FixedRotation() {
			public boolean asBool() throws TypeException {
				return mBody.isFixedRotation();
			};

			public void set(boolean value) throws TypeException {
				mBody.setFixedRotation(value);
			};
		};
		final FloatVariable width = new FloatVariable("width", 0f) {
			public void set(float value) throws TypeException {
				rebuildBody(mBody.getWorld(), x.asFloat(), y.asFloat(), value,
						mCurrSize.y, degrees.asFloat(), fixedRotation.asBool(),
						isStatic.asBool());
			};

			@Override
			public float asFloat() throws TypeException {
				return mCurrSize.x;
			}
		};
		final FloatVariable height = new FloatVariable("height", 0f) {
			public void set(float value) throws TypeException {
				rebuildBody(mBody.getWorld(), x.asFloat(), y.asFloat(),
						mCurrSize.x, value, degrees.asFloat(),
						fixedRotation.asBool(), isStatic.asBool());
			};

			@Override
			public float asFloat() throws TypeException {
				return mCurrSize.y;
			}
		};

		position = new Position(x, y);
		size = new Size(width, height);
		rotation = new Rotation(degrees);
		physics = new Physics(fixedRotation, isStatic, velocityX, velocityY);
		addState(position);
		addState(size);
		addState(rotation);
		addState(physics);
		addState(new Rendering(new DrawableVariable("texture", GdxAssetProvider.DEFAULT_IMAGE)));
	}

	private void rebuildBody(World world, float x, float y, float width,
			float height, float angleDegrees, boolean fixedRotation,
			boolean isStatic) {
		if (mBody != null) {
			mBody.getWorld().destroyBody(mBody);
		}

		BodyDef bd = new BodyDef();

		bd.fixedRotation = fixedRotation;
		bd.fixedRotation = false;
		bd.type = isStatic ? BodyType.StaticBody : BodyType.DynamicBody;
		bd.position.set(x, y);
		bd.angle = MathUtils.degreesToRadians * angleDegrees;
		bd.linearDamping = 0.2f;
		mBody = world.createBody(bd);
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
	public void dispose() {
		if (mBody != null) {
			mBody.getWorld().destroyBody(mBody);
		}
		super.dispose();
	}
	@Override
	public String toString() {
		return mName;
	}
}
