package com.kevlanche.engine.editor.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import com.kevlanche.engine.game.GameState;
import com.kevlanche.engine.game.Kge;
import com.kevlanche.engine.game.actor.Actor;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.impl.Position;
import com.kevlanche.engine.game.state.impl.Rotation;
import com.kevlanche.engine.game.state.impl.Size;

@SuppressWarnings("serial")
public class CenterPanel extends BasePanel {

	private static final double PTM_RATIO = 32.0;

	private final GameState mState;

	private double scaleFactor = 1.0;
	private Point gameTransform = new Point();
	private final AffineTransform mCombinedTransform;

	private class PhysicalActor {
		public Actor actor;
		public Position position;
		public Rotation rotation;
		public Size size;
	}

	PhysicalActor fromNormalActor(Actor actor) {
		Position pos = null;
		Size size = null;
		Rotation rotation = null;

		for (State state : actor.getStates()) {
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
			System.err.println("Unable to extract pos/size from " + actor);
			return null;
		}
		final PhysicalActor ret = new PhysicalActor();
		ret.actor = actor;
		ret.position = pos;
		ret.size = size;
		ret.rotation = rotation;

		return ret;
	}

	public CenterPanel(GameState state) {
		setBackground(Color.DARK_GRAY);

		mState = state;

		final MouseAdapter ma = new MouseAdapter() {

			Point start = new Point();
			PhysicalActor target = null;

			boolean nullSelection = false;

			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
				start = swingToGame(e.getX(), e.getY());

				target = null;
				for (Actor actor : mState.getAllActors()) {
					final PhysicalActor pa = fromNormalActor(actor);
					if (pa == null) {
						continue;
					}

					if (pa.position.x.asFloat() <= start.x
							&& (pa.position.x.asFloat() + pa.size.width.asFloat()) >= start.x
							&& pa.position.y.asFloat() <= start.y
							&& (pa.position.y.asFloat() + pa.size.height.asFloat()) >= start.y) {
						target = pa;
						break;
					}
				}
				if (target == null) {
					nullSelection = true;
				} else {
					nullSelection = false;
					mState.setCurrentSelection(target.actor);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point curr = swingToGame(e.getX(), e.getY());

				int dx = curr.x - start.x;
				int dy = curr.y - start.y;

				if (dx != 0 || dy != 0) {
					nullSelection = false;
					System.out.println("Dragged " + dx + "," + dy);

					if (target != null) {
						int mods = e.getModifiers();
						System.out.println(Integer.toBinaryString(mods));
						if ((mods & MouseEvent.SHIFT_MASK) != 0) {
							target.size.width.set(target.size.width.asInt()
									+ dx);
							target.size.height.set(target.size.height.asInt()
									+ dy);
							// target.size.saveState();
						} else {
							target.position.x.set(target.position.x.asInt()
									+ dx);
							target.position.y.set(target.position.y.asInt()
									+ dy);
							// target.position.saveState();
						}

					} else {
						gameTransform.x += dx;
						gameTransform.y += dy;
						updateTransform();
						curr = swingToGame(e.getX(), e.getY());
					}
				}

				start.setLocation(curr);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				target = null;
				if (nullSelection) {
					mState.setCurrentSelection(null);
				}
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scaleFactor = Math.max(
						0.25f,
						Math.min(1.25f, scaleFactor - e.getUnitsToScroll()
								/ 30f));
				System.out.println("sf = " + scaleFactor);
			}
		};

		addMouseListener(ma);
		addMouseMotionListener(ma);
		addMouseWheelListener(ma);

		final Map<Integer, Integer> keyMappings = new HashMap<>();
		keyMappings.put(KeyEvent.VK_RIGHT, Kge.Input.RIGHT);
		keyMappings.put(KeyEvent.VK_LEFT, Kge.Input.LEFT);
		keyMappings.put(KeyEvent.VK_UP, Kge.Input.UP);
		keyMappings.put(KeyEvent.VK_DOWN, Kge.Input.DOWN);
		keyMappings.put(KeyEvent.VK_SPACE, Kge.Input.SPACE);
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				handle(e, true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handle(e, false);
			}

			private void handle(KeyEvent e, boolean pressed) {
				final Integer mapping = keyMappings.get(e.getKeyCode());
				if (mapping == null) {
					return;
				}
				Kge.getInstance().input.setPressing(mapping, pressed);
			}
		});
		setFocusable(true);

		mCombinedTransform = new AffineTransform();

		mCombinedTransform.setToIdentity();
		updateTransform();
	}

	private void updateTransform() {
		mCombinedTransform.setToIdentity();
		mCombinedTransform.scale(PTM_RATIO * scaleFactor, -PTM_RATIO
				* scaleFactor);
		mCombinedTransform.translate(0, -getHeight()
				/ (PTM_RATIO * scaleFactor));
		mCombinedTransform.translate(gameTransform.x, gameTransform.y);
		// mCombinedTransform.preConcatenate(mPositionTransform);
		// mCombinedTransform.preConcatenate(mScaleTransform);
	}

	private Point swingToGame(int x, int y) {

		final Point2D srcPos = new Point2D.Float(x, y);
		final Point2D dst = new Point2D.Float();
		try {
			mCombinedTransform.inverseTransform(srcPos, dst);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		return new Point((int) dst.getX(), (int) dst.getY());
	}

	private Rectangle gameToSwing(float x, float y, float w, float h) {

		final Point2D srcPos = new Point2D.Float(x, y);
		final Point2D dst = new Point2D.Float();
		mCombinedTransform.transform(srcPos, dst);

		int tx = (int) dst.getX();
		int ty = (int) dst.getY();

		int tw = (int) (w * PTM_RATIO * scaleFactor);
		int th = (int) (h * -PTM_RATIO * scaleFactor);

		if (th < 0) {
			ty += th;
			th = -th;
		}

		return new Rectangle(tx, ty, tw, th);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		updateTransform();

		final int gridw = 20;
		final int gridh = 20;
		final Rectangle grid = gameToSwing(0, 0, gridw, gridh);
		for (int x = 0; x <= gridw; x++) {
			if (x % 5 == 0) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.GRAY);
			}
			final int ox = x * grid.width / gridw;

			g.drawLine(grid.x + ox, grid.y, grid.x + ox, grid.y + grid.height);
		}
		for (int y = 0; y <= gridh; y++) {
			if (y % 5 == 0) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.GRAY);
			}
			final int oy = y * grid.height / gridh;

			g.drawLine(grid.x, grid.y + oy, grid.x + grid.width, grid.y + oy);
		}

		final Color focusColor = new Color(255, 0, 255, 200);
		final Color normalColor = new Color(120, 120, 120, 200);

		for (Actor actor : mState.getAllActors()) {
			final PhysicalActor pa = fromNormalActor(actor);
			if (pa == null) {
				continue;
			}
			
			final Rectangle toDraw = gameToSwing(pa.position.x.asFloat(),
					pa.position.y.asFloat(), pa.size.width.asFloat(),
					pa.size.height.asFloat());

			if (actor == mState.getCurrentSelection()) {
				g.setColor(focusColor);
			} else {
				g.setColor(normalColor);
			}

			Graphics2D g2d = (Graphics2D) g;

			AffineTransform at = g2d.getTransform();
			double rot = Math.PI * -pa.rotation.degrees.asFloat() / 180.0;
			at.rotate(rot, toDraw.x, toDraw.y + toDraw.height);
			g2d.setTransform(at);

			g.fillRect(toDraw.x, toDraw.y, toDraw.width, toDraw.height);

			g.setColor(Color.BLUE.darker().darker());
			g.drawRect(toDraw.x, toDraw.y, toDraw.width, toDraw.height);

			at.rotate(-rot, toDraw.x, toDraw.y + toDraw.height);
			g2d.setTransform(at);
		}

		repaint();
	}

}
