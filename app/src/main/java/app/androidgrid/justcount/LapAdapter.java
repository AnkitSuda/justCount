package app.androidgrid.justcount;

import android.bluetooth.le.AdvertisingSetParameters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SUDA on 13-09-2017.
 */

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {

    private Context context;
    private List<LapModel> lapModels;
    private int lastPosition = -1;

    public LapAdapter(Context context, List<LapModel> lapModels) {
        this.context = context;
        this.lapModels = lapModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_lap_layout, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LapModel lapModel = lapModels.get(position);

        holder.textPosition.setText(lapModel.getLapPosition());
        holder.textTime.setText(lapModel.getLapTime());
        holder.textPlus.setText(lapModel.getLapPlusTime());

        setFadeAnimation(holder.mView, position);

    }

    @Override
    public int getItemCount() {
        return lapModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textPosition, textTime, textPlus;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            textPlus = mView.findViewById(R.id.lap_plus_time_text);
            textPosition = mView.findViewById(R.id.lap_position_text);
            textTime = mView.findViewById(R.id.lap_on_time_text);
        }

        public void clearAnimation()
        {
            mView.clearAnimation();
        }
    }

    private void setFadeAnimation(View view, int position) {
        if (position > lastPosition) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            view.startAnimation(anim);
            lastPosition = position;
        }

    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        ((ViewHolder)holder).clearAnimation();
    }
}
