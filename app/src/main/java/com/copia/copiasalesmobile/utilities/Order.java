package com.copia.copiasalesmobile.utilities;

/**
 * Created by mbuco on 3/9/16.
 */
public class Order {

    //"date_time_","expected_delivery_date_","cust_phone_"
    String date_time_;
    String expected_delivery_date_;
    String cust_phone_;
    String order_status_;
    String order_id;
    String agent_id_;

    public String getAgent_id_() {
        return agent_id_;
    }

    public void setAgent_id_(String agent_id_) {
        this.agent_id_ = agent_id_;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_status_() {
        return order_status_;
    }

    public void setOrder_status_(String order_status_) {
        this.order_status_ = order_status_;
    }

    public String getDate_time_() {
        return date_time_;
    }

    public void setDate_time_(String date_time_) {
        this.date_time_ = date_time_;
    }

    public String getExpected_delivery_date_() {
        return expected_delivery_date_;
    }

    public void setExpected_delivery_date_(String expected_delivery_date_) {
        this.expected_delivery_date_ = expected_delivery_date_;
    }

    public String getCust_phone_() {
        return cust_phone_;
    }

    public void setCust_phone_(String cust_phone_) {
        this.cust_phone_ = cust_phone_;
    }
}
