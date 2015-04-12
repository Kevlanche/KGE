package com.kevlanche.kge.runtime;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.Kge.Graphics;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Camera;

public class CamController {
	private List<TrackedCamera> mCameras;

	private final GameState mState;
	private final Stage mStage;

	private final TrackedCamera mDefaultCamera;

	public CamController(GameState state, Stage stage, Camera defaultCamera) {
		mCameras = new ArrayList<>();
		mState = state;
		mStage = stage;
		mDefaultCamera = new TrackedCamera(defaultCamera);
	}

	public void reset() {
		mCameras.clear();
		final List<State> cameras = mState.getStatesByName(Camera.NAME);

		for (State e : cameras) {
			if (!(e instanceof Camera)) {
				System.err.println("Got non-camera from camera query? " + e);
				continue;
			}
			Camera c = (Camera) e;
			mCameras.add(new TrackedCamera(c));
		}
	}

	void tick() {
		if (mState.isRunning()) {
			for (TrackedCamera tc : mCameras) {
				update(tc);
			}
		}
		update(mDefaultCamera);
	}

	private void update(TrackedCamera tc) {
		if (tc.check()) {
			final OrthographicCamera og = (OrthographicCamera) mStage
					.getCamera();
			og.zoom = tc.mCamera.zoom.asFloat();

			final Graphics graphics = Kge.getInstance().graphics;
			final Viewport viewport = mStage.getViewport();
			viewport.setScreenSize(graphics.width, graphics.height);

			final float camw = tc.mCamera.width.asFloat();
			final float camh = tc.mCamera.height.asFloat();
			viewport.setWorldSize(camw, camh);

			viewport.apply(false);

			og.position.set(tc.mCamera.x.asFloat() + camw / 2,
					tc.mCamera.y.asFloat() + camh / 2, 0f);
			og.update();
		}
	}

	private class TrackedCamera {
		private Camera mCamera;
		private long mLastMod;

		public TrackedCamera(Camera camera) {
			mCamera = camera;
			mLastMod = mCamera.getLastModified();
		}

		public boolean check() {
			final long newMod = mCamera.getLastModified();
			if (mLastMod != newMod) {
				mLastMod = newMod;
				return true;
			} else {
				return false;
			}
		}
	}
}