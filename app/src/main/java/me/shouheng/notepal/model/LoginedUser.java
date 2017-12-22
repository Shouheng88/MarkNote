package me.shouheng.notepal.model;

/**
 * 已登录的账号的信息
 * Created by wangshouheng on 2017/5/25.
 */
public class LoginedUser {

    private Long userId;

    private String token;

    private String userName;

    private String userNameCN;

    private String userMotto;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameCN() {
        return userNameCN;
    }

    public void setUserNameCN(String userNameCN) {
        this.userNameCN = userNameCN;
    }

    public String getUserMotto() {
        return userMotto;
    }

    public void setUserMotto(String userMotto) {
        this.userMotto = userMotto;
    }
}
