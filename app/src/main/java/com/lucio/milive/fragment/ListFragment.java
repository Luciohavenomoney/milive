package com.lucio.milive.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.lucio.milive.MainActivity;
import com.lucio.milive.R;
import com.lucio.milive.adapter.ProgramModel;
import com.lucio.milive.adapter.ProgramsAdapter;
import com.lucio.milive.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends BottomSheetDialogFragment {

    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.list)
    RecyclerView list;
    private ProgramsAdapter programsAdapter;
    private List<ProgramModel> channel = new ArrayList<>();

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvClose.setOnClickListener(v -> dismiss());
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        programsAdapter = new ProgramsAdapter(getContext());
        programsAdapter.setClickCallback((position, bean) -> {
            if(getActivity() instanceof MainActivity){
                ((MainActivity) getActivity()).refreshList(position,bean);
                dismiss();
            }
        });
        list.setAdapter(programsAdapter);
        channel.add(new ProgramModel("","原始节目列表1",""));
        channel.add(new ProgramModel("","原始节目列表2",""));
        programsAdapter.refresh(channel);
        getLocalList();
    }

    public void getLocalList(){
        List<ProgramModel> localList = JsonUtil.getAllFiles(JsonUtil.listFile,"m3u");
        if(localList!=null){
            channel.addAll(localList);
            programsAdapter.refresh(channel);
        }
    }
}