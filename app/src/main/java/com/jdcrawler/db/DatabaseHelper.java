package com.jdcrawler.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jdcrawler.model.SearchResult;
import com.jdcrawler.model.JdAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库帮助类
 * 管理搜索结果和账号数据
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jd_crawler.db";
    private static final int DATABASE_VERSION = 1;

    // 搜索结果表
    private static final String TABLE_RESULTS = "search_results";
    private static final String COL_ID = "id";
    private static final String COL_KEYWORD = "keyword";
    private static final String COL_PRODUCT_COUNT = "product_count";
    private static final String COL_ACCOUNT = "account";
    private static final String COL_SEARCH_TIME = "search_time";
    private static final String COL_NOTES = "notes";

    // 账号表
    private static final String TABLE_ACCOUNTS = "jd_accounts";
    private static final String COL_USERNAME = "username";
    private static final String COL_COOKIE = "cookie";
    private static final String COL_NICKNAME = "nickname";
    private static final String COL_IS_ACTIVE = "is_active";
    private static final String COL_LAST_USED = "last_used";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建搜索结果表
        String createResultsTable = "CREATE TABLE " + TABLE_RESULTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_KEYWORD + " TEXT NOT NULL, " +
                COL_PRODUCT_COUNT + " INTEGER, " +
                COL_ACCOUNT + " TEXT, " +
                COL_SEARCH_TIME + " INTEGER, " +
                COL_NOTES + " TEXT)";
        db.execSQL(createResultsTable);

        // 创建账号表
        String createAccountsTable = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL, " +
                COL_COOKIE + " TEXT, " +
                COL_NICKNAME + " TEXT, " +
                COL_IS_ACTIVE + " INTEGER DEFAULT 0, " +
                COL_LAST_USED + " INTEGER)";
        db.execSQL(createAccountsTable);

        // 创建索引
        db.execSQL("CREATE INDEX idx_keyword ON " + TABLE_RESULTS + "(" + COL_KEYWORD + ")");
        db.execSQL("CREATE INDEX idx_search_time ON " + TABLE_RESULTS + "(" + COL_SEARCH_TIME + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        onCreate(db);
    }

    // ==================== 搜索结果操作 ====================

    /**
     * 添加搜索结果
     */
    public long addSearchResult(SearchResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_KEYWORD, result.getKeyword());
        values.put(COL_PRODUCT_COUNT, result.getProductCount());
        values.put(COL_ACCOUNT, result.getAccount());
        values.put(COL_SEARCH_TIME, result.getSearchTime());
        values.put(COL_NOTES, result.getNotes());

        long id = db.insert(TABLE_RESULTS, null, values);
        db.close();
        return id;
    }

    /**
     * 批量添加搜索结果
     */
    public void addSearchResults(List<SearchResult> results) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (SearchResult result : results) {
                ContentValues values = new ContentValues();
                values.put(COL_KEYWORD, result.getKeyword());
                values.put(COL_PRODUCT_COUNT, result.getProductCount());
                values.put(COL_ACCOUNT, result.getAccount());
                values.put(COL_SEARCH_TIME, result.getSearchTime());
                values.put(COL_NOTES, result.getNotes());
                db.insert(TABLE_RESULTS, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 获取所有搜索结果
     */
    public List<SearchResult> getAllResults() {
        List<SearchResult> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESULTS, null, null, null, null, null, COL_SEARCH_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                SearchResult result = new SearchResult();
                result.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                result.setKeyword(cursor.getString(cursor.getColumnIndexOrThrow(COL_KEYWORD)));
                result.setProductCount(cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_COUNT)));
                result.setAccount(cursor.getString(cursor.getColumnIndexOrThrow(COL_ACCOUNT)));
                result.setSearchTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_SEARCH_TIME)));
                result.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)));
                results.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;
    }

    /**
     * 删除所有结果
     */
    public void deleteAllResults() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESULTS, null, null);
        db.close();
    }

    /**
     * 获取结果数量
     */
    public int getResultCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESULTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // ==================== 账号操作 ====================

    /**
     * 添加账号
     */
    public long addAccount(JdAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, account.getUsername());
        values.put(COL_COOKIE, account.getCookie());
        values.put(COL_NICKNAME, account.getNickname());
        values.put(COL_IS_ACTIVE, account.isActive() ? 1 : 0);
        values.put(COL_LAST_USED, account.getLastUsed());

        long id = db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
        return id;
    }

    /**
     * 获取所有账号
     */
    public List<JdAccount> getAllAccounts() {
        List<JdAccount> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                JdAccount account = new JdAccount();
                account.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                account.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
                account.setCookie(cursor.getString(cursor.getColumnIndexOrThrow(COL_COOKIE)));
                account.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COL_NICKNAME)));
                account.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_ACTIVE)) == 1);
                account.setLastUsed(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_USED)));
                accounts.add(account);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accounts;
    }

    /**
     * 获取当前活跃账号
     */
    public JdAccount getActiveAccount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, null, COL_IS_ACTIVE + "=1", null, null, null, null);

        JdAccount account = null;
        if (cursor.moveToFirst()) {
            account = new JdAccount();
            account.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            account.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
            account.setCookie(cursor.getString(cursor.getColumnIndexOrThrow(COL_COOKIE)));
            account.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(COL_NICKNAME)));
            account.setActive(true);
            account.setLastUsed(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LAST_USED)));
        }
        cursor.close();
        db.close();
        return account;
    }

    /**
     * 设置活跃账号
     */
    public void setActiveAccount(long accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 先取消所有账号的活跃状态
        ContentValues clearValues = new ContentValues();
        clearValues.put(COL_IS_ACTIVE, 0);
        db.update(TABLE_ACCOUNTS, clearValues, null, null);
        
        // 设置指定账号为活跃
        ContentValues values = new ContentValues();
        values.put(COL_IS_ACTIVE, 1);
        values.put(COL_LAST_USED, System.currentTimeMillis());
        db.update(TABLE_ACCOUNTS, values, COL_ID + "=?", new String[]{String.valueOf(accountId)});
        
        db.close();
    }

    /**
     * 删除账号
     */
    public void deleteAccount(long accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, COL_ID + "=?", new String[]{String.valueOf(accountId)});
        db.close();
    }

    /**
     * 更新账号 Cookie
     */
    public void updateAccountCookie(long accountId, String cookie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COOKIE, cookie);
        db.update(TABLE_ACCOUNTS, values, COL_ID + "=?", new String[]{String.valueOf(accountId)});
        db.close();
    }
}
