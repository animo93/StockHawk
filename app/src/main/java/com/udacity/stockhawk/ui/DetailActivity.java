package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockProvider;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import timber.log.Timber;

/**
 * Created by animo on 14/1/17.
 */

public class DetailActivity extends AppCompatActivity{

    private Uri mUri;
    ViewGroup detailLayout;
    private final DecimalFormat dollarFormat;

    TextView dateTime;

    TextView historicalQuote;

    public DetailActivity() {
        this.dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        detailLayout= (ViewGroup) findViewById(R.id.layout_detail);
        Intent intent = getIntent();
        mUri=intent.getParcelableExtra("extra_text");
        getHistoricalData();
    }

    private void getHistoricalData() {
        Cursor mCursor=getContentResolver().query(mUri
                ,Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{})
                ,null
                ,null
                ,null);
        if(mCursor!=null && mCursor.moveToFirst()){
            String listHistorical = mCursor.getString(Contract.Quote.POSITION_HISTORY);
            List<String> historical = parseHistoricalData(listHistorical);

            for(String data:historical){
                View detailView = LayoutInflater.from(this).inflate(
                        R.layout.detail_item,null
                );
                dateTime= (TextView) detailView.findViewById(R.id.date);
                historicalQuote= (TextView) detailView.findViewById(R.id.historical_quote);
                dateTime.setText(convetTodateTime(data.split(",")[0]));
                String quote=data.split(",")[1];
                historicalQuote.setText(dollarFormat.format(Double.parseDouble(quote)));
                detailLayout.addView(detailView);
            }
        }

    }

    private String convetTodateTime(String s) {
        SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(s));
        return format.format(calendar.getTime());
    }

    private List<String> parseHistoricalData(String listHistorical) {
        StringTokenizer tokenizer = new StringTokenizer(listHistorical,"\n");
        List<String> historicalList = new ArrayList<>();
        while (tokenizer.hasMoreTokens()){
            historicalList.add(tokenizer.nextToken());
        }
        return historicalList;
    }


}
