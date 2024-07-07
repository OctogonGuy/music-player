module tech.octopusdragon.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires transitive jaudiotagger;
    requires org.apache.commons.io;
    requires java.desktop;


    opens tech.octopusdragon.musicplayer to javafx.fxml;
    opens tech.octopusdragon.musicplayer.components to javafx.fxml;
    opens tech.octopusdragon.musicplayer.scenes to javafx.fxml;
    opens tech.octopusdragon.musicplayer.window to javafx.fxml;
    exports tech.octopusdragon.musicplayer;
    exports tech.octopusdragon.musicplayer.components;
    exports tech.octopusdragon.musicplayer.scenes;
    exports tech.octopusdragon.musicplayer.window;
}