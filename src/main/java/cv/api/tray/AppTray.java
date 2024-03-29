package cv.api.tray;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Slf4j
public class AppTray {

    private static TrayIcon trayIcon;
    private final Font menuFont = new Font("Arial", Font.BOLD, 12);
    private SystemTray tray;

    public static void showMessage(String message) {
        trayIcon.displayMessage("Core Cloud Service.", message, TrayIcon.MessageType.INFO);
    }

    public void startup() {
        if (SystemTray.isSupported()) {
            log.info("Tray started.");
            tray = SystemTray.getSystemTray();
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/icon.png")));
            PopupMenu menu = new PopupMenu();
            MenuItem closeItem = new MenuItem("Exit");
            closeItem.setFont(menuFont);
            closeItem.addActionListener((e) -> {
                tray.remove(trayIcon);
                System.exit(0);
            });
            menu.add(closeItem);
            trayIcon = new TrayIcon(icon.getImage(), "Core Inventory Api", menu);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Core Value Notification.", "Welcome.", TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                log.error(String.format("startup: %s", e.getMessage()));
            }
        }
    }
}
