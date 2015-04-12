package com.kevlanche.kge.runtime;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kevlanche.engine.game.EntityLoader;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Level;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.assets.StateDefinition;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptDefinition;
import com.kevlanche.engine.game.script.impl.PythonScript;
import com.kevlanche.engine.game.state.JavaState;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Physics;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rendering;
import com.kevlanche.engine.game.state.impl.Rendering.DrawableSrc;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;
import com.kevlanche.engine.game.state.value.BoolValue;
import com.kevlanche.engine.game.state.value.FloatValue;
import com.kevlanche.engine.game.state.value.IntValue;
import com.kevlanche.engine.game.state.value.StringValue;
import com.kevlanche.engine.game.state.value.Value;
import com.kevlanche.engine.game.state.value.variable.FloatVariable;
import com.kevlanche.engine.game.state.value.variable.IntVariable;
import com.kevlanche.engine.game.state.value.variable.ObservableVariable;
import com.kevlanche.engine.game.state.value.variable.StringVariable;
import com.kevlanche.engine.game.state.value.variable.Variable;

public class GdxAssetProvider implements AssetProvider {

	public static GdxDrawable DEFAULT_IMAGE;

	private Map<String, Drawable> mLoadedRegions;

	private File mRootDirectory;
	final List<ScriptDefinition> mScripts;

	final List<StateDefinition> mStateDefinitions;

	final List<EntityDefinition> mClassDefinitions;

	final List<Level> mLevels;

	public GdxAssetProvider(File rootDirectory) {
		mRootDirectory = rootDirectory;
		mLoadedRegions = new HashMap<>();
		mScripts = new ArrayList<>();
		mStateDefinitions = new ArrayList<>();
		mClassDefinitions = new ArrayList<>();
		mLevels = new ArrayList<>();
	}

	@Override
	public void storeAsLevel(List<Entity> entities, String levelName)
			throws IOException {
		final File levelDir = new File(mRootDirectory, "levels");
		if (!levelDir.exists()) {
			levelDir.mkdir();
		}
		final File out = new File(levelDir, levelName + ".json");
		try (final OutputStream oos = new FileOutputStream(out);
				JsonWriter writer = new JsonWriter(new OutputStreamWriter(oos))) {

			writer.setOutputType(OutputType.json);

			writer.array();

			for (Entity entity : entities) {
				if (entity.getParent() != null) {
					continue;
				}

				storeEntity(writer, entity);
			}
			writer.pop();
			writer.flush();
		}

		mLevels.clear();
		loadLevels();
	}

	private void storeEntity(JsonWriter writer, Entity entity)
			throws IOException {
		writer.object();
		writer.set("class", entity.getClassName());

		writer.object("vars");
		{
			for (State state : entity.getStates()) {
				
				boolean started = false;
				
				for (Variable var : state.getVariables()) {
					if (!var.hasDefaultValue()) {
						if (!started) {
							started = true;
							writer.object(state.getName());
						}
						writer.set(var.getName(), var.asString());
					}
				}
				if (started) {
					writer.pop();
				}
			}
		}
		writer.pop(); // vars

		final List<Entity> children = entity.getChildren();
		if (children != null && !children.isEmpty()) {
			writer.array("children");

			for (Entity child : children) {
				storeEntity(writer, child);
			}

			writer.pop();
		}

		writer.pop();
	}

