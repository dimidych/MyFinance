package com.myfinance;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.myfiance.R;

import java.util.ArrayList;

public class ExpenseFragment extends Fragment implements OnClickListener {

    private final String LOG_TAG = "ExpenseFragment";
    private DbWorker _dbWrkInst;
    private ArrayList<Category> _categoryLst;
    private Spinner cmbCategory;
    private EditText txtExpense;
    private EditText txtExpenseName;
    private Button btnAddExpense;

    public ExpenseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (_dbWrkInst == null) {
            _dbWrkInst = new DbWorker(this.getContext());
            _categoryLst = (new Category(_dbWrkInst)).getCategory(-1, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        try {
            cmbCategory = (Spinner) (view.findViewById(R.id.cmbCategory));
            String[] categoryNameLst = getCategoryNames();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                    android.R.layout.simple_list_item_1, categoryNameLst);
            cmbCategory.setAdapter(adapter);
            cmbCategory.setSelection(0);
            txtExpense = (EditText) (view.findViewById(R.id.txtExpense));
            txtExpenseName = (EditText) (view.findViewById(R.id.txtExpenseName));
            btnAddExpense = (Button) (view.findViewById(R.id.btnAddExpense));
            btnAddExpense.setOnClickListener(this);
        } catch (Exception ex) {
            String strErr = "Error in onCreateView - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
        }

        return view;
    }

    private String[] getCategoryNames() {
        try {
            if (_categoryLst == null || _categoryLst.size() == 0)
                throw new Exception("Empty category list");

            ArrayList<String> categoryNameLst = new ArrayList<String>();

            for (Category cat : _categoryLst)
                if (!categoryNameLst.contains(cat.ExpenseTypeName.toLowerCase().trim()))
                    categoryNameLst.add(cat.ExpenseTypeName.toLowerCase().trim());

            String[] result = new String[categoryNameLst.size()];
            categoryNameLst.toArray(result);
            return result;
        } catch (Exception ex) {
            String strErr = "Error in getCategoryNames - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }
    }

    private Category getCategoryByName(String categoryName) {
        try {
            if (_categoryLst == null || _categoryLst.size() == 0)
                throw new Exception("Empty category list");

            for (Category cat : _categoryLst)
                if (cat.ExpenseTypeName.equalsIgnoreCase(categoryName.toLowerCase()))
                    return cat;
        } catch (Exception ex) {
            String strErr = "Error in getCategoryByName - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
        }

        return null;
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() != R.id.btnAddExpense)
                return;

            String strExpenseName = txtExpenseName.getText().toString().trim().toLowerCase().trim();

            if (strExpenseName.isEmpty()) {
                Snackbar.make(view, "Empty expense name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            String strExpense = txtExpense.getText().toString().trim().toLowerCase().trim();

            if (strExpense.isEmpty()) {
                Snackbar.make(view, "Empty expense", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            double expense = Double.parseDouble(strExpense);

            if (expense <= 0) {
                Snackbar.make(view, "Expense should be positive", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            String selectedCategory = (String) (cmbCategory.getSelectedItem());
            ContentValues cv = new ContentValues();
            cv.put("expense_name", strExpenseName);
            cv.put("expense", expense);
            cv.put("id_expense_type", getCategoryByName(selectedCategory).ExpenseTypeId);

            if (_dbWrkInst.CurrentDb.insert("EXPENSE_TBL", null, cv) > 0)
                Snackbar.make(view, "Expense successfully added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            txtExpenseName.setText("");
            txtExpense.setText("");
        } catch (Exception ex) {
            String strErr = "Error in onClick - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }
}
