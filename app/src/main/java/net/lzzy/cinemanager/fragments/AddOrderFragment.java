package net.lzzy.cinemanager.fragments;

import net.lzzy.cinemanager.R;

/**
 * Created by lzzy_gxy on 2019/3/27.
 * Description:
 */
public class AddOrderFragment extends BaseFragment {

    @Override
    protected void populate() {
        getView().findViewById(R.id.fragment_add_order_edt);


    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_add_orders;
    }
}
