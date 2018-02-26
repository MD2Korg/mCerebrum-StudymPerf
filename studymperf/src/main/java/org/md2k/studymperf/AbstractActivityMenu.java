package org.md2k.studymperf;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.core.access.serverinfo.ServerCP;
import org.md2k.mcerebrum.core.access.studyinfo.StudyCP;
import org.md2k.mcerebrum.system.update.Update;
import org.md2k.studymperf.menu.MenuContent;
import org.md2k.studymperf.menu.MyMenu;
import org.md2k.studymperf.ui.main.FragmentContactUs;
import org.md2k.studymperf.ui.main.FragmentHelp;
import org.md2k.studymperf.ui.main.FragmentHome;
import org.md2k.studymperf.ui.main.FragmentWorkAnnotation;

import static org.md2k.studymperf.menu.MyMenu.MENU_START_STOP;

public abstract class AbstractActivityMenu extends AbstractActivityBasics {
    private Drawer result = null;
    int selectedMenu = -1;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createUI() {
        createDrawer();
        result.resetDrawerContent();
        result.getHeader().refreshDrawableState();
        result.setSelection(MyMenu.MENU_HOME);
        selectedMenu=MyMenu.MENU_HOME;
    }

    public void updateUI() {
        if (result == null) {
            createUI();
            return;
        }
        int badgeValue = Update.hasUpdate(AbstractActivityMenu.this);
        if (badgeValue > 0) {
            StringHolder a = new StringHolder(String.valueOf(badgeValue));
            result.updateBadge(MyMenu.MENU_UPDATE, a);
        } else {
            StringHolder a = new StringHolder("");
            result.updateBadge(MyMenu.MENU_UPDATE, a);
        }
        boolean start = AppInfo.isServiceRunning(this, ServiceStudy.class.getName());
        PrimaryDrawerItem pd = (PrimaryDrawerItem) result.getDrawerItem(MENU_START_STOP);
        if (start == false) {
            pd = pd.withName("Start Data Collection").withIcon(FontAwesome.Icon.faw_play_circle_o);
        } else {
            pd = pd.withName("Stop Data Collection").withIcon(FontAwesome.Icon.faw_pause_circle_o);
        }
        int pos = result.getPosition(MENU_START_STOP);
        result.removeItem(MENU_START_STOP);
        result.addItemAtPosition(pd, pos);
        result.setSelection(MyMenu.MENU_HOME, false);
        selectedMenu=MyMenu.MENU_HOME;
    }

    void createDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.cover_image)
                .withCompactStyle(true)
                .addProfiles(new MyMenu().getHeaderContent(ServerCP.getUserName(getBaseContext()), responseCallBack))
                .build();
        boolean start = AppInfo.isServiceRunning(this, ServiceStudy.class.getName());

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(new MyMenu().getMenuContent(start, responseCallBack))
                .build();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (selectedMenu != MyMenu.MENU_HOME) {
                responseCallBack.onResponse(null, MyMenu.MENU_HOME);
            } else {
                stopAll();
                super.onBackPressed();
            }
        }
    }

    public ResponseCallBack responseCallBack = new ResponseCallBack() {
        @Override
        public void onResponse(final IDrawerItem drawerItem, final int responseId) {
            Log.d("abc", "r1_count=:" + (++cc) + " " + (startT - DateTime.getDateTime()));
            startT = DateTime.getDateTime();
            if(selectedMenu==responseId) return;
            selectedMenu = responseId;
            if (drawerItem != null)
                toolbar.setTitle(getStudyName() + ": " + ((Nameable) drawerItem).getName().getText(AbstractActivityMenu.this));
            else toolbar.setTitle(getStudyName());
            switch (responseId) {
                case MyMenu.MENU_HOME:
                    result.setSelection(MyMenu.MENU_HOME, false);
                    selectedMenu=MyMenu.MENU_HOME;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commitAllowingStateLoss();
                    break;
                case MENU_START_STOP:
                    boolean start = AppInfo.isServiceRunning(AbstractActivityMenu.this, ServiceStudy.class.getName());

                    if (start) {
                        stopDataCollection();
                    } else {
                        startDataCollection();
                    }
                    toolbar.setTitle(getStudyName());
                    updateUI();
                    result.setSelection(MyMenu.MENU_HOME, false);
                    selectedMenu=MyMenu.MENU_HOME;
                    break;
                case MyMenu.MENU_RESET:
                    boolean startt = AppInfo.isServiceRunning(AbstractActivityMenu.this, ServiceStudy.class.getName());

                    if (startt) {
                        resetDataCollection();
                    } else {
                        startDataCollection();
                    }
                    toolbar.setTitle(getStudyName());
                    result.setSelection(MyMenu.MENU_HOME, false);
                    selectedMenu=MyMenu.MENU_HOME;
                    break;
                case MyMenu.MENU_UPDATE:
                    try {
                        Intent ii = new Intent(AbstractActivityMenu.this, ServiceStudy.class);
                        stopService(ii);
                        StudyCP.setStarted(AbstractActivityMenu.this, false);
                        isServiceRunning = false;
                    }catch (Exception e){}
                    Intent intent = new Intent();
                    String p = AppBasicInfo.getMCerebrum(AbstractActivityMenu.this);

                    intent.putExtra("STUDY", getPackageName());
                    intent.setComponent(new ComponentName(p, p + ".UI.check_update.ActivityCheckUpdate"));
                    startActivity(intent);
                    finish();
                    break;
                case MyMenu.MENU_SETTINGS:
                    stopAndQuit();
                    break;
                case MyMenu.MENU_CONTACT_US:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentContactUs()).commitAllowingStateLoss();
                    break;
                case MyMenu.MENU_WORK_ANNOTATION:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWorkAnnotation()).commitAllowingStateLoss();
                    break;
                case MyMenu.MENU_HELP:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHelp()).commitAllowingStateLoss();
                    break;


                default:
            }
        }
    };
}

