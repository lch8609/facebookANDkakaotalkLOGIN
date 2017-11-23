package com.com_engineer.leechanghyeon.facebooklogin;

import android.app.Activity;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

/**
 * Created by leechanghyeon on 2017. 11. 23..
 */

public class KakaoSDKAdapter extends KakaoAdapter {
    public KakaoSDKAdapter(){
        super();
    }
    @Override
    public ISessionConfig getSessionConfig() {

        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return true;
            }
        };
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return new IApplicationConfig() {
            @Override
            public Activity getTopActivity() {
                return null;
            }

            @Override
            public Context getApplicationContext() {
                return GlobalApplication.getGlobalApplicationContext();
            }
        };
    }
}
