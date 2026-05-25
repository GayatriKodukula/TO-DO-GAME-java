package todogame.ui;

import todogame.Task;
import java.awt.*;

public final class Theme {
    private Theme() {}

    // Palette
    public static final Color BG_DEEP       = new Color(0x08, 0x0D, 0x1A);
    public static final Color BG_PANEL      = new Color(0x10, 0x18, 0x2E);
    public static final Color BG_CARD       = new Color(0x16, 0x22, 0x3E);
    public static final Color BG_HOVER      = new Color(0x1E, 0x30, 0x55);
    public static final Color BG_INPUT      = new Color(0x0B, 0x13, 0x22);
    public static final Color BG_OWNED      = new Color(0x08, 0x20, 0x14);

    public static final Color COIN_GOLD     = new Color(0xFF, 0xC8, 0x00);
    public static final Color COIN_AMBER    = new Color(0xFF, 0x90, 0x00);
    public static final Color GEM_CYAN      = new Color(0x00, 0xE5, 0xFF);
    public static final Color GEM_PURPLE    = new Color(0xBB, 0x86, 0xFC);

    public static final Color ACCENT_GREEN  = new Color(0x2E, 0xCC, 0x4E);
    public static final Color ACCENT_RED    = new Color(0xFF, 0x45, 0x65);
    public static final Color ACCENT_BLUE   = new Color(0x4A, 0x9C, 0xFF);

    public static final Color TEXT_PRIMARY  = new Color(0xEC, 0xF2, 0xFF);
    public static final Color TEXT_SECONDARY= new Color(0x78, 0x8A, 0xAA);
    public static final Color TEXT_MUTED    = new Color(0x35, 0x48, 0x62);
    public static final Color BORDER        = new Color(0x1C, 0x2D, 0x46);
    public static final Color BORDER_BRIGHT = new Color(0x2C, 0x44, 0x68);

    public static Color priorityColor(Task.Priority p) {
        return switch (p) {
            case HIGH   -> ACCENT_RED;
            case MEDIUM -> COIN_AMBER;
            case LOW    -> ACCENT_BLUE;
        };
    }

    // Fonts
    public static final Font FONT_TITLE   = new Font("Monospaced", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Monospaced", Font.BOLD,  13);
    public static final Font FONT_BODY    = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Monospaced", Font.PLAIN, 11);
    public static final Font FONT_BADGE   = new Font("Monospaced", Font.BOLD,  11);

    // Geometry
    public static final int PAD_SM = 6, PAD_MD = 12, PAD_LG = 20;
    public static final int RADIUS = 12, ROW_H = 64;
}
