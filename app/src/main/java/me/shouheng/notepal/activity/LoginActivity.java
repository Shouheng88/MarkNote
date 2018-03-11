package me.shouheng.notepal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.base.CommonActivity;
import me.shouheng.notepal.config.Constants;
import me.shouheng.notepal.databinding.ActivityLoginBinding;
import me.shouheng.notepal.dialog.RegisterDialog;

public class LoginActivity extends CommonActivity<ActivityLoginBinding> {

    private final static int RC_SIGN_IN = 0x01;

//    private GoogleSignInClient mGoogleSignInClient;
//
//    private LoginButton loginButton;
//    private CallbackManager callbackManager;

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), LoginActivity.class);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        editAccount = findViewById(R.id.in_account);
//        editPassword = findViewById(R.id.in_password);



//        setTheme();
//
//        initGoogleAuth();
//
//        initFBAuth();
//
//        SignInButton signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setOnClickListener(v -> signIn());
//
//        findViewById(R.id.btn_sign_out).setOnClickListener(v -> signOut());
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        getSingedAccount();
//    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        getBinding().btnLogin.setBackgroundColor(primaryColor());
        getBinding().btnLogin.setUnpressedColor(primaryColor());

        getBinding().register.setOnClickListener(view -> showRegisterDialog());
    }

    private void showRegisterDialog() {
        RegisterDialog regDlg = RegisterDialog.newInstance();
        regDlg.setCancelable(false);
        regDlg.show(getSupportFragmentManager(), "Register Dialog");
    }

//    private void initFBAuth() {
//        callbackManager = CallbackManager.Factory.create();
//        loginButton = findViewById(R.id.login_button);
//        loginButton.setReadPermissions("email");
//        // If using in a fragment
////        loginButton.setFragment(this);
//        // Other app specific specialization
//
//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                ToastUtils.makeToast(LoginActivity.this, loginResult.toString());
//            }
//
//            @Override
//            public void onCancel() {
//                ToastUtils.makeToast(LoginActivity.this, "Canceled");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                ToastUtils.makeToast(LoginActivity.this, exception.toString());
//            }
//        });
//    }
//
//    private void initGoogleAuth() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestProfile()
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//    }
//
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void signOut() {
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> LogUtils.d("Google logout"));
//    }
//
//    private void revokeAccess() {
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, task -> LogUtils.d("Revoke Access"));
//    }
//
//    private void getSingedAccount() {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_login:
//                onConfirmLogin(v);
//                break;
//            case R.id.weibo:
//                break;
//            case R.id.wexin:
//                break;
//            case R.id.qq:
//                break;
//        }
//    }

//    private void onConfirmLogin(final View v){
//        String strAccount = editAccount.getText().toString();
//        String strPassword = editPassword.getText().toString();
//        if(TextUtils.isEmpty(strAccount)){
//            showSnackBar(v, R.string.account_toast);
//        } else if (TextUtils.isEmpty(strPassword)){
//            showSnackBar(v, R.string.password_toast);
//        } else {
//            UserKeeper.login(this, strAccount, strPassword, new UserKeeper.AccountValidCallback() {
//                @Override
//                public void onFinish(String result) {
//                    Bundle data = new Bundle();
//                    data.putString(KEY_STATUS, result);
//                    Message msg = new Message();
//                    msg.what = MSG_LN;
//                    msg.obj = v;
//                    msg.setData(data);
//                    handler.sendMessage(msg);
//                }
//                @Override
//                public void onError(Exception e) {
//                    Bundle data = new Bundle();
//                    data.putString(KEY_STATUS, UserKeeper.LoginInfo.LN_ERR);
//                    Message msg = new Message();
//                    msg.what = MSG_LN;
//                    msg.obj = v;
//                    msg.setData(data);
//                    handler.sendMessage(msg);
//                }
//            });
//        }
//    }

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//           handle(msg);
//        }
//    };

//    private void handle(Message msg){
//        View v = (View) msg.obj;
//        switch (msg.what){
//            case MSG_LN:
//                switch (msg.getData().getString(KEY_STATUS, "")){
//                    case UserKeeper.LoginInfo.OFFLINE:
//                        showSnackBar(v, R.string.check_net_toast);
//                        break;
//                    case UserKeeper.LoginInfo.ERR_NONE_EXIST:
//                        showSnackBar(v, R.string.account_none_exist_toast);
//                        break;
//                    case UserKeeper.LoginInfo.ERR_WRONG_PSD:
//                        showSnackBar(v, R.string.password_error_toast);
//                        break;
//                    case UserKeeper.LoginInfo.LN_ERR:
//                        showSnackBar(v, R.string.known_error);
//                        break;
//                    case UserKeeper.LoginInfo.LN_OK:
//                        MainActivity.activityStart(LoginActivity.this, editAccount.getText().toString());
//                }
//                break;
//            case MSG_REG:
//                switch (msg.getData().getString(KEY_STATUS, "")){
//                    case UserKeeper.RegInfo.ERR_WRONG_FORMAT:
//                        showSnackBar(v, R.string.email_format);
//                        break;
//                    case UserKeeper.RegInfo.ERR_PSD_LONG:
//                        showSnackBar(v, R.string.psd_long);
//                        break;
//                    case UserKeeper.RegInfo.ERR_PSD_SHORT:
//                        showSnackBar(v, R.string.psd_short);
//                        break;
//                    case UserKeeper.RegInfo.ERR_PSD_SIMPLE:
//                        showSnackBar(v, R.string.psd_numeric);
//                        break;
//                    case UserKeeper.RegInfo.ERR_PSD_ILLEGAL:
//                        showSnackBar(v, R.string.psd_Ill_char);
//                        break;
//                    case UserKeeper.RegInfo.OFFLINE:
//                        showSnackBar(v, R.string.check_net);
//                        break;
//                    case UserKeeper.RegInfo.ERR_ACC_EXISTED:
//                        showSnackBar(v, R.string.email_registered);
//                        break;
//                    case UserKeeper.RegInfo.REG_OK:
//                        editAccount.setText(msg.getData().getString(KEY_ACCOUNT, ""));
//                        editPassword.setText(msg.getData().getString(KEY_PASSWORD, ""));
//                        if (regDlg != null && regDlg.isShowing()){
//                            regDlg.dismiss();
//                        }
//                        showSnackBar(v, R.string.rgst_success);
//                        break;
//                    case UserKeeper.RegInfo.REG_ERR:
//                        showSnackBar(v, R.string.known_error);
//                        break;
//                }
//                break;
//        }
//    }

//    private void showSnackBar(View view, int res){
//        Snackbar.make(view, res, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
//    }

//    private void setTheme(){
//        UIRippleButton btnLogin = (UIRippleButton) findViewById(R.id.btn_login);
//        btnLogin.setBackgroundColor(primaryColor());
//        btnLogin.setUnpressedColor(primaryColor());
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case RC_SIGN_IN:
//                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                    handleSignInResult(task);
//                    break;
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            updateUI(account);
//        } catch (ApiException e) {
//            LogUtils.w("signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
//        }
//    }

//    private void updateUI(GoogleSignInAccount account) {
//        if (account != null) ToastUtils.makeToast(account.zzaaq());
//    }
}
