package tech.octopusdragon.musicplayer.components;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;

/**
 * A toggle button that is not automatically selected when clicked
 * @author Alex Gill
 *
 */
public class ManualToggleButton extends ToggleButton {
	
	public ManualToggleButton() {
		super();
		this.getStyleClass().add("button");
	}
	
	@Override
	public void fire() {
		if (!isDisabled()) {
        	//setSelected(!isSelected()); <- Would ordinarily be here
            fireEvent(new ActionEvent());
		}
	}
}
