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

        String array = "";
        String target = " ";
        int target_num = array.indexOf(target);

        for(int i =0;i<4;i++)
        {
           // toString().substring(3);
            String[] result = CardNum.split(" ");

            //result[i] = target.substring(target.length()-4, target.length());
            String hap = target.substring(target_num,(target.substring(target_num).indexOf(" ")+ target_num));
            result[i] = hap;
        }

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