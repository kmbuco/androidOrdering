package com.copia.copiasalesmobile.odooConn;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mbuco on 2/15/16.
 */
public class Login implements LoginOdoo {
    @Override
    public int LoginOdoo() {
        //http://52.89.125.104:8069
        String host = "http://52.89.125.104";
        String db = "copiaERP";
        int port = 8069;
        String url = "http://52.89.125.104:8069/xmlrpc/common";
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setEnabledForExtensions(true);
        try {
            config.setServerURL(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        XmlRpcClient client = new XmlRpcClient();
	    /*XmlRpcClientConfigImpl pConfig;*/
        client.setConfig(config);

        String username = "admin";
        String password = "@dm1n2014_cop1a_erp";
        //Object[] params = new Object[]{"terp", "admin", "a"};
        Object[] params = new Object[]{db,username, password};
        Object res;
        try {
            res = client.execute("login", params);
            System.out.print(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ///////////////////////////////////////////////////////////////
        return 0;
    }
}