	private void loadDefaults() {
		mStateDefinitions.add(new StateDefinition() {

			@Override
			public String getName() {
				return Physics.NAME;
			}

			@Override
			public State createInstance() {
				return new Physics();
			}
		});
		mStateDefinitions.add(new StateDefinition() {

			@Override
			public String getName() {
				return Position.NAME;
			}

			@Override
			public State createInstance() {
				return new Position();
			}
		});
		mStateDefinitions.add(new StateDefinition() {

			@Override
			public String getName() {
				return Rendering.NAME;
			}

			@Override
			public State createInstance() {
				return new Rendering(new DrawableSrc(DEFAULT_IMAGE,
						GdxAssetProvider.this));
			}
		});
		mStateDefinitions.add(new StateDefinition() {

			@Override
			public String getName() {
				return Rotation.NAME;
			}

			@Override
			public State createInstance() {
				return new Rotation();
			}
		});
		mStateDefinitions.add(new StateDefinition() {

			@Override
			public String getName() {
				return Size.NAME;
			}

			@Override
			public State createInstance() {
				return new Size();
			}
		});

		mClassDefinitions.add(new EntityDefinition() {

			@Override
			public void setDefaultParameters(Entity entity) {
			}

			@Override
			public List<String> getRequiredStates() {
				return Arrays.asList(Physics.NAME);
			}

			@Override
			public List<String> getRequiredScripts() {
				return null;
			}

			@Override
			public String getParentClass() {
				return "base";
			}

			@Override
			public String getClassName() {
				return "physicsEntity";
			}

			@Override
			public String toString() {
				return getClassName();
			}

			@Override
			public List<EntityDefinition> getChildren() {
				return null;
			}
		});

		mClassDefinitions.add(new EntityDefinition() {

			@Override
			public void setDefaultParameters(Entity entity) {
			}

			@Override
			public List<String> getRequiredStates() {
				return Arrays.asList(Position.NAME, Size.NAME, Rotation.NAME,
						Rendering.NAME);
			}

			@Override
			public List<String> getRequiredScripts() {
				return null;
			}

			@Override
			public String getParentClass() {
				return null;
			}

			@Override
			public String getClassName() {
				return "base";
			}

			@Override
			public String toString() {
				return getClassName();
			}

			@Override
			public List<EntityDefinition> getChildren() {
				return null;
			}
		});
	}

	public void doLoad() {
		mLoadedRegions.clear();
		mScripts.clear();
		mClassDefinitions.clear();
		mStateDefinitions.clear();
		DEFAULT_IMAGE = null;

		loadDefaults();

		loadTextures();

		loadScripts();

		loadClasses();

		loadStates();

		loadLevels();
	}

	@Override
	public List<Level> getLevels() {
		return mLevels;
	}

	@Override
	public Collection<Drawable> getDrawables() {
		return mLoadedRegions.values();
	}

	@Override
	public List<ScriptDefinition> getScripts() {
		return mScripts;
	}

	@Override
	public List<StateDefinition> getAvailableStates() {
		return mStateDefinitions;
	}

	@Override
	public List<EntityDefinition> getClasses() {
		return mClassDefinitions;
	}

	private void loadLevels() {
		final File[] levelFiles = new File(mRootDirectory, "levels")
				.listFiles();

		if (levelFiles != null) {
			JsonReader reader = new JsonReader();
			for (File file : levelFiles) {
				String fileName = file.getName();
				if (fileName.endsWith(".json")) {

					final JsonValue val = reader.parse(new FileHandle(file));
					mLevels.add(new GdxLevel(fileName.substring(0,
							fileName.indexOf('.')), val));

				} else {
					System.out.println("Skippin 'level' file " + file);
				}

			}
		}
	}

	private void loadStates() {
		final File[] stateFiles = new File(mRootDirectory, "states")
				.listFiles();

		if (stateFiles != null) {
			JsonReader reader = new JsonReader();
			for (File file : stateFiles) {
				String fileName = file.getName();
				if (fileName.endsWith(".json")) {

					final JsonValue val = reader.parse(new FileHandle(file));
					final List<ObservableVariable> vars = parseUserState(val);

					if (vars.isEmpty()) {
						System.err.println("No variables in " + file + "?");
					} else {
						final String stateName = fileName.substring(0,
								fileName.indexOf('.'));

						mStateDefinitions.add(new UserStateDefinition() {

							@Override
							public String toString() {
								return getName();
							}

							@Override
							public String getName() {
								return stateName;
							}

							@Override
							public Instance createInstance() {
								return new UserStateInstance(stateName,
										parseUserState(val));
							}
						});

					}
				} else {
					System.out.println("Skippin 'state' file " + file);
				}
			}
		}
	}

	private void loadClasses() {
		final File[] classFiles = new File(mRootDirectory, "classes")
				.listFiles();

		if (classFiles != null) {
			JsonReader reader = new JsonReader();
			for (File file : classFiles) {
				String fileName = file.getName();
				if (fileName.endsWith(".json")) {

					try {
						mClassDefinitions.add(extractClass(reader, file));
					} catch (RuntimeException e) {
						e.printStackTrace();
						System.err.println("Invalid class file " + file);
					}

				} else {
					System.out.println("Skippin 'class' file " + file);
				}
			}
		}
	}

