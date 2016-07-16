package in.mbm.espectro.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.mbm.espectro.R;
import in.mbm.espectro.model.Notification;

/**
 * Created by tarunsmalviya12 on 17/4/16.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Variables.
     */
    static final int TYPE_DEFAULT = 0;

    private Context context;
    private ArrayList<Notification> NOTIFICATION;

    public NotificationAdapter(Context context, ArrayList<Notification> notification) {
        this.context = context;
        this.NOTIFICATION = notification;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return NOTIFICATION.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        switch (viewType) {
            case TYPE_DEFAULT: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_DEFAULT: {
                // Retrieving cell elements from item_notification.xml.
                TextView messageTxt = (TextView) holder.itemView.findViewById(R.id.messageTxt);
                TextView timeTxt = (TextView) holder.itemView.findViewById(R.id.timeTxt);

                if (position % 2 == 0)
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white20));
                else
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                // Setting up information.
                messageTxt.setText(NOTIFICATION.get(position).getMessage());

                try {
                    SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date schedule = fd.parse(NOTIFICATION.get(position).getDate_time());

                    SimpleDateFormat date = new SimpleDateFormat("dd MMMM, yyyy");
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                    timeTxt.setText(date.format(schedule) + " at " + time.format(schedule).toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }
}
