package com.ats.monginis_cash_mgmt.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ats.monginis_cash_mgmt.R;
import com.ats.monginis_cash_mgmt.adapter.ComparativeReportAdapter;

import java.util.ArrayList;

public class ComparativeReportFragment extends Fragment {

    private ListView lvReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparative_report, container, false);
        getActivity().setTitle("Comparative Report");

        ArrayList<String> nameArray = new ArrayList<>();
        nameArray.add("Anmol Shirke");
        nameArray.add("Sachin Thackre");
        nameArray.add("Sagar Shinde");
        nameArray.add("Ankit More");

        lvReport = view.findViewById(R.id.lvComparativeReport);
        ComparativeReportAdapter adapter = new ComparativeReportAdapter(getContext(), nameArray);
        lvReport.setAdapter(adapter);

        return view;
    }

}
