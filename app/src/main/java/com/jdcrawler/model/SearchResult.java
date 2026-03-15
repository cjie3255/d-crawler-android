package com.jdcrawler.model;

/**
 * 搜索结果数据模型
 */
public class SearchResult {
    private long id;
    private String keyword;          // 搜索关键词
    private int productCount;        // 产品件数
    private String account;          // 使用的账号
    private long searchTime;         // 搜索时间
    private String notes;            // 备注

    public SearchResult() {}

    public SearchResult(String keyword, int productCount, String account) {
        this.keyword = keyword;
        this.productCount = productCount;
        this.account = account;
        this.searchTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }
    
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    
    public long getSearchTime() { return searchTime; }
    public void setSearchTime(long searchTime) { this.searchTime = searchTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
