package org.md2k.studymperf.data_quality;

import android.os.Handler;

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
public class UserViewDataQuality{
    private static final String TAG = UserViewDataQuality.class.getSimpleName();
    private Handler handler;
    private DataQualityManager dataQualityManager;
    private ResultCallback resultCallback;

    public UserViewDataQuality(DataQualityManager dataQualityManager) {
        handler=new Handler();
        this.dataQualityManager=dataQualityManager;
    }
    public void set(ResultCallback resultCallback) {
        this.resultCallback = resultCallback;
        handler.post(runnableUpdateView);
    }
    public void clear() {
        handler.removeCallbacks(runnableUpdateView);
    }

    public void updateView(){
        if(dataQualityManager==null || dataQualityManager.dataQualityInfos==null || dataQualityManager.dataQualityInfos.size()==0)  return;
        int[] result=new int[dataQualityManager.dataQualityInfos.size()];
        ArrayList<DataQualityInfo> dataQualityInfos = dataQualityManager.dataQualityInfos;
        for (int i = 0; i < dataQualityInfos.size(); i++) {
            result[i]=dataQualityInfos.get(i).getQuality();
        }
        resultCallback.onResult(result);
    }


    private Runnable runnableUpdateView = new Runnable() {
        @Override
        public void run() {
            updateView();
            handler.postDelayed(this, 5000);
        }
    };

}