	private void loadScripts() {
		final File[] scriptFiles = new File(mRootDirectory, "scripts")
				.listFiles();

		if (scriptFiles != null) {
			for (final File file : scriptFiles) {
				final String fileName = file.getName();
				if (fileName.endsWith(".py")) {
					mScripts.add(new ScriptDefinition() {

						@Override
						public String getName() {
							return fileName;
						}

						@Override
						public Script createInstance() {
							return new PythonFileScript(getName(), file);
						}
					});
				} else {
					System.out.println("Skippin 'script' file " + file);
				}
			}
		}
	}

	private void loadTextures() {
		final File textureDirectory = new File(mRootDirectory, "textures");

		final File[] atlasFiles = textureDirectory.listFiles();

		if (atlasFiles != null) {
			for (File atlas : atlasFiles) {
				if (atlas.getName().endsWith(".atlas")) {
					TextureAtlas ta = new TextureAtlas(new FileHandle(atlas));
					final Array<AtlasRegion> regions = ta.getRegions();

					for (AtlasRegion region : regions) {
						final GdxDrawable drawable = new GdxDrawable(region);
						if (DEFAULT_IMAGE == null) {
							DEFAULT_IMAGE = drawable;
						}
						mLoadedRegions.put(region.name, drawable);
					}
				}
			}
		}
	}

	private EntityDefinition extractClass(JsonReader reader, File file) {
		final JsonValue val = reader.parse(new FileHandle(file));

		final List<ValuedState> states = new ArrayList<>();

		final String fn = file.getName();

		final String className = fn.substring(0, fn.indexOf('.'));
		final String superClass = val.getString("extends", "base");

		final JsonValue reqStates = val.get("states");
		if (reqStates != null) {
			for (JsonValue req : reqStates.iterator()) {
				ValuedState vs = new ValuedState(req.name);

				for (JsonValue var : req.iterator()) {

					final Value value = toValue(var);
					if (value == null) {
						continue;
					}
					vs.defaultValues.put(var.name, value);
				}

				states.add(vs);
			}
		}

		final List<String> reqScripts = new ArrayList<>();
		final JsonValue scriptArr = val.get("scripts");
		if (scriptArr != null && scriptArr.isArray()) {
			for (int i = 0; i < scriptArr.size; i++) {
				reqScripts.add(scriptArr.get(i).asString());
			}
		}

		return new EntityDefinition() {

			@Override
			public void setDefaultParameters(Entity entity) {
				// Yo dawg I heard you like for loops
				for (ValuedState vs : states) {
					for (State state : entity.getStates()) {
						if (state.getName().equals(vs.stateName)) {
							for (Entry<String, Value> defVal : vs.defaultValues
									.entrySet()) {
								final String attrName = defVal.getKey();
								for (Variable stateVar : state.getVariables()) {
									if (stateVar.getName().equals(attrName)) {
										stateVar.copy(defVal.getValue());
										break;
									}
								}
							}
							continue;
						}
					}
				}
			}

			@Override
			public List<String> getRequiredStates() {
				final List<String> ret = new ArrayList<>();
				for (ValuedState vs : states) {
					ret.add(vs.stateName);
				}
				return ret;
			}

			@Override
			public List<String> getRequiredScripts() {
				return reqScripts;
			}

			@Override
			public String getParentClass() {
				return superClass;
			}

			@Override
			public String getClassName() {
				return className;
			}

			@Override
			public String toString() {
				return getClassName();
			}

			@Override
			public List<EntityDefinition> getChildren() {
				return null;
			}
		};
	}

	private Value toValue(JsonValue var) {
		if (var.isLong()) {
			return new IntValue(var.asInt());
		} else if (var.isDouble()) {
			return new FloatValue(var.asFloat());
		} else if (var.isString()) {
			return new StringValue(var.asString());
		} else if (var.isBoolean()) {
			return new BoolValue(var.asBoolean());
		} else {
			System.out.println("Unknown var type " + var);
			return null;
		}
	}

