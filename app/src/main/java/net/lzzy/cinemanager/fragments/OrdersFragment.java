package net.lzzy.cinemanager.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.telecom.Connection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.models.OrderFactory;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.List;


/**
 * Created by lzzy_gxy on 2019/3/26.
 * Description:
 */
public class OrdersFragment extends BaseFragment {
    private static final String ARG_NEW_ORDER = "100";
    private ListView lv;
    private List<Order> orders;
    private OrderFactory factory = OrderFactory.getInstance();
    private GenericAdapter<Order> adapter;
    private Order order;
    private boolean isDeleter = false;
    private final float MIN_DELETE = 100;

    private float touchX1;
    private float touchX2;


    public static OrdersFragment newInstance(Order order) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEW_ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = getArguments().getParcelable(ARG_NEW_ORDER);
            this.order = order;
        }
    }
    //未重构前的写法
//    public OrderFragment(){}
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//       View view=inflater.inflate(R.layout.fragment_order,null);
//       TextView textView=view.findViewById(R.id.fragment_order_tv);
//        return view;
//    }

    @Override
    protected void populate() {
        lv = find(R.id.fragment_order_lv);
        View empty = find(R.id.activity_main_tv_none);
        lv.setEmptyView(empty);
        orders = factory.get();
        adapter = new GenericAdapter<Order>(getContext(), R.layout.order_item, orders) {
            @Override
            public void populate(ViewHolder viewHolder, Order order) {
                String location = String.valueOf(CinemaFactory.getInstance().getById(order.getCinemaId().toString()));
                viewHolder.setTextView(R.id.order_items_name, order.getMovie())
                        .setTextView(R.id.order_items_location, location);
                Button btn = viewHolder.getView(R.id.main_item_btn);
                btn.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                        .setTitle("删除确认")
                        .setMessage("确定删除吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialog, which) -> {
                            isDeleter = false;
                            adapter.remove(order);
                        })
                        .show());
                int visible = isDeleter ? View.VISIBLE : View.GONE;
                btn.setVisibility(visible);

                viewHolder.getConvertView().setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        slideToDelete(event, order, btn);
                        return true;
                    }
                });
            }

            @Override
            public boolean persistInsert(Order order) {
                return factory.addOrder(order);
            }

            @Override
            public boolean persistDelete(Order order) {
                return factory.delete(order);
            }
        };
        lv.setAdapter(adapter);
        if (order != null) {
            save(order);
        }
    }

    private void slideToDelete(MotionEvent event, Order order, Button button) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                touchX2 = event.getX();
                if (touchX1 - touchX2 > MIN_DELETE) {
                    if (!isDeleter) {
                        button.setVisibility(View.VISIBLE);
                        isDeleter = true;
                    }
                } else {
                    if (button.isShown()) {
                        button.setVisibility(View.GONE);
                        isDeleter = false;
                    } else {
                        clickOrder(order);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void clickOrder(Order order) {
        Cinema cinema = CinemaFactory.getInstance().getById(order.getCinemaId().toString());
        String content = "[" + order.getMovie() + "]" + order.getMovieTime() + "\n" + cinema.toString() + "票价" + order.getPrice() + "元";
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_qrcode, null);
        ImageView img = view.findViewById(R.id.dialog_code_img);
        img.setImageBitmap(AppUtils.createQRCodeBitmap(content, 300, 300));
        new AlertDialog.Builder(getContext()).setView(view).show();
    }

    public void save(Order order) {
        adapter.add(order);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_order;
    }

    @Override
    public void search(String kw) {
        order.clear();
        if (TextUtils.isEmpty(kw)) {
            orders.addAll(factory.get());
        } else {
            orders.addAll(factory.searchOrders(kw));
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void hideSearch() {

    }
}
