package com.com_engineer.leechanghyeon.facebooklogin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.com_engineer.leechanghyeon.facebooklogin.R.id.main_imageview_profile;
import static com.kakao.auth.Session.getCurrentSession;

public class MainActivity extends AppCompatActivity {
    private static final int CANCLEJOINSOCIALUSER = 54321;
    private static final int JOINSOCIALUSER = 54322;
    com.facebook.login.LoginManager fbLoginManager;
    CallbackManager callbackManager;
    private ISessionCallback mKakaoCallback;
    private Session session;
    private String TAG = "MainActivity";
    private int session_openCount=0;
    private String kakaouserid;
    ImageView iv_profile;
    private MainActivity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        Button btn_facebooklogin = (Button)findViewById(R.id.main_button_facebooklogin);
        Button btn_kakaologin = (Button)findViewById(R.id.main_button_kakaotalklogin);

        iv_profile = (ImageView)findViewById(main_imageview_profile);


        checkPermission();


        AppEventsLogger.activateApp(this);
        btn_facebooklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   facebookButton.performClick();
                FBLogin();
//                LoginManager.getInstance().logInWithReadPermissions((Activity) getApplicationContext(), Arrays.asList("public_profile", "user_friends","email"));
            }
        });
        btn_kakaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KKLogin();
            }
        });
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED){

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "앱 내의 컨텐츠 저장용으로 사용됩니다.", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE
                }, 33);


                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.

                return true;
            }
        }
        return false;
    }

    void FBLogin(){
        fbLoginManager  = com.facebook.login.LoginManager.getInstance();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends","email"));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken token = loginResult.getAccessToken();
                        final Profile profile = Profile.getCurrentProfile();
                        Log.d("LoginActivity", profile.getId());
                        Log.d("LoginActivity", profile.getName());

                        String profilePictureUri = "https://graph.facebook.com/"+profile.getId().toString()+"/picture?type=large";
                        Glide.with(getApplicationContext()).load(profilePictureUri)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(iv_profile);

                        request_Duplicate_Check_id(profile.getId(), LoggedInCase.FBLogin.getLogin_case());

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v(TAG, response.toString());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                        }
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        // App code
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode== 33&&grantResults.length>0) {
            Log.e("grant size",grantResults.length+"");
            int apply_cnt=0;
            for(int i=0;i<grantResults.length;i++)
            {
                Log.e("GrantResult"+i,grantResults[i]+":"+permissions[i]);
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED)apply_cnt++;
            }
            if(apply_cnt==permissions.length)
            {
                //허용됨

            }else
            {
                Log.d("permission", "Permission always deny");
                Toast.makeText(this,"앱 권한을 다시 설정해주세요",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void KKLogin() {
        mKakaoCallback = new ISessionCallback() {


            @Override
            public void onSessionOpened() {
                Log.e(TAG,"KAKAO Session Open");
                Log.e("asdf","aaaaa");
                if(session_openCount==0){
                    session_openCount = 1;
                    String kakaoAccessToken = session.getAccessToken();
                    Log.e("asdf","bbbbb");
                    kakaoRequestMe();
                }

            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Log.e(TAG,exception+"");
                Log.e("asdf","ccccc");
            }
        };
        session = getCurrentSession();
        session.addCallback(mKakaoCallback);
        session.checkAndImplicitOpen();
        session.open(AuthType.KAKAO_TALK, this);
    }

    private void kakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback(){

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                Log.e(TAG,userProfile.toString());
                String profilePictureUri = userProfile.getProfileImagePath();
                Glide.with(getApplicationContext()).load(profilePictureUri)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(iv_profile);

                kakaouserid = String.valueOf(userProfile.getId());

                request_Duplicate_Check_id(kakaouserid, LoggedInCase.KAKAOLogin.getLogin_case());

                Log.e("asdf","asadada");

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e(TAG,errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {

            }
        });
    }

    private void request_Duplicate_Check_id(final String kakaouserid, final String login_case) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        session_openCount = 0;
        if(resultCode == CANCLEJOINSOCIALUSER){
            return;
        }else if(resultCode == JOINSOCIALUSER){
            Toast.makeText(self,"로그인 해주세요",Toast.LENGTH_LONG).show();
        }
        if(session!=null){
            if (session.handleActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

}
