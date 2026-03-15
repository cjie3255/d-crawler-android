package com.jdcrawler.model;

/**
 * 京东账号模型
 */
public class JdAccount {
    private long id;
    private String username;         // 用户名/手机号
    private String cookie;           // 登录 Cookie
    private String nickname;         // 昵称
    private boolean isActive;        // 是否当前使用
    private long lastUsed;           // 最后使用时间

    public JdAccount() {}

    public JdAccount(String username, String cookie, String nickname) {
        this.username = username;
        this.cookie = cookie;
        this.nickname = nickname;
        this.isActive = false;
        this.lastUsed = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getCookie() { return cookie; }
    public void setCookie(String cookie) { this.cookie = cookie; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }
}
