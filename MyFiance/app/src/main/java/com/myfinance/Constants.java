package com.myfinance;

public class Constants {
    /**
     * DB params
     */
    public static final String DB_NAME = "EXPENSE";
    public static final int DB_VERSION = 1;

    /**
     * Operation types
     */
    public static final int INSERT_OP = 1;
    public static final int UPDATE_OP = 2;
    public static final int DELETE_OP = 3;
    public static final int SELECT_OP = 4;
    public static final int BROWSE_OP = 5;

    /**
     * Dialog results
     */
    public static final byte DIALOG_OK = 1;
    public static final byte DIALOG_CANCEL = 0;

    /**
     * Navigation fragment enum
     */
    public static final byte FRG_EXPENCE = 0;
    public static final byte FRG_CATEGORY = 1;
    public static final byte FRG_TOTALS = 2;

    /**
     * Date dialog type
      */
    public static final byte DLG_DATE_FROM = 0;
    public static final byte DLG_DATE_TO = 1;
}