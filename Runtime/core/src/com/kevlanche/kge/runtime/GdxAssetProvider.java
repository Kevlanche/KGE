package com.kevlanche.kge.runtime;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptProvider;
import com.kevlanche.engine.game.script.impl.PythonScript;

public class GdxAssetProvider implements AssetProvider {

	public static GdxDrawable DEFAULT_IMAGE;

	private Map<String, Drawable> mLoadedRegions;

	private File mRootDirectory;
	final List<Script> mScripts;

	public GdxAssetProvider(File rootDirectory) {
		mRootDirectory = rootDirectory;
		mLoadedRegions = new HashMap<>();
		mScripts = new ArrayList<>();
	}

	public void doLoad() {
		mLoadedRegions.clear();
		mScripts.clear();

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

		final File[] scriptFiles = new File(mRootDirectory, "scripts")
				.listFiles();

		if (scriptFiles != null) {
			for (File file : scriptFiles) {
				if (file.getName().endsWith(".py")) {
					mScripts.add(new PythonFileScript(file));
				} else {
					System.out.println("Skippin 'script' file " + file);
				}
			}
		}
	}

	@Override
	public Collection<Drawable> getDrawables() {
		return mLoadedRegions.values();
	}

	@Override
	public Drawable getDrawable(String name) {
		return mLoadedRegions.get(name);
	}

	@Override
	public List<Script> getScripts() {
		return mScripts;
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

		public PythonFileScript(final File file) {
			super(new Streamable() {

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
}
