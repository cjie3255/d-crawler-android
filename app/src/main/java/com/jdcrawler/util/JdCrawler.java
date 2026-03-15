package com.jdcrawler.util;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 京东爬虫核心类
 * 负责搜索关键词并提取产品件数
 */
public class JdCrawler {
    private static final String TAG = "JdCrawler";
    private static final String JD_SEARCH_URL = "https://search.jd.com/Search?keyword=%s&enc=utf-8";
    
    private OkHttpClient client;
    private String currentCookie;
    private boolean isRunning = false;
    private int delayMs = 2000; // 默认请求间隔 2 秒

    public JdCrawler() {
        initClient();
    }

    private void initClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, java.util.List<Cookie> cookies) {
                        // 保存 Cookie（可选实现）
                    }

                    @Override
                    public java.util.List<Cookie> loadForRequest(HttpUrl url) {
                        // 加载 Cookie
                        return java.util.Collections.emptyList();
                    }
                })
                .build();
    }

    /**
     * 设置登录 Cookie
     */
    public void setCookie(String cookie) {
        this.currentCookie = cookie;
    }

    /**
     * 设置请求间隔（毫秒）
     * 建议至少 2000ms，避免被封
     */
    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    /**
     * 搜索单个关键词，返回产品件数
     */
    public int searchProductCount(String keyword) throws IOException {
        String encodedKeyword = java.net.URLEncoder.encode(keyword, "utf-8");
        String url = String.format(JD_SEARCH_URL, encodedKeyword);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Referer", "https://www.jd.com/");

        if (currentCookie != null && !currentCookie.isEmpty()) {
            requestBuilder.addHeader("Cookie", currentCookie);
        }

        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败：" + response.code());
            }

            String html = response.body().string();
            return parseProductCount(html);
        }
    }

    /**
     * 从 HTML 中解析产品件数
     */
    private int parseProductCount(String html) {
        try {
            Document doc = Jsoup.parse(html);
            
            // 尝试多种选择器查找产品件数
            // 京东通常在搜索结果页显示 "共 XX 万件" 或类似信息
            
            // 方法 1: 查找包含"件"的元素
            Element countElement = doc.selectFirst(".result-total, .summary-title, .fw-num");
            if (countElement != null) {
                String text = countElement.text();
                Integer count = extractNumber(text);
                if (count != null) {
                    return count;
                }
            }
            
            // 方法 2: 查找包含"共"和"件"的文本
            Elements elements = doc.getAllElements();
            for (Element element : elements) {
                String text = element.text();
                if (text.contains("共") && text.contains("件")) {
                    Integer count = extractNumber(text);
                    if (count != null) {
                        return count;
                    }
                }
            }
            
            // 方法 3: 尝试从脚本中解析
            Element script = doc.selectFirst("script:containsData(searchData)");
            if (script != null) {
                String data = script.data();
                Integer count = parseFromJson(data);
                if (count != null) {
                    return count;
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "解析 HTML 失败", e);
        }
        
        return -1; // 未找到
    }

    /**
     * 从文本中提取数字
     */
    private Integer extractNumber(String text) {
        try {
            // 匹配数字（包括万、亿等单位）
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)(万|亿)?");
            java.util.regex.Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                double number = Double.parseDouble(matcher.group(1));
                String unit = matcher.group(2);
                
                if ("万".equals(unit)) {
                    number *= 10000;
                } else if ("亿".equals(unit)) {
                    number *= 100000000;
                }
                
                return (int) number;
            }
        } catch (Exception e) {
            Log.e(TAG, "提取数字失败", e);
        }
        return null;
    }

    /**
     * 从 JSON 数据中解析
     */
    private Integer parseFromJson(String jsonData) {
        try {
            // 尝试提取 JSON 并解析
            int start = jsonData.indexOf('{');
            int end = jsonData.lastIndexOf('}');
            if (start >= 0 && end > start) {
                String json = jsonData.substring(start, end + 1);
                JSONObject obj = new JSONObject(json);
                // 根据实际 JSON 结构调整
                if (obj.has("resultCount")) {
                    return obj.getInt("resultCount");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析 JSON 失败", e);
        }
        return null;
    }

    /**
     * 批量搜索关键词
     */
    public interface SearchCallback {
        void onProgress(int current, int total, String keyword, int count);
        void onComplete();
        void onError(String error);
    }

    /**
     * 开始批量搜索
     */
    public void batchSearch(final java.util.List<String> keywords, final SearchCallback callback) {
        new Thread(() -> {
            isRunning = true;
            int total = keywords.size();
            
            for (int i = 0; i < total && isRunning; i++) {
                String keyword = keywords.get(i);
                try {
                    int count = searchProductCount(keyword);
                    
                    if (callback != null) {
                        callback.onProgress(i + 1, total, keyword, count);
                    }
                    
                    // 延迟，避免请求过快
                    if (i < total - 1) {
                        Thread.sleep(delayMs);
                    }
                    
                } catch (IOException e) {
                    Log.e(TAG, "搜索失败：" + keyword, e);
                    if (callback != null) {
                        callback.onError("搜索失败：" + keyword + " - " + e.getMessage());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            if (callback != null) {
                callback.onComplete();
            }
            isRunning = false;
        }).start();
    }

    /**
     * 停止搜索
     */
    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
