package com.example.przelicznikwalut.database;

public final class CurrenciesContract {

    private CurrenciesContract() {
    }

    public static class CurrenciesEntry {
        public static final String TABLE_NAME = "currencies";
        public static final String SYMBOL_ID = "symbol_id";
        public static final String DESCRITPTION = "description";
        public static final String VALUE = "value";
        public static final String DATE = "date";
        public static final String FAVOURITE = "favourite";
    }


}
