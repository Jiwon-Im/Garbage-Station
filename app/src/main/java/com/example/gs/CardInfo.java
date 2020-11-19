package com.example.gs;

import android.widget.EditText;

public class CardInfo {
    private String CardNum;
    private String MmYy;
    private String CardPass;
    private String BirDate;
    private int money;

    public CardInfo(String CardNum, String MmYy, String CardPass, String BirDate, Integer money){
        this.CardNum = CardNum;
        this.MmYy = MmYy;
        this.CardPass = CardPass;
        this.BirDate = BirDate;
        this.money = money;
    }

    public String getCardNum(){
        return this.CardNum;
    }
    public void setCardNum(String CardNum){
        this.CardNum = CardNum;
    }

    public String getMmYy(){
        return this.MmYy;
    }
    public void setMmYy(String MmYy){
        this.MmYy = MmYy;
    }

    public String getCardPass(){
        return this.CardPass;
    }
    public void setCardPass(String CardPass){
        this.CardPass = CardPass;
    }

    public String getBirDate(){
        return this.BirDate;
    }
    public void setBirDate(String BirDate){
        this.BirDate = BirDate;
    }

}