	private List<ObservableVariable> parseUserState(JsonValue val) {
		final List<ObservableVariable> vars = new ArrayList<>();

		System.out.println(val);
		for (JsonValue var : val.iterator()) {

			if (var.isLong()) {
				vars.add(new IntVariable(var.name, var.asInt()));
			} else if (var.isDouble()) {
				vars.add(new FloatVariable(var.name, var.asFloat()));
			} else if (var.isString()) {
				vars.add(new StringVariable(var.name, var.asString()));
			} else {
				System.out.println("Unknown var type " + var);
			}
		}
		return vars;
	}

	private class GdxLevel implements Level {

		private final String mName;
		private final JsonValue mValue;

		public GdxLevel(String name, JsonValue src) {
			mName = name;
			mValue = src;
		}

		@Override
		public String getName() {
			return mName;
		}

		@Override
		public String toString() {
			return getName();
		}

		@Override
		public void load(GameState game) {
			game.clearEntities();

			final List<EntityDefinition> classes = game.getAssetProvider()
					.getClasses();

			final Map<String, EntityDefinition> mapped = new HashMap<>();
			for (EntityDefinition def : classes) {
				mapped.put(def.getClassName(), def);
			}

			for (JsonValue entity : mValue.iterator()) {
				loadEntity(game, mapped, entity, null);
			}
		}

		private void loadEntity(GameState game,
				final Map<String, EntityDefinition> mapped, JsonValue entity,
				Entity parent) {
			final String name = entity.getString("class", "base");

			final EntityDefinition def = mapped.get(name);
			if (def == null) {
				throw new IllegalArgumentException("No such class \"" + name
						+ "\"");
			}
			try {
				Entity loaded = new EntityLoader().load(parent, def,
						game.getAssetProvider());

				final JsonValue vars = entity.get("vars");
				if (vars != null) {

					for (JsonValue state : vars.iterator()) {

						for (State entityState : loaded.getStates()) {
							if (entityState.getName().equals(state.name)) {

								for (Variable var : entityState.getVariables()) {

									final JsonValue defValue = state.get(var
											.getName());
									if (defValue != null) {
										final Value value = toValue(defValue);
										if (value != null) {
											var.copy(value);
										}
									}
								}
							}
						}
					}
				}
				final JsonValue children = entity.get("children");
				if (children != null && children.isArray()) {

					for (JsonValue child : children.iterator()) {
						loadEntity(game, mapped, child, loaded);
					}
				}

				game.addEntity(loaded);
			} catch (Exception e) {
				Thread.dumpStack();
				e.printStackTrace();
				System.out.println("no entity for you!");
			}
		}
	}

	public class GdxDrawable implements Drawable {
		public final AtlasRegion texture;

		public GdxDrawable(AtlasRegion texture) {
			this.texture = texture;
		}

		@Override
		public String getName() {
			return texture.name;
		}

	}

	public static class PythonFileScript extends PythonScript {

		private File mFile;

		public PythonFileScript(String name, final File file) {
			super(name, new Streamable() {

				@Override
				public InputStream read() throws IOException {
					return new FileInputStream(file);
				}

				@Override
				public String toString() {
					return file.getAbsolutePath();
				}
			});

			mFile = file;

			final AtomicLong scriptState = new AtomicLong(file.lastModified());

			new Timer(100, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					final long newMod = mFile.lastModified();
					if (newMod != scriptState.get()) {
						scriptState.set(newMod);
						System.out.println("Script change detected!");
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								notifyReload();
							}
						});
					}
				}
			}).start();
		}

		@Override
		public String toString() {
			return mFile.getName();
		}
	}

	private class ValuedState {
		public final String stateName;
		public Map<String, Value> defaultValues = new HashMap<>();

		public ValuedState(String name) {
			if (name == null) {
				throw new IllegalArgumentException("Missing name");
			}
			stateName = name;
		}
	}

	private class UserStateInstance extends JavaState implements
			UserStateDefinition.Instance {

		UserStateInstance(String name, List<ObservableVariable> vars) {
			super(name);
			for (ObservableVariable var : vars) {
				register(var);
			}
		}
	};

}
