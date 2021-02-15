package com.example.przelicznikwalut.model;

public class Currency {

    private String symbol;
    private String description;
    private String value;
    private String date;
    private int favourite;

    public Currency(String symbol, String description, String value, String date, int favourite) {
        this.symbol = symbol;
        this.description = description;
        this.value = value;
        this.date = date;
        this.favourite = favourite;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                ", value='" + value + '\'' +
                ", date='" + date + '\'' +
                ", favourite=" + favourite +
                '}';
    }
}
