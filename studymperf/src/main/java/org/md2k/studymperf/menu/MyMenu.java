package org.md2k.studymperf.menu;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.graphics.Color;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.md2k.studymperf.R;
import org.md2k.studymperf.ResponseCallBack;


public class MyMenu {
    public static final int MENU_HOME = 0;
    public static final int MENU_HELP = 1;
    public static final int MENU_SETTINGS = 2;
    public static final int MENU_LOGIN = 3;
    public static final int MENU_LOGOUT = 4;
    public static final int MENU_START_STOP=5;
    public static final int MENU_CONTACT_US=6;
    public static final int MENU_WORK_ANNOTATION =7;
    public static final int MENU_RESET=8;

//    abstract IProfile[] getHeaderContentType(final Context context, UserInfo userInfo, StudyInfo studyInfo, final ResponseCallBack responseCallBack);

    public IProfile[] getHeaderContent(String userTitle, /*UserInfo userInfo, StudyInfo studyInfo, */final ResponseCallBack responseCallBack) {
        IProfile[] iProfiles=new IProfile[1];
        iProfiles[0]=new ProfileDrawerItem().withName(userTitle).withIcon(R.mipmap.ic_launcher);
/*
        if(isLoggedin){
            iProfiles[1] = new ProfileSettingDrawerItem().withName("Login").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    responseCallBack.onResponse(null,MENU_LOGIN);
                    return false;
                }
            });
        }else{
            iProfiles[1] = new ProfileSettingDrawerItem().withName("Logout").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    responseCallBack.onResponse(null, MENU_LOGOUT);
                    return false;
                }
            });
        }
*/
        return iProfiles;
    }
    private MenuContent[] menuContentWithStart = new MenuContent[]{
            new MenuContent("Home", FontAwesome.Icon.faw_home, MenuContent.PRIMARY_DRAWER_ITEM, MENU_HOME,0),
            new MenuContent("Settings", FontAwesome.Icon.faw_cog, MenuContent.PRIMARY_DRAWER_ITEM, MENU_SETTINGS,0),
            new MenuContent("Reset Application", FontAwesome.Icon.faw_refresh, MenuContent.PRIMARY_DRAWER_ITEM, MENU_RESET,0),
            new MenuContent("Start Data Collection", FontAwesome.Icon.faw_play_circle_o, MenuContent.PRIMARY_DRAWER_ITEM, MENU_START_STOP,0),
            new MenuContent("Help", FontAwesome.Icon.faw_question, MenuContent.PRIMARY_DRAWER_ITEM, MENU_HELP,0),
            new MenuContent("Contact Us", FontAwesome.Icon.faw_envelope_o, MenuContent.PRIMARY_DRAWER_ITEM, MENU_CONTACT_US,0),
            new MenuContent("Work Annotation", FontAwesome.Icon.faw_briefcase, MenuContent.PRIMARY_DRAWER_ITEM, MENU_WORK_ANNOTATION,0),

    };
    private MenuContent[] menuContentWithStop = new MenuContent[]{
            new MenuContent("Home", FontAwesome.Icon.faw_home, MenuContent.PRIMARY_DRAWER_ITEM, MENU_HOME,0),
            new MenuContent("Settings", FontAwesome.Icon.faw_cog, MenuContent.PRIMARY_DRAWER_ITEM, MENU_SETTINGS,0),
            new MenuContent("Reset Application", FontAwesome.Icon.faw_refresh, MenuContent.PRIMARY_DRAWER_ITEM, MENU_RESET,0),
            new MenuContent("Stop Data Collection", FontAwesome.Icon.faw_pause_circle_o, MenuContent.PRIMARY_DRAWER_ITEM, MENU_START_STOP,0),
            new MenuContent("Help", FontAwesome.Icon.faw_question, MenuContent.PRIMARY_DRAWER_ITEM, MENU_HELP,0),
            new MenuContent("Contact Us", FontAwesome.Icon.faw_envelope_o, MenuContent.PRIMARY_DRAWER_ITEM, MENU_CONTACT_US,0),
            new MenuContent("Workplace Annotation", FontAwesome.Icon.faw_briefcase, MenuContent.PRIMARY_DRAWER_ITEM, MENU_WORK_ANNOTATION,0),
    };

    public IDrawerItem[] getMenuContent(boolean start, final ResponseCallBack responseCallBack) {
        MenuContent[] menuContent;
        if(start){
            menuContent=menuContentWithStop;
        }else{
            menuContent=menuContentWithStart;
        }
        return getMenuContent(menuContent, responseCallBack);
    }


    private IDrawerItem[] getMenuContent(MenuContent[] menuContent, final ResponseCallBack responseCallBack) {
        IDrawerItem[] iDrawerItems = new IDrawerItem[menuContent.length];
        for (int i = 0; i < menuContent.length; i++) {
            switch (menuContent[i].type) {
                case MenuContent.PRIMARY_DRAWER_ITEM:
                    iDrawerItems[i] = new PrimaryDrawerItem().withName(menuContent[i].name).withIcon(menuContent[i].icon).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
                    if(menuContent[i].badgeValue>0){
                        ((PrimaryDrawerItem)(iDrawerItems[i])).withBadge(String.valueOf(menuContent[i].badgeValue)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));;
                    }
                    break;
                case MenuContent.SECONDARY_DRAWER_ITEM:
                    iDrawerItems[i] = new SecondaryDrawerItem().withName(menuContent[i].name).withIcon(menuContent[i].icon).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
                    if(menuContent[i].badgeValue>0){
                        ((SecondaryDrawerItem)(iDrawerItems[i])).withBadge(String.valueOf(menuContent[i].badgeValue)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700));;
                    }
                    break;
                case MenuContent.SECTION_DRAWER_ITEM:
                    iDrawerItems[i] = new SectionDrawerItem().withName(menuContent[i].name).withIdentifier(menuContent[i].identifier).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            responseCallBack.onResponse(drawerItem, (int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
            }
        }
        return iDrawerItems;
    }
}

