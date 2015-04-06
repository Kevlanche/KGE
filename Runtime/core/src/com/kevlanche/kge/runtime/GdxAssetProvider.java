package com.kevlanche.kge.runtime;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.kevlanche.engine.game.assets.Drawable;
import com.kevlanche.engine.game.assets.AssetProvider;

public class GdxAssetProvider implements AssetProvider {

	public static GdxDrawable DEFAULT_IMAGE;

	private Map<String, Drawable> mLoadedRegions;

	private final File mTextureDirectory;

	public GdxAssetProvider(File textureDirectory) {
		mTextureDirectory = textureDirectory;
		mLoadedRegions = new HashMap<>();
	}

	public void doLoad() {
		final File[] files = mTextureDirectory.listFiles();

		if (files != null) {
			for (File atlas : files) {
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

	@Override
	public Collection<Drawable> getDrawables() {
		return mLoadedRegions.values();
	}

	@Override
	public Drawable getDrawable(String name) {
		return mLoadedRegions.get(name);
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
}
