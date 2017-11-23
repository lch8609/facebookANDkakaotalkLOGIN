package com.com_engineer.leechanghyeon.facebooklogin;

/**
 * Created by leechanghyeon on 2017. 11. 23..
 */

public enum LoggedInCase {
    FBLogin("FBLogin"),
    KAKAOLogin("KAKAOLogin");
    private String Login_case;



    private LoggedInCase(String login_case) {
        Login_case = login_case;
    }

    public String getLogin_case() {
        return Login_case;
    }
}
