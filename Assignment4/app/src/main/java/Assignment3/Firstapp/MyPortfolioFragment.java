package Assignment3.Firstapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Assignment3.Firstapp.Stock.Stock;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPortfolioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPortfolioFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final List<Stock> Stocks = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private List<Stock> stocks;


    public MyPortfolioFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MyPortfolioFragment newInstance() {
        MyPortfolioFragment fragment = new MyPortfolioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.stocks = new ArrayList<Stock>();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment;
        View view = inflater.inflate(R.layout.fragment_my_portfolio, container, false);
        TableLayout table = view.findViewById(R.id.StockTable);
        for (Stock s:this.stocks
        ) {
            TableRow tr = new TableRow(this.getActivity());
            TextView price = new TextView(this.getActivity());
            TextView name = new TextView(this.getActivity());
            TextView spacer = new TextView(this.getActivity());

            price.setText(s.getPrice());
            price.setTextColor(0x00ff00);
            name.setText(s.getNameString());
            spacer.setText(" --------- ");
            tr.addView(name);
            tr.addView(spacer);
            tr.addView(price);
            table.addView(tr);
        }
        return view;

    }


    public void addStock(Stock stock){
        this.stocks.add(stock);
    }

}