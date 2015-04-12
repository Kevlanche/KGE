package com.kevlanche.engine.game.state.value.variable;


public interface ObservableVariable extends Variable{

	public interface ChangeListener {
		void onChanged(ObservableVariable var);
	}
	
	void setChangeListener(ChangeListener listener);
}
