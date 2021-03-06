package com.kevlanche.kge.runtime;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.state.impl.Camera;

public class Grid extends Actor {

	private final Camera mCamState;
	
	public Grid(final GameState gameState, Camera camState) {
		mCamState = camState;
		
		addListener(new InputListener() {

			Vector2 touch = new Vector2();

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (gameState.isRunning()) {
					return false;
				}
				touch.set(x, y);
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				final float dx = x - touch.x;
				final float dy = y - touch.y;
				
				mCamState.x.set(mCamState.x.asFloat() - dx);
				mCamState.y.set(mCamState.y.asFloat() - dy);
			}
		});
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return this;
	}

	@Override
	protected void drawDebugBounds(ShapeRenderer shapes) {
		if (!getDebug())
			return;
		Color normColor = new Color(.2f, .2f, .2f, .25f);
		Color hlColor = new Color(.2f, .2f, .2f, .5f);
		Color shlColor = new Color(.3f, .3f, .3f, 1f);
		shapes.setColor(normColor);
		shapes.set(ShapeType.Line);

		final Stage stage = getStage();
		float w = stage.getWidth();
		float h = stage.getHeight();
		
		final int miny = (int)(-3*h/2 + stage.getCamera().position.y);
		final int maxy = (int)(miny + 3*h);
		
		final int minx = (int)(-3*w/2 + stage.getCamera().position.x);
		final int maxx = (int)(minx + 3*w);
		
		for (int y = miny; y < maxy; y++) {
			if (y % 50 == 0) {
				shapes.setColor(shlColor);
			} else if (y % 5 == 0) {
				shapes.setColor(hlColor);
			} else {
				shapes.setColor(normColor);
			}
			shapes.line(minx, y, maxx, y);
		}

		for (int x = minx; x < maxx; x++) {
			if (x % 50 == 0) {
				shapes.setColor(shlColor);
			} else if (x % 5 == 0) {
				shapes.setColor(hlColor);
			} else {
				shapes.setColor(normColor);
			}
			shapes.line(x, miny, x, maxy);
		}
	}
}