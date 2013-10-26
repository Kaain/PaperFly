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
 * @author Christoph Ott
 */
public class NavListAdapter extends ArrayAdapter<NavItemModel> {

    public NavListAdapter(Context context) {
        super(context, 0);
    }

    public void addHeader(int title) {
        add(new NavItemModel(title, -1, true));
    }

    public void addItem(int title, int icon) {
        add(new NavItemModel(title, icon, false));
    }

    public void addItem(NavItemModel itemModel) {
        add(itemModel);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isHeader() ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEnabled(int position) {
        return !getItem(position).isHeader();
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
            view.setTag(new ViewHolder(text, icon));
        }

        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if (item != null && holder != null) {
            if (holder.textHolder != null)
                holder.textHolder.setText(item.getTitle());

            if (holder.imageHolder != null) {
                if (item.getIcon() > 0) {

                    holder.imageHolder.setVisibility(View.VISIBLE);
                    holder.imageHolder.setImageResource(item.getIcon());
                } else {
                    holder.imageHolder.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }

    public static class ViewHolder {
        public final TextView textHolder;
        public final ImageView imageHolder;

        public ViewHolder(TextView text1, ImageView image1) {
            this.textHolder = text1;
            this.imageHolder = image1;
        }
    }
}
