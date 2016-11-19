package com.myfinance;

import android.content.Intent;
import android.database.Cursor;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.myfiance.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TotalsFragment extends Fragment implements OnClickListener {

    private final String LOG_TAG = "TotalsFragment";
    private final String EXPENSE_NAME = "EXPENSE_NAME";
    private final String EXPENSE_SUM = "EXPENSE_SUM";
    private final String PERCENT = "PERCENT";
    private DbWorker _dbWrkInst = null;
    private Button btnFrom;
    private Button btnTo;
    private Button btnShowTotals;
    private Switch swcCategorized;
    private TextView lblTotal;
    private ListView lstTotals;
    private Calendar _clndrFrom;
    private Calendar _clndrTo;
    private boolean _isCategorized;

    public TotalsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (_dbWrkInst == null)
            _dbWrkInst = new DbWorker(this.getContext());
    }

    private String getStringDatePresent(Calendar clndr) {
        String month = (clndr.get(Calendar.MONTH) + 1) < 10 ? ("0" + (clndr.get(Calendar.MONTH) + 1)) : ((clndr.get(Calendar.MONTH) + 1) + "");
        String day = clndr.get(Calendar.DAY_OF_MONTH) < 10 ? ("0" + clndr.get(Calendar.DAY_OF_MONTH)) : (clndr.get(Calendar.DAY_OF_MONTH) + "");
        return "" + clndr.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_totals, container, false);

        try {
            _clndrFrom = Calendar.getInstance();
            _clndrTo = Calendar.getInstance();
            lblTotal = (TextView) (view.findViewById(R.id.lblTotal));
            btnFrom = (Button) (view.findViewById(R.id.btnFrom));
            btnFrom.setText(getStringDatePresent(_clndrFrom));
            btnTo = (Button) (view.findViewById(R.id.btnTo));
            btnTo.setText(getStringDatePresent(_clndrTo));
            btnShowTotals = (Button) (view.findViewById(R.id.btnShowTotals));
            swcCategorized = (Switch) (view.findViewById(R.id.swcCategorized));
            swcCategorized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    _isCategorized = isChecked;
                }
            });
            lstTotals = (ListView) (view.findViewById(R.id.lstTotals));
            btnFrom.setOnClickListener(this);
            btnTo.setOnClickListener(this);
            btnShowTotals.setOnClickListener(this);
        } catch (Exception ex) {
            String strErr = "Error in onCreateView - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.btnFrom: {
                    DateTimePickerFragment dateFromFragment = new DateTimePickerFragment();
                    dateFromFragment.setTargetFragment(this, Constants.DLG_DATE_FROM);
                    dateFromFragment.show(getActivity().getSupportFragmentManager(), "dateFromFragment");
                }
                break;

                case R.id.btnTo: {
                    DateTimePickerFragment dateToFragment = new DateTimePickerFragment();
                    dateToFragment.setTargetFragment(this, Constants.DLG_DATE_TO);
                    dateToFragment.show(getActivity().getSupportFragmentManager(), "dateToFragment");
                }
                break;

                case R.id.btnShowTotals:
                    calcTotals();
                    break;
            }
        } catch (Exception ex) {
            String strErr = "Error in onClick - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }

    private void calcTtl() {
        try {
            String query = "select sum(expense) as sum from EXPENSE_TBL where expense_date between ? and ?";
            String[] params = new String[]{btnFrom.getText().toString(), btnTo.getText().toString()};
            Cursor reader = _dbWrkInst.CurrentDb.rawQuery(query, params);

            if (reader != null)
                if (reader.moveToFirst()) {
                    double sum = reader.getDouble(0);
                    lblTotal.setText("" + sum + " сом");
                }
        } catch (Exception ex) {
            String strErr = "Error in calcTtl - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }

    private void calcTotals() {
        try {
            if (_clndrFrom.getTimeInMillis() > _clndrTo.getTimeInMillis())
                throw new Exception("Wrong time interval");

            calcTtl();
            String query = _isCategorized ? "select b.expense_type_name, sum(a.expense) as sum from EXPENSE_TBL a " +
                    "left join EXPENSE_TYPE_TBL b on b.id=a.id_expense_type " +
                    "where a.expense_date between ? and ? " +
                    "group by a.id_expense_type " +
                    "order by b.expense_type_name" :
                    "select distinct trim(lower(a.expense_name)) as exp_name, b.expense_type_name, sum(a.expense) as sum from EXPENSE_TBL a " +
                            "left join EXPENSE_TYPE_TBL b on b.id=a.id_expense_type " +
                            "where a.expense_date between ? and ? " +
                            "group by trim(lower(a.expense_name)), b.expense_type_name " +
                            "order by exp_name";

            String[] params = new String[]{btnFrom.getText().toString(), btnTo.getText().toString()};
            Cursor reader = _dbWrkInst.CurrentDb.rawQuery(query, params);

            if (reader != null)
                if (reader.moveToFirst()) {
                    ArrayList<Map<String, Object>> ttlExpenseLst = new ArrayList<Map<String, Object>>();
                    Map<String, Object> map;
                    String expenseTypeName = "";
                    String expenseName = "";
                    double expenseSum = 0;
                    double maxExpenseSum = -1;

                    do {
                        if (_isCategorized) {
                            expenseTypeName = reader.getString(0);
                            expenseSum = reader.getDouble(1);
                        } else {
                            expenseName = reader.getString(0);
                            expenseTypeName = reader.getString(1);
                            expenseSum = reader.getDouble(2);
                        }

                        if (expenseSum > maxExpenseSum)
                            maxExpenseSum = expenseSum;

                        map = new HashMap<String, Object>();

                        if (_isCategorized)
                            map.put(EXPENSE_NAME, expenseTypeName + " : " + expenseSum + " сом");
                        else
                            map.put(EXPENSE_NAME, expenseName + " [" + expenseTypeName + "] : " + expenseSum + " сом");

                        map.put(EXPENSE_SUM, expenseSum);
                        ttlExpenseLst.add(map);
                    } while (reader.moveToNext());

                    ArrayList<Map<String, Object>> resultExpenseLst = new ArrayList<Map<String, Object>>();

                    for (int i = 0; i < ttlExpenseLst.size(); i++) {
                        Map<String, Object> expense = new HashMap<String, Object>();
                        double xpenseSum = (double) (ttlExpenseLst.get(i).get(EXPENSE_SUM));
                        expense.put(EXPENSE_NAME, ttlExpenseLst.get(i).get(EXPENSE_NAME));
                        expense.put(PERCENT, 100 * xpenseSum / maxExpenseSum);
                        ttlExpenseLst.set(i, expense);
                    }

                    String[] from = new String[]{EXPENSE_NAME, PERCENT, PERCENT};
                    int[] to = new int[]{R.id.tvLoad, R.id.pbLoad, R.id.llLoad};
                    SimpleAdapter sAdapter = new SimpleAdapter(this.getContext(), ttlExpenseLst, R.layout.total_item, from, to);
                    sAdapter.setViewBinder(new TotalsViewBinder());
                    lstTotals.setAdapter(sAdapter);
                }
        } catch (Exception ex) {
            String strErr = "Error in calcTotals - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Constants.DIALOG_CANCEL)
                return;

            if (data == null)
                return;

            if (requestCode == Constants.DLG_DATE_FROM || requestCode == Constants.DLG_DATE_TO) {
                int year = data.getIntExtra("selected_year", 2010);
                int month = data.getIntExtra("selected_month", 1);
                int day = data.getIntExtra("selected_day", 1);

                if (requestCode == Constants.DLG_DATE_FROM) {
                    _clndrFrom.set(year, month, day);
                    btnFrom.setText(getStringDatePresent(_clndrFrom));
                }

                if (requestCode == Constants.DLG_DATE_TO) {
                    _clndrTo.set(year, month, day);
                    btnTo.setText(getStringDatePresent(_clndrTo));
                }
            }
        } catch (Exception ex) {
            String strErr = "Error while getting child activity data - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }

    class TotalsViewBinder implements SimpleAdapter.ViewBinder {

        int red = getResources().getColor(R.color.Red);
        int orange = getResources().getColor(R.color.Orange);
        int green = getResources().getColor(R.color.Green);

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            double value = 0;

            switch (view.getId()) {
                // LinearLayout
                case R.id.llLoad:
                    value = ((Double) data).doubleValue();

                    if (value < 40)
                        view.setBackgroundColor(green);
                    else if (value < 70)
                        view.setBackgroundColor(orange);
                    else
                        view.setBackgroundColor(red);

                    return true;
                // ProgressBar
                case R.id.pbLoad:
                    value = ((Double) data).doubleValue();
                    ((ProgressBar) view).setProgress((int) (Math.round(value)));
                    return true;
            }
            return false;
        }
    }
}


