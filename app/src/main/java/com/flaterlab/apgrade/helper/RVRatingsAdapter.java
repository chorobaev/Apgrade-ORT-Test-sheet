package com.flaterlab.apgrade.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flaterlab.apgrade.R;
import com.flaterlab.apgrade.model.TestResult;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVRatingsAdapter extends RecyclerView.Adapter<RVRatingsAdapter.RatingViewHolder> {

    private final String TAG = "MyRVTestAdapter";
    private Context mContext;
    private ArrayList<TestResult> results;

    public RVRatingsAdapter(Context context) {
        mContext = context;
        results = new ArrayList<>();
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rating_item_layout, parent, false);

        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        TestResult result = results.get(position);

        holder.tvNumeration.setText(String.valueOf((position + 1) + "."));
        holder.tvFullName.setText(result.getUserFullName());
        holder.tvExtraInfo.setText(result.getRegionAndSchool());
        holder.tvMarks.setText(String.valueOf((int) result.getTotalMarks()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void updateData(ArrayList<TestResult> results) {
        if (results == null) {
            this.results = new ArrayList<>();
        } else  {
            this.results = results;
        }
        notifyDataSetChanged();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeration;
        TextView tvFullName;
        TextView tvExtraInfo;
        TextView tvMarks;

        RatingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNumeration = itemView.findViewById(R.id.tv_numeration);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvExtraInfo = itemView.findViewById(R.id.tv_extra_info);
            tvMarks = itemView.findViewById(R.id.tv_total_marks);

            itemView.setClickable(true);

            itemView.setOnClickListener(view -> {
                // TODO: implement click event
            });
        }

    }
}
