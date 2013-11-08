package de.fhb.mi.paperfly.navigation;

/**
 * Class for holding information about a navigation item.
 *
 * @author Christoph Ott
 */
public class NavItemModel {
    private NavKey key;
    private String title;
    private int iconID;
    private boolean isHeader;

    public NavItemModel(NavKey key, String title, int iconID, boolean isHeader) {
        this.key = key;
        this.title = title;
        this.iconID = iconID;
        this.isHeader = isHeader;
    }

    public NavItemModel(String title, int iconID, boolean isHeader) {
        this(NavKey.HEADER, title, iconID, isHeader);
    }

    public NavItemModel(NavKey key, String title, int icon) {
        this(key, title, icon, false);
    }

    public NavKey getKey() {
        return key;
    }

    public void setKey(NavKey key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }
}
