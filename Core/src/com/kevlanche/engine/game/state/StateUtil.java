package com.kevlanche.engine.game.state;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;

public class StateUtil {

	public static class OwnedState {
		public final State state;
		public final Entity owner;

		public OwnedState(State state, Entity owner) {
			this.state = state;
			this.owner = owner;
		}
	}

	public static List<OwnedState> recursiveFindStates(Entity src) {
		final List<OwnedState> ret = new ArrayList<OwnedState>();
		recursiveFindStates(src, ret);
		return ret;
	}

	private static void recursiveFindStates(Entity src, List<OwnedState> out) {
		outer: for (State state : src.getStates()) {

			final String stateName = state.getName();
			for (OwnedState existing : out) {
				if (existing.state.getName().equals(stateName)) {
					continue outer;
				}
			}
			out.add(new OwnedState(state, src));
		}
		final Entity parent = src.getParent();
		if (parent != null) {
			recursiveFindStates(parent, out);
		}
	}

}
