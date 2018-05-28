package com.ats.monginis_cash_mgmt.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ats.monginis_cash_mgmt.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by maxadmin on 20/12/17.
 */

public class ExpandableApprovalAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataChild;

    //  private static HashMap<Integer, String> edDataList = new HashMap<>();

    public ExpandableApprovalAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        Log.e("ExpandableListAdapter", "-------------" + listDataHeader);
        Log.e("ExpandableListAdapter", "-------------" + listDataChild);
    }


    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return this.listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.listDataChild.get(this.listDataHeader.get(i))
                .get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.exp_approval_header, null);
            ExpandableListView mExpandableListView = (ExpandableListView) viewGroup;
            mExpandableListView.expandGroup(i);
        }

        TextView headerName = view
                .findViewById(R.id.tvExpApprovalHead_Name);
        headerName.setText(headerTitle);

        TextView headerDate = view
                .findViewById(R.id.tvExpApprovalHead_Date);

//        headerDate.setText("" + getChildrenCount(i) + " items");

  /*      headerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked : " + i, Toast.LENGTH_SHORT).show();

                Log.e("CLICKED : -------", "" + getChildrenCount(i));
                Log.e("LIST : -----", "" + edDataList);

            }
        });
*/

        return view;
    }

    @Override
    public View getChildView(int i, final int i1, boolean b, View view, ViewGroup viewGroup) {

        Log.e("i : ", "--------------" + i);
        Log.e("i1 : ", "--------------" + i1);

        Log.e("child : --------", "" + listDataChild);
        Log.e("getChild()--------", "" + getChild(i, i1));

        //  final Item item = (Item) getChild(i, i1);

        try {
            final String childText = (String) getChild(i, i1);

        } catch (Exception e) {
        }


        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.exp_approval_item, null);
        }

        TextView txtGivenAmt = (TextView) view
                .findViewById(R.id.tvExpApprovalItem_GivenAmt);

        TextView txtReturnAmt = (TextView) view
                .findViewById(R.id.tvExpApprovalItem_ReturnAmt);


//        txtListChild.setText("" + item.getItemName());
//        edStock.setHint("" + item.getCurrentStock());


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
