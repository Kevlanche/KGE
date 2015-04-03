package com.kevlanche.engine.game.state;

import java.util.ArrayList;
import java.util.List;

import com.kevlanche.engine.game.actor.Entity;

public class StateUtil {

	public static class FoundState {
		public final State state;
		public final Entity owner;

		public FoundState(State state, Entity owner) {
			this.state = state;
			this.owner = owner;
		}
	}

	public static List<FoundState> recursiveFindStates(Entity src) {
		final List<FoundState> ret = new ArrayList<FoundState>();
		recursiveFindStates(src, ret);
		return ret;
	}

	private static void recursiveFindStates(Entity src, List<FoundState> out) {
		outer: for (State state : src.getStates()) {

			final String stateName = state.getName();
			for (FoundState existing : out) {
				if (existing.state.getName().equals(stateName)) {
					continue outer;
				}
			}
			out.add(new FoundState(state, src));
		}
		final Entity parent = src.getParent();
		if (parent != null) {
			recursiveFindStates(parent, out);
		}
	}

}
