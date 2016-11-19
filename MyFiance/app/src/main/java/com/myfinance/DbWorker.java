package com.myfinance;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.myfiance.R;

public class DbWorker extends SQLiteOpenHelper {
    private Context m_Ctx = null;
    public SQLiteDatabase CurrentDb = null;
    private final String LOG_TAG = "myfiance - DbWorker";

    /**
     * Not default constructor
     */
    public DbWorker(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);

        try {
            m_Ctx = context;
            CurrentDb = this.getWritableDatabase();
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Error in DbWorker constructor - " + ex.getMessage());
        }
    }

    /**
     * Closes all opened connections
     */
    public void destroyInstance() {
        try {
            this.close();
        } catch (Exception ex) {
            Log.d(LOG_TAG, "DbWorker destroyInstance error - " + ex.getMessage());
        }
    }

    /**
     * Finalizes object resources
     */
    @Override
    protected void finalize() {
        destroyInstance();
    }

    /**
     * Creates new schema
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Toast.makeText(m_Ctx, "Try to create DB EXPENSE", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Try to create DB EXPENSE");
            ////////////////////////////////// Table creation //////////////////////////////
            db.beginTransactionNonExclusive();

            try {
                //EXPENSE_TYPE_TBL
                db.execSQL("CREATE TABLE EXPENSE_TYPE_TBL (id INTEGER PRIMARY KEY AUTOINCREMENT, expense_type_name TEXT NOT NULL);");
                Toast.makeText(m_Ctx, "Try to create table EXPENSE_TYPE_TBL", Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Try to create table EXPENSE_TYPE_TBL");

                //EXPENSE_TBL
                db.execSQL("CREATE TABLE EXPENSE_TBL (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "expense_date DATETIME NOT NULL DEFAULT(DATETIME('now', 'localtime')), " +
                        "id_expense_type INTEGER NOT NULL, expense DOUBLE NOT NULL, expense_name TEXT NOT NULL);");
                Toast.makeText(m_Ctx, "Try to create table EXPENSE_TBL", Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Try to create table EXPENSE_TBL");

                db.setTransactionSuccessful();
            } catch (Exception ex) {
            } finally {
                db.endTransaction();
            }

            //////////////////////////////// Values set //////////////////////////////////////
            CurrentDb = db;

            //EXPENSE_TYPE_TBL
            ArrayList<HashMap<String, String>> paramTypeValueCollection = new ArrayList<HashMap<String, String>>();
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.Other));
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.Food));
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.House));
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.Fuel));
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.Tax));
            }});
            paramTypeValueCollection.add(new HashMap<String, String>() {{
                put("expense_type_name", m_Ctx.getString(R.string.Entertainment));
            }});
            makePackageInsert("EXPENSE_TYPE_TBL", paramTypeValueCollection);
            paramTypeValueCollection.clear();
            Toast.makeText(m_Ctx, "Try to insert initial data to EXPENSE_TYPE_TBL", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "Try to insert initial data to EXPENSE_TYPE_TBL");

            Toast.makeText(m_Ctx, "DB EXPENSE was created", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "DB EXPENSE was created");
        } catch (Exception ex) {
            Log.d(LOG_TAG, ex.getMessage());
            Toast.makeText(m_Ctx, "Error while creating DB - " + ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        } finally {
        }
    }

    /**
     * Upgrades schema
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

        } catch (Exception ex) {
            Log.d(LOG_TAG, "Error while updating DB - " + ex.getMessage());
            return;
        }
    }

    /**
     * Drops current db
     */
    public boolean dropDatabase() {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            CurrentDb.beginTransactionNonExclusive();
            CurrentDb.execSQL("delete from EXPENSE_TBL");
            CurrentDb.execSQL("delete from EXPENSE_TYPE_TBL");
            CurrentDb.setTransactionSuccessful();
            result = true;
            Toast.makeText(m_Ctx, "DB EXPENSE was deleted", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "DB EXPENSE was deleted");
        } catch (Exception ex) {
            String strErr = "Error while dropping DB - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        } finally {
            if (CurrentDb != null)
                CurrentDb.endTransaction();
        }

        return result;
    }

    /**
     * Makes insert into table
     */
    public boolean makePackageInsert(String strTableName, ArrayList<HashMap<String, String>> paramTypeValueCollection) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            if (strTableName.trim().equalsIgnoreCase(""))
                throw new Exception("Table name was not set");

            if (paramTypeValueCollection == null || paramTypeValueCollection.isEmpty())
                throw new Exception("Parameters was not set");

            ContentValues cv = new ContentValues();

            for (int i = 0; i < paramTypeValueCollection.size(); i++) {
                try {
                    HashMap<String, String> paramAttr = paramTypeValueCollection.get(i);

                    if (paramAttr == null || paramAttr.isEmpty())
                        continue;

                    Object[] keyArr = paramAttr.keySet().toArray();

                    for (int j = 0; j < keyArr.length; j++) {
                        String key = (String) (keyArr[j]);
                        cv.put(key, paramAttr.get(key));
                    }

                    CurrentDb.insert(strTableName, null, cv);
                } catch (Exception ex) {
                    Log.d(LOG_TAG, ex.getMessage());
                }
            }

            result = true;
        } catch (Exception ex) {
            String strErr = "Error while  MakePackageInsert - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }

    /**
     * Makes update into table
     */
    public boolean makeUpdate(String strTableName, ArrayList<HashMap<String, String>> paramTypeValueCollection) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            if (strTableName.trim().equalsIgnoreCase(""))
                throw new Exception("Table name was not set");

            if (paramTypeValueCollection == null || paramTypeValueCollection.isEmpty())
                throw new Exception("Parameters was not set");

            result = true;
        } catch (Exception ex) {
            String strErr = "Error while MakeUpdate - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }

    /**
     * Makes delete from table
     */
    public boolean makeDelete(String strTableName, ArrayList<HashMap<String, String>> paramTypeValueCollection) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            if (strTableName.trim().equalsIgnoreCase(""))
                throw new Exception("Table name was not set");

            if (paramTypeValueCollection == null || paramTypeValueCollection.isEmpty())
                throw new Exception("Parameters was not set");

            result = true;
        } catch (Exception ex) {
            String strErr = "Error while makeDelete - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }

    /**
     * Makes delete from table
     */
    public boolean checkRecordExistance(String strTableName, String strCondition, String[] conditionArgs) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            if (strTableName.trim().equalsIgnoreCase(""))
                throw new Exception("Table name was not set");

            Cursor reader = CurrentDb.query(strTableName, new String[]{"count(1) as cnt"}, strCondition, conditionArgs, null, null, null);

            if (reader != null) {
                if (reader.moveToFirst())
                    result = (reader.getInt(0) > 0);

                reader.close();
            }
        } catch (Exception ex) {
            String strErr = "Error while CheckRecordExistance - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }

    /**
     * Makes selection from table
     */
    public boolean makeSelection(String strQuery, String[] params) {
        boolean result = false;

        try {
            if (CurrentDb == null)
                throw new Exception("DB not exists");

            Cursor reader = CurrentDb.rawQuery(strQuery, params);

            if (reader != null) {
                if (reader.moveToFirst())
                    result = (reader.getInt(0) > 0);

                reader.close();
            }
        } catch (Exception ex) {
            String strErr = "Error while MakeSelection - " + ex.getMessage();
            Log.d(LOG_TAG, strErr);
            return false;
        }

        return result;
    }
}
