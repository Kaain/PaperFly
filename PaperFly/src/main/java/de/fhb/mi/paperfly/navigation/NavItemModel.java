package de.fhb.mi.paperfly.navigation;

import lombok.Getter;
import lombok.Setter;

/**
 * Class for holding information about a navigation item.
 *
 * @author Christoph Ott
 */
@Getter
@Setter
public class NavItemModel {
    private NavKey key;
    private String title;
    private int iconID;
    private boolean isHeader;
    private boolean isVisible;

    public NavItemModel(NavKey key, String title, int iconID, boolean isHeader, boolean isVisible) {
        this.key = key;
        this.title = title;
        this.iconID = iconID;
        this.isHeader = isHeader;
        this.isVisible = isVisible;
    }

    public NavItemModel(String title, int iconID, boolean isHeader) {
        this(NavKey.HEADER, title, iconID, isHeader, true);
    }

    public NavItemModel(NavKey key, String title, int icon) {
        this(key, title, icon, false, true);
    }
}
