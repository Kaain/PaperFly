package de.fhb.mi.paperfly.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.fhb.mi.paperfly.R;

/**
 * Creates Navigation and NavigationItems
 *
 * @author Christoph Ott
 */
public class NavListAdapter extends ArrayAdapter<NavItemModel> {

    public NavListAdapter(Context context) {
        super(context, 0);
    }

    /**
     * adds a NavigationHeader
     *
     * @param title
     */
    public void addHeader(String title) {
        add(new NavItemModel(title, -1, true));
    }

    /**
     * adds a NavigationItem
     *
     * @param key
     * @param title
     * @param iconID
     */
    public void addItem(NavKey key, String title, int iconID) {
        add(new NavItemModel(key, title, iconID));
    }

    /**
     * adds a NavigationItem
     *
     * @param key
     * @param title
     * @param iconID
     * @param isVisible
     */
    public void addItem(NavKey key, String title, int iconID, boolean isVisible) {
        add(new NavItemModel(key, title, iconID, false, isVisible));
    }

    /**
     * adds a NavigationItem
     *
     * @param itemModel
     */
    public void addItem(NavItemModel itemModel) {
        add(itemModel);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavItemModel item = getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if (view == null) {
            int layout;
            if (item.isHeader()) {
                layout = R.layout.drawer_nav_row_header;
            } else {
                layout = R.layout.drawer_nav_row_item;
            }
            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView text = (TextView) view.findViewById(R.id.nav_text);
            ImageView icon = (ImageView) view.findViewById(R.id.nav_icon);
            view.setTag(new ViewHolder(item.getKey(), text, icon));

        }

        if (holder == null && view != null) {
            if (item.isVisible()) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if (item != null && holder != null) {
            if (holder.textHolder != null)
                holder.textHolder.setText(item.getTitle());

            if (holder.imageHolder != null) {
                if (item.getIconID() > 0) {

                    holder.imageHolder.setVisibility(View.VISIBLE);
                    holder.imageHolder.setImageResource(item.getIconID());
                } else {
                    holder.imageHolder.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return !getItem(position).isHeader();
    }

    /**
     * Holds the images of the navigationList,
     * beware of reloads images and texts every time
     */
    public static class ViewHolder {
        public final NavKey key;
        public final TextView textHolder;
        public final ImageView imageHolder;

        public ViewHolder(NavKey key, TextView text, ImageView image) {
            this.key = key;
            this.textHolder = text;
            this.imageHolder = image;
        }
    }
}
