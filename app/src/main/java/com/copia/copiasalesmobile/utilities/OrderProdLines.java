package com.copia.copiasalesmobile.utilities;

import java.util.ArrayList;

/**
 * Created by mbuco on 3/21/16.
 */
public class OrderProdLines {
    String sOrderID ;
    String scust_phone_;
    String sdate_time_;
    String stype_;
    String sexpected_delivery_date_;
    String agent_id_;
    String sReference;
    ArrayList<productLine> arrProdLines;

    public String getsOrderID() {
        return sOrderID;
    }

    public void setsOrderID(String sOrderID) {
        this.sOrderID = sOrderID;
    }

    public String getScust_phone_() {
        return scust_phone_;
    }

    public void setScust_phone_(String scust_phone_) {
        this.scust_phone_ = scust_phone_;
    }

    public String getSdate_time_() {
        return sdate_time_;
    }

    public void setSdate_time_(String sdate_time_) {
        this.sdate_time_ = sdate_time_;
    }

    public String getStype_() {
        return stype_;
    }

    public void setStype_(String stype_) {
        this.stype_ = stype_;
    }

    public String getSexpected_delivery_date_() {
        return sexpected_delivery_date_;
    }

    public void setSexpected_delivery_date_(String sexpected_delivery_date_) {
        this.sexpected_delivery_date_ = sexpected_delivery_date_;
    }

    public ArrayList<productLine> getArrProdLines() {
        return arrProdLines;
    }

    public void setArrProdLines(ArrayList<productLine> arrProdLines) {
        this.arrProdLines = arrProdLines;
    }

    public String getAgent_id_() {
        return agent_id_;
    }

    public void setAgent_id_(String agent_id_) {
        this.agent_id_ = agent_id_;
    }


    public String getsReference() {
        return sReference;
    }

    public void setsReference(String sReference) {
        this.sReference = sReference;
    }

}
