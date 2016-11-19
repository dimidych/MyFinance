package com.myfinance;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.myfiance.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements OnClickListener {

    private final String LOG_TAG = "CategoryFragment";
    private DbWorker _dbWrkInst = null;
    private Button btnAddCategory;
    private EditText txtCategory;
    private ListView lstCategory;

    public CategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (_dbWrkInst == null)
            _dbWrkInst = new DbWorker(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        txtCategory = (EditText) (view.findViewById(R.id.txtCategory));
        lstCategory=(ListView)(view.findViewById(R.id.lstCategory));
        btnAddCategory = (Button) (view.findViewById(R.id.btnAddCategory));
        btnAddCategory.setOnClickListener(this);
        fillCategoryList();
        return view;
    }

    private void fillCategoryList(){
        try {
            String query="select expense_type_name from EXPENSE_TYPE_TBL";
            Cursor reader = _dbWrkInst.CurrentDb.rawQuery(query,null);

            if (reader != null)
                if (reader.moveToFirst()) {
                    ArrayList<String> categoryLst=new ArrayList<String>();

                    do {
                        String cat=reader.getString(0);
                        categoryLst.add(cat);
                    }while(reader.moveToNext());

                    ArrayAdapter adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1,categoryLst);
                    lstCategory.setAdapter(adapter);
                }
        } catch (Exception ex) {
            String strErr = "Error in fillCategoryList - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() != R.id.btnAddCategory)
                return;

            String newcategory = txtCategory.getText().toString().toLowerCase().trim();

            if (newcategory.isEmpty()) {
                Snackbar.make(view, "Empty category", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            if (_dbWrkInst.checkRecordExistance("EXPENSE_TYPE_TBL", "trim(lower(expense_type_name))=trim(lower(?))", new String[]{newcategory})) {
                Snackbar.make(view, "Such category already exists", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

            ContentValues cv = new ContentValues();
            cv.put("expense_type_name", newcategory);

            if (_dbWrkInst.CurrentDb.insert("EXPENSE_TYPE_TBL", null, cv) > 0) {
                Snackbar.make(view, "Category successfully added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                txtCategory.setText("");
                fillCategoryList();
            }
        } catch (Exception ex) {
            String strErr = "Error in onClick - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return;
        }
    }
}
