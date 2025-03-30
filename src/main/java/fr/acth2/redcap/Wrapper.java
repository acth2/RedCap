package fr.acth2.redcap;

import com.formdev.flatlaf.*;
import fr.acth2.redcap.utils.References;
import javax.swing.*;
import java.io.File;

import static fr.acth2.redcap.log.Logger.*;

public class Wrapper {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        log("TRYING TO START REDCAP");
        log("Logs are located in " + LOG_DIR);
        log("In case of errors consult the file redcap.log");

        log("Setting up FlatLaf themes.");
        FlatDarkLaf.setup();
        FlatLightLaf.setup();
        FlatDarculaLaf.setup();
        FlatIntelliJLaf.setup();

        startUI();
    }

    private static void startUI() throws UnsupportedLookAndFeelException {
        if (new File(System.getProperty("user.home") + "\\.redcap\\firstBooted").exists()) {
            References.startUIFromId(1);
        } else {
            log("firstBooted file not found! Loading first boot payload");
            log("Setting FlatDarkLaf as default theme");
            UIManager.setLookAndFeel(new FlatDarkLaf());
            References.startUIFromId(0);
        }
    }
}
