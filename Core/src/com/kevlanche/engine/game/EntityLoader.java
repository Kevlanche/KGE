package com.kevlanche.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kevlanche.engine.game.actor.BaseEntity;
import com.kevlanche.engine.game.actor.Entity;
import com.kevlanche.engine.game.actor.EntityDefinition;
import com.kevlanche.engine.game.assets.AssetProvider;
import com.kevlanche.engine.game.assets.StateDefinition;
import com.kevlanche.engine.game.assets.UserStateDefinition;
import com.kevlanche.engine.game.script.Script;
import com.kevlanche.engine.game.script.ScriptDefinition;
import com.kevlanche.engine.game.state.State;
import com.kevlanche.engine.game.state.StateUtil;
import com.kevlanche.engine.game.state.StateUtil.OwnedState;

public class EntityLoader {

	public Entity load(Entity parent, EntityDefinition clazz,
			AssetProvider provider) throws Exception {
		final List<EntityDefinition> availClasses = provider.getClasses();
		final List<StateDefinition> availStates = provider.getAvailableStates();
		final List<ScriptDefinition> availScripts = provider.getScripts();

		BuilableEntity entity = new BuilableEntity(clazz.getClassName(), parent);

		Map<String, State> parentStates = new HashMap<>();
		if (parent != null) {
			for (OwnedState os : StateUtil.recursiveFindStates(parent)) {
				parentStates.put(os.state.getName(), os.state);
			}
		}

		loadEntity(entity, clazz, availClasses, availStates, availScripts,
				parentStates);

		if (parent != null) {
			parent.addActor(entity);
		}

		final List<EntityDefinition> children = clazz.getChildren();
		if (children != null) {
			for (EntityDefinition def : children) {
				load(entity, def, provider);
			}
		}

		return entity;
	}

	private void loadEntity(BuilableEntity entity, EntityDefinition clazz,
			List<EntityDefinition> availClasses,
			List<StateDefinition> availStates,
			List<ScriptDefinition> availScripts,
			Map<String, State> statesFromParent) {

		String parentClazz = clazz.getParentClass();
		if (parentClazz != null) {
			boolean foundClass = false;
			for (EntityDefinition existing : availClasses) {
				if (existing.getClassName().equals(parentClazz)) {
					loadEntity(entity, existing, availClasses, availStates,
							availScripts, statesFromParent);
					foundClass = true;
					break;
				}
			}
			if (!foundClass) {
				throw new IllegalArgumentException("Unable to resolve class "
						+ parentClazz);
			}
		}
		final List<String> reqStates = clazz.getRequiredStates();
		if (reqStates != null) {

			loadStates: for (String reqState : reqStates) {

				final State fromParent = statesFromParent.get(reqState);
				if (fromParent != null && fromParent.canBeShared()) {
					entity.addPermanentState(fromParent);
					continue loadStates;
				}
				for (State existing : entity.getStates()) {
					if (existing.getName().equals(reqState)) {
						continue loadStates;
					}
				}
				for (StateDefinition avail : availStates) {
					if (avail.getName().equals(reqState)) {
						if (avail instanceof UserStateDefinition) {
							entity.addUserState((UserStateDefinition) avail);
						} else {
							entity.addPermanentState(avail.createInstance());
						}
						continue loadStates;
					}
				}
				throw new IllegalArgumentException("Unable to find state "
						+ reqState);
			}
		}
		final List<String> reqScripts = clazz.getRequiredScripts();
		if (reqScripts != null) {
			loadScripts: for (String reqScript : reqScripts) {

				for (Script existing : entity.getScripts()) {
					if (existing.getName().equals(reqScript)) {
						continue loadScripts;
					}
				}
				for (ScriptDefinition avail : availScripts) {
					if (avail.getName().equals(reqScript)) {
						entity.addScript(avail.createInstance());
						continue loadScripts;
					}
				}
				throw new IllegalArgumentException("Unable to find script "
						+ reqScript);
			}
		}

		clazz.setDefaultParameters(entity);
	}

	private static Map<String, Integer> sUidCtr = new HashMap<>();

	private class BuilableEntity extends BaseEntity {

		private final String mName;

		public BuilableEntity(String className, Entity parent) {
			super(className, parent);

			Integer prev = sUidCtr.get(className);
			if (prev == null) {
				prev = 0;
			}
			mName = className + (++prev);
			sUidCtr.put(className, prev);
		}

		@Override
		public void addPermanentState(State state) {
			super.addPermanentState(state);
		}

		@Override
		public String toString() {
			return mName;
		}
	}
}
