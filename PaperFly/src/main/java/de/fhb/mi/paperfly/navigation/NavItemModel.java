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
    private int title;
    private int icon;
    private boolean isHeader;

    public NavItemModel(int title, int icon, boolean isHeader) {
        this.title = title;
        this.icon = icon;
        this.isHeader = isHeader;
    }

    public NavItemModel(int title, int icon) {
        this(title, icon, false);
    }
}
