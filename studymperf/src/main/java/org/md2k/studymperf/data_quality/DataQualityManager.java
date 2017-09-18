package org.md2k.studymperf.data_quality;

import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformId;
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
public class DataQualityManager {
    private static final String TAG = DataQualityManager.class.getSimpleName();
    private ArrayList<DataQuality> dataQualities;
    ArrayList<DataQualityInfo> dataQualityInfos;

    public DataQualityManager() {
        dataQualityInfos = new ArrayList<>();
        dataQualities = new ArrayList<>();
    }
    private ArrayList<DataSource> getDataSources(){
        ArrayList<DataSource> dataSources=new ArrayList<>();
        Platform pl=new PlatformBuilder().setId(PlatformId.LEFT_WRIST).build();
        Platform pr=new PlatformBuilder().setId(PlatformId.RIGHT_WRIST).build();
        dataSources.add(new DataSourceBuilder().setType(DataSourceType.DATA_QUALITY).setPlatform(pl).build());
        dataSources.add(new DataSourceBuilder().setType(DataSourceType.DATA_QUALITY).setPlatform(pr).build());
        return dataSources;
    }

    public void set() {
        final ArrayList<DataSource> dataQuality = getDataSources();
        for (int i = 0; i < dataQuality.size(); i++) {
            dataQualityInfos.add(new DataQualityInfo());
            final int finalI = i;
            dataQualities.add(new DataQuality(MyApplication.getContext(), dataQuality.get(i),dataQualityInfos.get(i),  new ReceiveCallBack() {
                @Override
                public void onReceive(DataTypeInt sample) {
                    dataQualityInfos.get(finalI).set(sample);
                }
            }));
        }
        for (int i = 0; i < dataQuality.size(); i++)
            dataQualities.get(i).start();
    }

    public void clear() {
        if (dataQualities != null) {
            for (int i = 0; i < dataQualities.size(); i++)
                dataQualities.get(i).stop();
            dataQualities.clear();
        }
        if (dataQualityInfos != null)
            dataQualityInfos.clear();
    }
}
