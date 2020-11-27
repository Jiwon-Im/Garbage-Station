package com.example.gs;

import android.widget.EditText;

public class CardInfo {
    private String CardNum;
    private String MmYy;
    private String CardPass;
    private String BirDate;
    private Number GsPay;
    private String uid;




    public CardInfo(String CardNum, String MmYy, String CardPass, String BirDate, Number GsPay, String uid){
        this.CardNum = CardNum;
        this.MmYy = MmYy;
        this.CardPass = CardPass;
        this.BirDate = BirDate;
        this.GsPay = GsPay;
        this.uid=uid;
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
        //String[] array = BirDate.split(" ");
        return this.BirDate;
    }
    public void setBirDate(String BirDate){ this.BirDate = BirDate;  }

    public Number getGsPay(){ return this.GsPay; }
    public void setGsPay(Number GsPay){ this.GsPay = GsPay; }

    public String getuid(){  return this.uid; }
    public void setUId(String uid){ }

}