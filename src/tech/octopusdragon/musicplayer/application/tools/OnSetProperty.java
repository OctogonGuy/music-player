package tech.octopusdragon.musicplayer.application.tools;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;

/**
 * A property that fires events that can be listened to when mutated; that means
 * even if the new value is the same as the old value, the event will still
 * occur
 * @author Alex Gill
 *
 * @param <T> type
 */
public class OnSetProperty<T> extends SimpleObjectProperty<T> {
	
	List<MutateListener<T>> mutateListeners;
	
	/**
	 * Default constructor
	 */
	public OnSetProperty() {
		this(null);
	}
	
	/**
	 * Overloaded constructor
	 * @param defaultValue The default value
	 */
	public OnSetProperty(T defaultValue) {
		super(defaultValue);
		mutateListeners = new ArrayList<MutateListener<T>>();
	}
	
	@Override
	public void set(T newValue) {
		T oldValue = this.get();
		super.set(newValue);
		for (MutateListener<T> mutateListener : mutateListeners) {
			if (mutateListener != null) {
				mutateListener.mutated(oldValue, newValue);
			}
		}
	}
	
	/**
	 * Adds a mutate listener
	 * @param mutateListener mutate listeener to add
	 */
	public void addMutateListener(MutateListener<T> mutateListener) {
		mutateListeners.add(mutateListener);
	}
	
	/**
	 * Removes a mutate listener
	 * @param mutateListener mutate listener to remove
	 */
	public void removeMutateListener(MutateListener<T> mutateListener) {
		mutateListeners.remove(mutateListener);
	}
	
	/**
	 * Interface controlling the listeners
	 * @author Alex Gill
	 *
	 * @param <T> type
	 */
	public interface MutateListener<T> {
		void mutated(T oldValue, T newValue);
	}
}
