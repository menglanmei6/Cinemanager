package net.lzzy.cinemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hp.hpl.sparta.xpath.ParentNodeTest;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by lzzy_gxy on 2019/3/26.
 * Description:
 */
public class CinemasFragment extends BaseFragment {

    private List<Cinema> cinemas;
    public static final String CINEMA="cinema";
    private CinemaFactory factory=CinemaFactory.getInstance();
    private ListView lv;
    private Cinema cinema;
    private GenericAdapter<Cinema> adapter;
    private OnCinemaCreatedListener listener;


    public static CinemasFragment newInstance(Cinema cinema) {
        CinemasFragment fragment=new CinemasFragment();
        Bundle args=new Bundle();
        args.putParcelable(CINEMA,cinema);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            Cinema cinema=getArguments().getParcelable(CINEMA);
            this.cinema=cinema;
        }
    }

    @Override
    protected void populate() {
       lv=find(R.id.activity_cinema_lv);
       View empty=find(R.id.activity_cinemas_tv_none);
       lv.setEmptyView(empty);
       cinemas=factory.get();
        adapter = new GenericAdapter<Cinema>(getActivity(),
                R.layout.cinema_item,cinemas) {
            @Override
            public void populate(ViewHolder holder, Cinema cinema) {
                holder.setTextView(R.id.cinemas_items_tv_name,cinema.getName())
                        .setTextView(R.id.cinemas_items_tv_location,cinema.getLocation());

            }

            @Override
            public boolean persistInsert(Cinema cinema) {
                return factory.addCinema(cinema);
            }

            @Override
            public boolean persistDelete(Cinema cinema) {
                return factory.deleteCinema(cinema);
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(((parent, view, position, id) ->
                listener.onCinemaSelected(adapter.getItem(position).getId().toString())));
        if (cinema!=null){
            save(cinema);
        }

    }

    public void save(Cinema cinema) {
        adapter.add(cinema);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cinemas;
    }

    @Override
    public void search(String kw) {
        cinemas.clear();
        if (TextUtils.isEmpty(kw)){
            cinemas.addAll(factory.get());
        }else {
            cinemas.addAll(factory.searchCinemas(kw));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void hideSearch() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (OnCinemaCreatedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"必须实现OnCinemaCreatedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public interface OnCinemaCreatedListener{
        void onCinemaSelected(String cinemaId);
    }

}


