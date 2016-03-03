package com.copia.copiasalesmobile.odooConn;

import android.util.Log;

import com.copia.copiasalesmobile.utilities.Agent;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by mbuco on 2/15/16.
 * contains the operations needed to do call odoo db
 */
public class OpOdoo{
    XmlRpcClient client;
    //credentials
    String host = "http://52.89.125.104";
    String db = "copiaERP";
    int port = 8069;
    String url = "http://52.89.125.104:8069/xmlrpc/common";
    String username = "admin";
    String password = "@dm1n2014_cop1a_erp";

    //Arraylist of Ids
    ArrayList listAgentIds;
    //Arraylist of agents
    ArrayList<Agent> agents;

    public OpOdoo(){
        //do the login
        Login log = new Login();
        log.LoginOdoo();
    }

    //get all the agents using their ids
    public ArrayList<Agent> getAllAgents(String sDate){
        Object[] hm = null;
        agents = new ArrayList<>();
        listAgentIds = getAllAgentIds(sDate);

        try{
            XmlRpcClient client1 = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            clientConfig.setEnabledForExtensions(true);
            clientConfig.setServerURL(new URL("http", "52.89.125.104", 8069, "/xmlrpc/object"));
            client1.setConfig(clientConfig);

            Object[] params2 = { "name","phone", "mobile","write_date","id","experiment_id"};
            //Object[] params1 = { "name","phone", "mobile"};

            Vector<Object> args = new Vector<Object>();

            args.add(db);
            args.add(1);
            args.add(password);
            args.add("res.partner");
            args.add("read");
            args.add(listAgentIds); // <- THE PYTHON SYNTAX SAYS input 'LIST OF IDS' here What is the Java equivalent???
            args.add(params2);



            hm = (Object[])client1.execute("execute", args);
        }catch(Exception e){
            e.printStackTrace();
        }

        HashMap globalMap = new HashMap();
        Agent agent;
        for (Object object : hm) {
            agent = new Agent();
            HashMap hash = (HashMap) object;
            System.out.println(hash.values());
            globalMap.put("name", hash.get("name"));
            agent.setMobile(hash.get("mobile").toString());
            agent.setName(hash.get("name").toString());
            agent.setPhone(hash.get("phone").toString());
            agent.setId(hash.get("id").toString());
            agent.setWrite_date(hash.get("write_date").toString());
            agent.setExperiment_id(hash.get("experiment_id").toString());
            agents.add(agent);
            Log.e("Them name",hash.get("name").toString());
        }
        return agents;
    }

    //get the agent Ids
    public ArrayList getAllAgentIds(String sDate){
        listAgentIds = new ArrayList();
        try{
            client = new XmlRpcClient();
            XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
            clientConfig.setEnabledForExtensions(true);
            clientConfig.setServerURL(new URL("http", "52.89.125.104", 8069, "/xmlrpc/object"));
            client.setConfig(clientConfig);


            ArrayList<Object[]> listparam = new ArrayList<>();
            Object[] params3 = { "agent", "=", "True" };
            Object[] param_3= {"active","=","True"};
            Object[] param_write_date= {"write_date",">",sDate};
            listparam.add(param_write_date);
            listparam.add(params3);
            listparam.add(param_3);
            Vector<Object> arg = new Vector<Object>();
            arg.add(db); //database name
            arg.add(1);
            arg.add(password); //the password of the database
            arg.add("res.partner");
            arg.add("search");
            arg.add(listparam);

            Object[] ids = (Object[]) client.execute("execute", arg);
            System.out.println("partner addressees with create_uid 1 :");
            for (Object obj : ids) {
                int a = Integer.parseInt(obj.toString());
                listAgentIds.add(Integer.toString(a));
                //Log.e("String is: ", obj.toString());
            }


        }
        catch(Exception e){

        }
        return listAgentIds;
    }
}
