package com.myfinance;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;

public class Category  implements Comparator<Category> {

    private final static String LOG_TAG = "Category";
    public int ExpenseTypeId;
    public String ExpenseTypeName;
    private DbWorker _dbWrkObj;

    public Category() {
    }

    public Category(int expenseTypeId, String expenseTypeName) {
        ExpenseTypeId = expenseTypeId;
        ExpenseTypeName = expenseTypeName;
    }

    public Category(DbWorker dbWrkObj) {
        _dbWrkObj = dbWrkObj;
    }

    public boolean addCategory(String newCategory) {
        try {
            if (newCategory.isEmpty())
                throw new Exception("Empty category");

            if (_dbWrkObj.checkRecordExistance("EXPENSE_TYPE_TBL", "trim(lower(expense_type_name))=trim(lower(?))", new String[]{newCategory}))
                throw new Exception("Such category already exists");

            ContentValues cv = new ContentValues();
            cv.put("expense_type_name", newCategory);

            if (_dbWrkObj.CurrentDb.insert("EXPENSE_TYPE_TBL", null, cv) < 0)
                throw new Exception("Couldnt add new category");

            return true;
        } catch (Exception ex) {
            String strErr = "Error while adding category - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }
    }

    public Cursor getCategoryAsCursor(int id, String categoryName) {
        try {
            String query = "select id,expense_type_name from EXPENSE_TYPE_TBL";
            ArrayList<AbstractMap.SimpleEntry> cv = new ArrayList<AbstractMap.SimpleEntry>();

            if (id > 0) {
                query += " where id=?";
                cv.add(new AbstractMap.SimpleEntry("id", id));
            }

            if (!categoryName.isEmpty()) {
                query += cv.size() > 0 ? " and trim(lower(expense_type_name))=trim(lower(?))" : " where trim(lower(expense_type_name))=trim(lower(?))";
                cv.add(new AbstractMap.SimpleEntry("expense_type_name", categoryName));
            }

            String[] params = new String[cv.size()];

            if (cv.size() > 0)
                for (int i = 0; i < cv.size(); i++) {
                    params[i] = (String) (cv.get(i).getValue());
                }

            Cursor result = _dbWrkObj.CurrentDb.rawQuery(query + " order by expense_type_name", params);
            return result;
        } catch (Exception ex) {
            String strErr = "Error while getting category - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }
    }

    public ArrayList<Category> getCategory(int id, String categoryName) {
        try {
            Cursor reader = getCategoryAsCursor(id, categoryName);
            ArrayList<Category> result = new ArrayList<Category>();

            if (reader != null) {
                if (reader.moveToFirst())
                    do {
                        result.add(new Category(reader.getInt(0), reader.getString(1)));
                    }
                    while (reader.moveToNext());

                reader.close();
            }

            return result;
        } catch (Exception ex) {
            String strErr = "Error while getting category - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return null;
        }
    }

    @Override
    public int compare(Category lhs, Category rhs) {
        int result = 1;

        try {
            if (lhs.ExpenseTypeName.trim().equalsIgnoreCase(rhs.ExpenseTypeName))
                result = 0;
        } catch (Exception ex) {
            String strErr = "Object equation error - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return 1;
        }

        return result;
    }

    @Override
    public boolean equals(Object inst) {
        boolean result = false;

        try {
            Category groupInst = (Category) inst;
            result = (this.ExpenseTypeName.trim().equalsIgnoreCase(groupInst.ExpenseTypeName));
        } catch (Exception ex) {
            String strErr = "Object equation error - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }
}
