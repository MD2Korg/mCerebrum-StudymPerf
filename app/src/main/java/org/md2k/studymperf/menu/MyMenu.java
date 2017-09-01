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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.md2k.studymperf.R;
import org.md2k.studymperf.ResponseCallBack;


public class MyMenu {
    public static final int MENU_JOIN = 0;
    public static final int MENU_ABOUT_STUDY = 1;
    public static final int MENU_LOGIN = 2;
    public static final int MENU_LOGOUT = 3;
    public static final int MENU_LEAVE = 4;
    public static final int MENU_APP_ADD_REMOVE = 5;
    public static final int MENU_APP_SETTINGS = 6;
    //    public static final int OP_REPORT = 7;
//    public static final int OP_PLOT = 8;
//    public static final int OP_EXPORT_DATA = 9;
    public static final int MENU_HELP = 10;


//    abstract IProfile[] getHeaderContentType(final Context context, UserInfo userInfo, StudyInfo studyInfo, final ResponseCallBack responseCallBack);

    public IProfile[] getHeaderContent(final Context context, String userTitle, boolean isLoggedin, /*UserInfo userInfo, StudyInfo studyInfo, */final ResponseCallBack responseCallBack) {
        IProfile[] iProfiles=new IProfile[3];
        iProfiles[0]=new ProfileDrawerItem().withName(userTitle).withIcon(R.drawable.mcerebrum);
        if(isLoggedin){
            iProfiles[1] = new ProfileSettingDrawerItem().withName("Login").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    responseCallBack.onResponse(MENU_LOGIN);
                    return false;
                }
            });
        }else{
            iProfiles[1] = new ProfileSettingDrawerItem().withName("Logout").withIcon(FontAwesome.Icon.faw_sign_in).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    responseCallBack.onResponse(MENU_LOGOUT);
                    return false;
                }
            });
        }
        iProfiles[2]= new ProfileSettingDrawerItem().withName("Leave Study").withIcon(FontAwesome.Icon.faw_chain_broken).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                responseCallBack.onResponse(MENU_LEAVE);
                return false;
            }
        });
        return iProfiles;
    }
    private MenuContent[] menuContent = new MenuContent[]{
            new MenuContent("Add/Remove Apps", FontAwesome.Icon.faw_home, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_ADD_REMOVE,1),
            new MenuContent("App Settings", FontAwesome.Icon.faw_cog, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_SETTINGS,0),
//            new MenuContent("Step by Step Settings", FontAwesome.Icon.faw_cogs, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_SETTINGS,0),
            new MenuContent("Start Study", FontAwesome.Icon.faw_play, MenuContent.PRIMARY_DRAWER_ITEM, MENU_APP_SETTINGS,0)
//            new MenuContent("Report",FontAwesome.Icon.faw_bar_chart,MenuContent.PRIMARY_DRAWER_ITEM, OP_REPORT),
//            new MenuContent("Plot",FontAwesome.Icon.faw_line_chart,MenuContent.PRIMARY_DRAWER_ITEM, OP_PLOT),
//            new MenuContent("Export Data",FontAwesome.Icon.faw_upload,MenuContent.PRIMARY_DRAWER_ITEM, OP_EXPORT_DATA),
//            new MenuContent("Help",null,MenuContent.SECTION_DRAWER_ITEM, OP_HELP),
//            new MenuContent("Quick Tour",FontAwesome.Icon.faw_film,MenuContent.SECONDARY_DRAWER_ITEM, OP_QUICK_TOUR),
//            new MenuContent("Frequently asked question",FontAwesome.Icon.faw_question_circle,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("About",null,MenuContent.SECTION_DRAWER_ITEM, OP_ABOUT),
//            new MenuContent("Who we are",FontAwesome.Icon.faw_users,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Terms and Conditions",FontAwesome.Icon.faw_pencil_square_o,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Privacy Policy",FontAwesome.Icon.faw_lock,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Contact",FontAwesome.Icon.faw_envelope_o,MenuContent.SECONDARY_DRAWER_ITEM),
//            new MenuContent("Feedback",FontAwesome.Icon.faw_comment,MenuContent.SECONDARY_DRAWER_ITEM),
    };

    public IDrawerItem[] getMenuContent(final ResponseCallBack responseCallBack) {
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
                            responseCallBack.onResponse((int) drawerItem.getIdentifier());
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
                            responseCallBack.onResponse((int) drawerItem.getIdentifier());
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
                            responseCallBack.onResponse((int) drawerItem.getIdentifier());
                            return false;
                        }
                    });
            }
        }
        return iDrawerItems;
    }
}

