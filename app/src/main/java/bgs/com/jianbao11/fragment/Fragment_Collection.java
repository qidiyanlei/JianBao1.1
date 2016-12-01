package bgs.com.jianbao11.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import bgs.com.jianbao11.R;


/**
 * Created by 醇色 on 2016/11/25.
 * 收藏
 */

public class Fragment_Collection extends Fragment {
    private GridView mFrag_collection_gv;
    private ImageView mFrag_collection_out;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=View.inflate(getActivity(), R.layout.fragment_collection,null);
        return v;
    }
}
