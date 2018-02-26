package org.md2k.studymperf.data_quality;

import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;

import java.util.ArrayList;
import java.util.Iterator;

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
public class DataQualityInfo {
    private static final String TAG = DataQualityInfo.class.getSimpleName();
    private static final long TIME_STORE = 60*1000;

    private static final long TIME_LIMIT_NODATA = 10*1000;
    private static final long TIME_LIMIT_GOOD_TO_NOTWORN = 12*1000;
    private static final long TIME_LIMIT_NOTWORN_TO_GOOD = 6*1000;
    private ArrayList<DataTypeInt> qualities;
    private int quality;

    DataQualityInfo() {
        quality = -1;
        qualities=new ArrayList<>();
    }

    public int getQuality() {
        return quality;
    }
    private boolean isBandOff(){
        long curTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++){
            if(qualities.get(i).getSample()!=DATA_QUALITY.BAND_OFF && curTime - qualities.get(i).getDateTime()<TIME_LIMIT_NODATA)
                return false;
        }
        return true;
    }
    private int getWorn(){
        long curTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++){
            if(qualities.get(i).getSample()==DATA_QUALITY.GOOD && curTime - qualities.get(i).getDateTime()<TIME_STORE)
                return DATA_QUALITY.GOOD;
        }
        return DATA_QUALITY.NOT_WORN;
    }


    public void set(DataTypeInt value) {
        long currentTime = DateTime.getDateTime();
        int lastSample=translate(value.getSample());
        qualities.add(new DataTypeInt(value.getDateTime(), lastSample));
        for(Iterator<DataTypeInt> i=qualities.iterator();i.hasNext();) {
            DataTypeInt dataTypeInt = i.next();
            if(dataTypeInt.getDateTime()+ TIME_STORE <currentTime)
                i.remove();
        }
        if(quality==-1) quality=lastSample;
        else if(isBandOff()) quality=DATA_QUALITY.BAND_OFF;
        else quality=getWorn();

/*
        switch(quality){
            case -1: quality=lastSample;break;
            case DATA_QUALITY.BAND_OFF:
                quality=lastSample;
                break;
            case DATA_QUALITY.NOT_WORN:
                quality=getQualityNotWornTo();
                break;
            case DATA_QUALITY.GOOD:
                quality=getQualityGoodTo();
                break;
        }
*/
/*
        Status curStatus = new Status(0, this.quality);
        message = getTitle() + " - " + curStatus.getMessage();
*/
    }
    private int getQualityGoodTo(){
        int countNoData=0;
        int countGoodData=0;
        long currentTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++) {
            if (qualities.get(i).getSample() == DATA_QUALITY.BAND_OFF)
                countNoData++;
            else if(qualities.get(i).getSample()==DATA_QUALITY.GOOD && qualities.get(i).getDateTime()+ TIME_LIMIT_GOOD_TO_NOTWORN >=currentTime)
                countGoodData++;
        }
        if(qualities.size()==countNoData)
            return DATA_QUALITY.BAND_OFF;
        else if(countGoodData>0) return DATA_QUALITY.GOOD;
        else return DATA_QUALITY.NOT_WORN;
    }

    private int getQualityNotWornTo(){
        int countNoData=0;
        int countGoodData=0;
        long currentTime=DateTime.getDateTime();
        for(int i=0;i<qualities.size();i++) {
            if (qualities.get(i).getSample() == DATA_QUALITY.BAND_OFF)
                countNoData++;
            else if(qualities.get(i).getSample()==DATA_QUALITY.GOOD && qualities.get(i).getDateTime()+ TIME_LIMIT_NOTWORN_TO_GOOD >=currentTime)
                countGoodData++;
        }
        if(qualities.size()==countNoData)
            return DATA_QUALITY.BAND_OFF;
        else if(countGoodData>0) return DATA_QUALITY.GOOD;
        else return DATA_QUALITY.NOT_WORN;
    }
    private int translate(int value) {
        switch (value) {
            case DATA_QUALITY.GOOD:
                return DATA_QUALITY.GOOD;
            case DATA_QUALITY.BAND_OFF:
                return DATA_QUALITY.BAND_OFF;
            default:
                return DATA_QUALITY.NOT_WORN;
        }
    }
}
