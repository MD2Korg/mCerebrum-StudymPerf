package org.md2k.studymperf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.md2k.studymperf.menu.MyMenu;

public abstract class AbstractActivityMenu extends AppCompatActivity {
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        updateMenu(0);
    }

    public void updateMenu(int state) {
            createDrawer();
            result.resetDrawerContent();
            result.getHeader().refreshDrawableState();
            result.setSelection(state, true);
    }

    void createDrawer() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withCompactStyle(true)
                .addProfiles(new MyMenu().getHeaderContent(this,"abc", true, responseCallBack))
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(new MyMenu().getMenuContent(responseCallBack))
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
/*
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
*/
//        super.onSaveInstanceState(outState);
    }

    ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(int response) {
            switch (response) {
                case MyMenu.MENU_ABOUT_STUDY:
                    break;
                case MyMenu.MENU_HELP:
                    break;
                case MyMenu.MENU_APP_ADD_REMOVE:
                    break;
                case MyMenu.MENU_JOIN:
                    break;
                case MyMenu.MENU_LEAVE:
                    break;
                case MyMenu.MENU_LOGIN:
                    break;
                case MyMenu.MENU_LOGOUT:
                    break;
                case MyMenu.MENU_APP_SETTINGS:
                    break;
                default:
            }
        }
    };
}

