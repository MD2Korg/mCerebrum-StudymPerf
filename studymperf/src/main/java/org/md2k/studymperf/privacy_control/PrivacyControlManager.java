package org.md2k.studymperf.privacy_control;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.privacy.PrivacyData;
import org.md2k.studymperf.AbstractActivityBasics;
import org.md2k.studymperf.MyApplication;

import java.util.ArrayList;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
class PrivacyControlManager {
    private PrivacyData privacyData;

    PrivacyControlManager() {
        privacyData = null;
    }

    public void set() {
        privacyData = readFromDataKit();
    }

    public void clear() {
        privacyData = null;
    }
    private PrivacyData readFromDataKit() {
        PrivacyData privacyData = null;
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(createDataSourceBuilder());
            if (dataSourceClients.size() > 0) {
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), 1);
                if (dataTypes.size() != 0) {
                    try {
                        DataTypeJSONObject dataTypeJSONObject = (DataTypeJSONObject) dataTypes.get(0);
                        Gson gson = new Gson();
                        privacyData = gson.fromJson(dataTypeJSONObject.getSample().toString(), PrivacyData.class);
                    } catch (Exception ignored) {
                        privacyData = null;
                        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
                    }
                }
            }
        } catch (DataKitException e) {
            privacyData=null;
            Context context = MyApplication.getContext();
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }
        return privacyData;
    }
    long getRemainingTime(){
        if (privacyData == null) return -1;
        if (!privacyData.isStatus()) return -1;
        if (privacyData.getStartTimeStamp() + privacyData.getDuration().getValue() < DateTime.getDateTime())
            return -1;
        return privacyData.getStartTimeStamp() + privacyData.getDuration().getValue() - DateTime.getDateTime();
    }

    private DataSourceBuilder createDataSourceBuilder() {
        return new DataSourceBuilder().setType(DataSourceType.PRIVACY);
    }
}
