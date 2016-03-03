package com.copia.copiasalesmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by mbuco on 2/10/16.
 */
public abstract class AbstractBaseActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected abstract int getLayoutResource();

    protected void setToolBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void setToolBarTitle(String toolBarTitle) {
        toolbar.setTitle(toolBarTitle);
    }


}
