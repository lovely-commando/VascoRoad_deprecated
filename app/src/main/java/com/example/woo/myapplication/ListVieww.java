package com.example.woo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

public class ListVieww extends Activity implements View.OnClickListener{
    ListView listView;
  //  SingerAdapter adapter;
    ListViewAdapter adapter2;
    EditText editSearch;


//
//    ArrayList<ListViewItem> items = new ArrayList<ListViewItem>();




    class ListViewAdapter extends BaseAdapter implements Filterable
    {
        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
        // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
        private ArrayList<ListViewItem> filteredItemList = listViewItemList ;

        Filter listFilter ;

        // ListViewAdapter의 생성자
        public ListViewAdapter() {

        }

        // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
        @Override
        public int getCount() {
            return filteredItemList.size() ;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_listview, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.ImageView_person) ;
            TextView name = (TextView) convertView.findViewById(R.id.TextView_Name) ;
            TextView timee = (TextView) convertView.findViewById(R.id.TextView_Time) ;
            TextView place = (TextView) convertView.findViewById(R.id.TextView_Place) ;
            TextView desc = (TextView)convertView.findViewById(R.id.TextView_Desc);
            // Data Set(filteredItemList)에서 position에 위치한 데이터 참조 획득
            ListViewItem listViewItem = filteredItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            //iconImageView.setImageDrawable(listViewItem.getIcon());
            name.setText(listViewItem.getName());
            timee.setText(listViewItem.getTimee());
            place.setText(listViewItem.getPlace());
            desc.setText(listViewItem.getDesc());

            return convertView;
        }

        // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
        @Override
        public long getItemId(int position) {
            return position ;
        }

        // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
        @Override
        public Object getItem(int position) {
            return filteredItemList.get(position) ;
        }

        // 아이템 데이터 추가

        public void addItem(ListViewItem item)
        {
            listViewItemList.add(item);
        }

        public Filter getFilter()
        {
            if(listFilter==null)
            {
                listFilter = new ListFilter();
            }

            return listFilter;
        }

        public class ListFilter extends Filter{

            public FilterResults performFiltering(CharSequence constraint)
            {
                Toast.makeText(ListVieww.this, "on performFIltering", Toast.LENGTH_SHORT).show();

                FilterResults results = new FilterResults();

                    if(constraint==null||constraint.length()==0)
                    {
                        results.values = listViewItemList;
                        results.count = listViewItemList.size();
                    }
                    else
                    {
                        ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();
//                        Log.d("ListVieww", "items: " + items.size());
//                        Toast.makeText(ListVieww.this, "items: " +  items.size(), Toast.LENGTH_SHORT).show();

                        for(ListViewItem item : listViewItemList)
                        {
                            Log.d("ListVieww", "word: " + constraint.toString().toUpperCase());
                            if(item.getName().toUpperCase().contains(constraint.toString().toUpperCase()))
                                itemList.add(item);
                        }
                        results.values = itemList;
                        results.count = itemList.size();


                    }
                    return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                filteredItemList = (ArrayList<ListViewItem>)results.values;

                if(results.count>0)
                {
                    notifyDataSetChanged();
                }else
                {
                    notifyDataSetInvalidated();
                }
            }
        }

    }


    @Override
    public void onClick(View view) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        listView = (ListView) findViewById(R.id.listView);

//        adapter = new rAdapter();
        adapter2 = new ListViewAdapter();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String DistrictName = parent.getItemAtPosition(position).toString();
                //입력값을 변수에 저장한다.

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

/*
       //버튼을 누르면 등록이 된다.
        Button EnrollButton = (Button) findViewById(R.id.Button_Add);
        EnrollButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.addItem(new ListViewItem("kimsib","대구광역시 달서구 진천동","2019년 4월 19일 13시경",R.drawable.boy));
                listView.setAdapter(adapter);
            }
        });*/

        adapter2.addItem(new ListViewItem("Minjeong","대구광역시 달서구 진천동","2019년 4월 19일 13시경",R.drawable.boy,"하비스트 먹다가 사라짐"));
        listView.setAdapter(adapter2);
        adapter2.addItem(new ListViewItem("Joonhee","대구광역시 달서구 진천동","2019년 4월 19일 13시경",R.drawable.boy,"연어초밥 먹다가 사라짐"));
        listView.setAdapter(adapter2);
        adapter2.addItem(new ListViewItem("Semin","대구광역시 달서구 진천동","2019년 4월 19일 13시경",R.drawable.boy,"인도네시아 스쿠터 타고 사라짐"));
        listView.setAdapter(adapter2);

        adapter2.addItem(new ListViewItem("Seongki","대구광역시 달서구 진천동","2019년 4월 19일 13시경",R.drawable.boy,"엘렌에게 등짝맞아서 사라짐"));
        listView.setAdapter(adapter2);


        //리스트뷰를 누르면 다른 액티비티로 넘어간다(실종자 상세 정보)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(),listVieww_popup.class);

                Object putitem = adapter2.getItem(position);
                intent.putExtra("selecteditem", (Serializable) putitem);
                startActivity(intent);
            }
        });
        //검색어를 입력하면 검색이 가능하다.
        editSearch = (EditText)findViewById(R.id.EditText_Search);


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String filterText = editSearch.getText().toString().toLowerCase(Locale.getDefault());
//                Toast.makeText(ListVieww.this, "현재: " + filterText, Toast.LENGTH_SHORT).show();

//                if(filterText.length()>0)
//                {
//                    listView.setFilterText(filterText);
//                }
//                else
//                {
//                    listView.clearTextFilter();
//                }
                ( (ListViewAdapter)listView.getAdapter()).getFilter().filter(filterText);
            }

        });

    }
}







