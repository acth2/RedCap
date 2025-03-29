package fr.acth2.redcap.utils;

import fr.acth2.redcap.application.firstlog.FirstLogFrame;

import javax.swing.*;

import java.util.Random;

import static fr.acth2.redcap.log.Logger.*;

public class References {
    private static final Random random = new Random();

    public static void startUIFromId(int id) {
        log("Invoking startUIFromId function with ID " + id);

        switch (id) {
            case 0:
                SwingUtilities.invokeLater(() -> {
                    FirstLogFrame firstLogFrame = new FirstLogFrame(random.nextInt(3));
                });
                break;

            case 1:
                SwingUtilities.invokeLater(() -> {
                    // Nothing for now
                });
                break;
        }
    }
}
