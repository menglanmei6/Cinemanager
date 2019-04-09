package net.lzzy.cinemanager.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.models.OrderFactory;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/3.
 * Description:
 */
public class CinemaOrdersFragment extends   BaseFragment {
    private static final String ARG_CINEMA_AD="argCinemaId";
    private String cinemaId;
    private List<Order> orders;
    private GenericAdapter<Order> adapter;

    public static CinemaOrdersFragment newInstance(String cinemaId){
        CinemaOrdersFragment fragment=new CinemaOrdersFragment();
        Bundle args=new Bundle();
        args.putString(ARG_CINEMA_AD,cinemaId);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            cinemaId=getArguments().getString(ARG_CINEMA_AD);
        }
    }

    @Override
    protected void populate() {
        ListView lv = find(R.id.fragment_container_order_lv);
        View empty=find(R.id.fragment_container_tv_none);
        lv.setEmptyView(empty);
        orders = OrderFactory.getInstance().getOrdersByCinema(cinemaId);
        adapter = new GenericAdapter<Order>(getActivity(),R.layout.order_item, orders) {
            @Override
            public void populate(ViewHolder holder, Order order) {
                holder.setTextView(R.id.order_items_name, order.getMovie())
                        .setTextView(R.id.order_items_location, order.getMovieTime());

            }

            @Override
            public boolean persistInsert(Order order) {
                return false;
            }

            @Override
            public boolean persistDelete(Order order) {
                return false;
            }
        };
        lv.setAdapter(adapter);

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_cinema_orders;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void hideSearch() {

    }
}
