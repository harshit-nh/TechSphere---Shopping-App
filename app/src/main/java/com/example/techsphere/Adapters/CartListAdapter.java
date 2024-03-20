package com.example.techsphere.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.techsphere.Domain.PopularDomain;
import com.example.techsphere.Helper.ChangeNumberItemsListener;
import com.example.techsphere.Helper.ManageCart;
import com.example.techsphere.Helper.TinyDB;
import com.example.techsphere.R;


import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    ArrayList<PopularDomain> listItemSelected;
    private ManageCart manageCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartListAdapter(ArrayList<PopularDomain> listItemSelected,Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        manageCart = new ManageCart(context, new TinyDB(context));
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.ViewHolder holder, int position) {
        holder.title.setText(listItemSelected.get(position).getTitle());
        holder.feeEachItem.setText("₹"+listItemSelected.get(position).getPrice());
        holder.totalEachItem.setText("₹"+Math.round((listItemSelected.get(position).
                getNumberInCart() * listItemSelected.get(position).getPrice())));
        holder.num.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        int drawableResourceId = holder.itemView.getContext().
                getResources().getIdentifier(listItemSelected.get(position).getPicUrl(),"drawable",
                        holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,30,30))
                .into(holder.pic);


        holder.plusItem.setOnClickListener(view -> manageCart.plusNumberItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));


        holder.minusItem.setOnClickListener(view -> manageCart.minusNumberItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.change();
        }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,feeEachItem,plusItem,minusItem;
        ImageView pic;
        TextView totalEachItem, num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTxt3);
            pic= itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}
