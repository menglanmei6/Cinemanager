package net.lzzy.cinemanager.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.models.OrderFactory;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.simpledatepicker.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by lzzy_gxy on 2019/3/27.
 * Description:
 */
public class AddOrderFragment extends BaseFragment {
    private OnFragmentInteractionList listener;
    private TextView tvDate;
    private EditText etName;
    private Spinner spCinema;
    private ImageView imgQRCode;
    private EditText edtPrice;
    private List<Order> orders;
    private List<Cinema> cinemas;
    private OrderFactory factory = OrderFactory.getInstance();
    private CustomDatePicker picker;
    private Order order;
    private orderCreatedListener orderCreatedListener;


    @Override
    protected void populate() {
        listener.hideSearch();
        tvDate = find(R.id.fragment_add_tv_Time);
        etName = find(R.id.fragment_add_order_edt_name);
        edtPrice = find(R.id.fragment_add_order_price);
        spCinema = find(R.id.fragment_add_order_spinner);
        imgQRCode = find(R.id.fragment_main_Img_code);
        bindList();
        addListener();
        showAndOrders();

    }

    private void showAndOrders() {
        cinemas = CinemaFactory.getInstance().get();
        orders = factory.get();
        spCinema.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, cinemas));
        find(R.id.fragment_add_order_Time).setOnClickListener(v -> picker.show(tvDate.getText().toString()));
        initDatePicker();


    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        tvDate.setText(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        String end = sdf.format(calendar.getTime());
        picker = new CustomDatePicker(getContext(), s -> tvDate.setText(s), now, end);
        picker.showSpecificTime(true);
        picker.setIsLoop(true);

    }

    private void clickOrder(Order order) {
        Cinema cinema = CinemaFactory.getInstance().getById(order.getCinemaId().toString());
        String content = "[" + order.getMovie() + "]" + order.getMovieTime() + "\n" + cinema.toString() + "票价" + order.getPrice() + "元";
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_order, null);
        ImageView img = view.findViewById(R.id.dialog_code_img);
        img.setImageBitmap(AppUtils.createQRCodeBitmap(content, 300, 300));
        new AlertDialog.Builder(getActivity()).setView(view).show();
    }


    private void addListener() {
        find(R.id.fragment_add_order_btn_cancel).setOnClickListener(v -> {
                    orderCreatedListener.cancelAddOrder();
                }
        );
        find(R.id.fragment_add_order_btn_save).setOnClickListener(v -> {

            String name = etName.getText().toString();
            String strPrice = edtPrice.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(strPrice)) {
                Toast.makeText(getContext(), "需要完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            float price;
            try {
                price = Float.parseFloat(strPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "价格错误", Toast.LENGTH_SHORT).show();
                return;
            }
            Order order = new Order();
            Cinema cinema = cinemas.get(spCinema.getSelectedItemPosition());
            order.setMovieTime(tvDate.getText().toString());
            order.setMovie(name);
            order.setCinemaId(cinema.getId());
            order.setPrice(price);
            //  adapter.add(order);
            etName.setText("");
            orderCreatedListener.saveOrder(order);
        });

        find(R.id.fragment_add_order_btn_code).setOnClickListener(v -> {
            String name = etName.getText().toString();
            String price = edtPrice.getText().toString();
            String location = spCinema.getSelectedItem().toString();
            String time = tvDate.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(getActivity(), "需要完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            String content = "[" + name + "]" + time + "\n" + location + "票价" + price + "元";
            imgQRCode.setImageBitmap(AppUtils.createQRCodeBitmap(content, 200, 200));
        });
        imgQRCode.setOnLongClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) imgQRCode.getDrawable()).getBitmap();
            Toast.makeText(getActivity(), AppUtils.readQRCode(bitmap), Toast.LENGTH_SHORT).show();
            return true;
        });
    }


    private void bindList() {
        factory = OrderFactory.getInstance();
        orders = factory.get();
    }


    @Override
    public void onResume() {
        super.onResume();
        listener.hideSearch();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_add_orders;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnFragmentInteractionList) context;
            orderCreatedListener= (orderCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "必须实现OnFragmentInteractionListener");
        }


    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            listener.hideSearch();
        }
    }

    @Override
    public void hideSearch() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
        orderCreatedListener = null;
    }

    public interface orderCreatedListener {
        void cancelAddOrder();

        void saveOrder(Order order);


    }

}




