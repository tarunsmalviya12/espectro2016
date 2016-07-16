package in.mbm.espectro.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import in.mbm.espectro.EventDetail;
import in.mbm.espectro.R;
import in.mbm.espectro.model.Event;
import in.mbm.espectro.utils.URLS;

/**
 * Created by tarunsmalviya12 on 22/11/15.
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Variables.
     */
    static final int TYPE_DEFAULT = 0;

    private Context context;
    private ArrayList<Event> EVENT;

    public EventAdapter(Context context, ArrayList<Event> event) {
        this.context = context;
        this.EVENT = event;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return EVENT.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        switch (viewType) {
            case TYPE_DEFAULT: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_event, parent, false);
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
                // Retrieving cell elements from item_event.xml.
                ImageView eventImg = (ImageView) holder.itemView.findViewById(R.id.eventImg);

                TextView nameTxt = (TextView) holder.itemView.findViewById(R.id.nameTxt);
                TextView infoTxt = (TextView) holder.itemView.findViewById(R.id.infoTxt);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, EventDetail.class);
                        intent.putExtra("id", EVENT.get(position).getId());
                        intent.putExtra("name", EVENT.get(position).getName());
                        intent.putExtra("info", EVENT.get(position).getInfo());
                        intent.putExtra("time", EVENT.get(position).getEvent_time());
                        intent.putExtra("venue", EVENT.get(position).getVenue());
                        intent.putExtra("inter", EVENT.get(position).getInter());
                        intent.putExtra("intra", EVENT.get(position).getIntra());
                        intent.putExtra("desc", EVENT.get(position).getDesc());
                        context.startActivity(intent);
                    }
                });

                // Setting up information.
                ImageLoader.getInstance().displayImage(URLS.EVENT_IMG_URL + EVENT.get(position).getId() + ".jpg", eventImg);

                nameTxt.setText(EVENT.get(position).getName().toUpperCase());
                infoTxt.setText(EVENT.get(position).getInfo());
            }
            break;
        }
    }
}
