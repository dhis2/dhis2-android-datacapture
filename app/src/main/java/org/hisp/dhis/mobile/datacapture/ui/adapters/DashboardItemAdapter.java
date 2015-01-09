package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.hisp.dhis.mobile.datacapture.R;
import org.hisp.dhis.mobile.datacapture.api.android.models.DBItemHolder;
import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;
import org.hisp.dhis.mobile.datacapture.api.models.DashboardItem;
import org.hisp.dhis.mobile.datacapture.utils.DateTimeTypeAdapter;
import org.hisp.dhis.mobile.datacapture.utils.PicassoProvider;
import org.joda.time.DateTime;

public class DashboardItemAdapter extends DBBaseAdapter<DashboardItem> {
    private static final String DATE_FORMAT = "YYYY-MM-dd";
    private static final int MENU_GROUP_ID = 935482352;
    private static final int MENU_ITEM_ID = 893226352;
    private static final int MENU_ITEM_ORDER = 100;

    private OnItemClickListener mOnClickListener;
    private Picasso mImageLoader;
    private Transformation mImageTransformation;
    private String mServerUrl;

    public DashboardItemAdapter(Context context) {
        super(context);

        mImageLoader = PicassoProvider.getInstance(context);
        mImageTransformation = new ImgTransformation();
        mServerUrl = DHISManager.getInstance().getServerUrl();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = getInflater().inflate(R.layout.gridview_dashboard_item, parent, false);

            View itemViewButton = view.findViewById(R.id.dashboard_item_menu);
            PopupMenu menu = new PopupMenu(getContext(), itemViewButton);
            menu.getMenu().add(MENU_GROUP_ID, MENU_ITEM_ID,
                    MENU_ITEM_ORDER, R.string.share_interpretation);

            holder = new ViewHolder(
                    view.findViewById(R.id.dashboard_item_body_container),
                    (TextView) view.findViewById(R.id.dashboard_item_name),
                    (ImageView) view.findViewById(R.id.dashboard_item_image),
                    (TextView) view.findViewById(R.id.dashboard_item_text),
                    (TextView) view.findViewById(R.id.dashboard_item_last_updated),
                    (ImageButton) itemViewButton, menu
            );

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        DashboardItem dashboardItem = ((DBItemHolder<DashboardItem>) getItem(position)).getItem();
        handleDashboardItems(dashboardItem, holder);
        return view;
    }

    public void handleDashboardItems(final DashboardItem item, final ViewHolder holder) {
        if (item == null) {
            return;
        }

        String lastUpdated = "";
        if (item.getLastUpdated() != null) {
            DateTime dateTime = DateTimeTypeAdapter.deserializeDateTime(item.getLastUpdated());
            lastUpdated = dateTime.toString(DATE_FORMAT);
        }

        holder.lastUpdated.setText(lastUpdated);
        if (DashboardItem.TYPE_CHART.equals(item.getType()) && item.getChart() != null) {
            String request = mServerUrl + "/api/charts/" + item.getChart().getId() + "/data.png";
            handleItemsWithImages(item.getChart().getName(), request, holder);
            holder.itemMenuButton.setVisibility(View.VISIBLE);
        } else if (DashboardItem.TYPE_MAP.equals(item.getType()) && item.getMap() != null) {
            String request = mServerUrl + "/api/maps/" + item.getMap().getId() + "/data.png";
            handleItemsWithImages(item.getMap().getName(), request, holder);
            holder.itemMenuButton.setVisibility(View.VISIBLE);
        } else if (DashboardItem.TYPE_EVENT_CHART.equals(item.getType()) && item.getEventChart() != null) {
            String request = mServerUrl + "/api/eventCharts/" + item.getEventChart().getId() + "/data.png";
            handleItemsWithImages(item.getEventChart().getName(), request, holder);
            holder.itemMenuButton.setVisibility(View.GONE);
        } else if (DashboardItem.TYPE_REPORT_TABLE.equals(item.getType()) && item.getReportTable() != null) {
            handleItemsWithoutImages(item.getReportTable().getName(), holder);
            holder.itemMenuButton.setVisibility(View.VISIBLE);
        } else if (DashboardItem.TYPE_USERS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.users), holder);
            holder.itemMenuButton.setVisibility(View.GONE);
        } else if (DashboardItem.TYPE_REPORTS.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.reports), holder);
            holder.itemMenuButton.setVisibility(View.GONE);
        } else if (DashboardItem.TYPE_RESOURCES.equals(item.getType())) {
            handleItemsWithoutImages(getContext().getString(R.string.resources), holder);
            holder.itemMenuButton.setVisibility(View.GONE);
        }

        holder.itemBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(item);
                }
            }
        });

        holder.itemMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.popupMenu != null) {
                    holder.popupMenu.show();
                }
            }
        });

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == MENU_ITEM_ID) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onItemShareInterpretation(item);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handleItemsWithImages(String name, String request, ViewHolder holder) {
        holder.itemName.setVisibility(View.VISIBLE);
        holder.itemImage.setVisibility(View.VISIBLE);
        holder.itemText.setVisibility(View.GONE);

        holder.itemName.setText(name);
        mImageLoader.load(request)
                //.resize(160, 100)
                .transform(mImageTransformation)
                        //.centerInside()
                        //.fit()
                        //.centerCrop()
                .placeholder(R.drawable.stub_dashboard_background)
                .into(holder.itemImage);
    }

    private void handleItemsWithoutImages(String text, ViewHolder holder) {
        holder.itemImage.setVisibility(View.GONE);
        holder.itemName.setVisibility(View.GONE);
        holder.itemText.setVisibility(View.VISIBLE);

        holder.itemText.setText(text);
    }

    public interface OnItemClickListener {
        public void onItemClick(DashboardItem dashboardItem);
        public void onItemShareInterpretation(DashboardItem dashboardItem);
    }

    private static class ViewHolder {
        final View itemBody;
        final TextView itemName;
        final TextView itemText;
        final TextView lastUpdated;
        final ImageView itemImage;
        final ImageButton itemMenuButton;
        final PopupMenu popupMenu;

        ViewHolder(View itemBody,
                   TextView itemName,
                   ImageView itemImage,
                   TextView itemText,
                   TextView lastUpdated,
                   ImageButton itemMenuButton,
                   PopupMenu popupMenu) {
            this.itemBody = itemBody;
            this.itemName = itemName;
            this.itemImage = itemImage;
            this.itemText = itemText;
            this.lastUpdated = lastUpdated;
            this.itemMenuButton = itemMenuButton;
            this.popupMenu = popupMenu;
        }
    }
}
