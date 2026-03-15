package com.jdcrawler.util;

import com.jdcrawler.model.SearchResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * CSV 导出工具
 */
public class CsvExporter {
    
    /**
     * 导出搜索结果到 CSV 文件
     */
    public static void export(List<SearchResult> results, File outputFile) throws IOException {
        FileWriter writer = new FileWriter(outputFile);
        
        try {
            // 写入 BOM 头（让 Excel 正确识别 UTF-8）
            writer.write('\ufeff');
            
            // 写入表头
            writer.write("序号，关键词，产品件数，账号，搜索时间，备注\n");
            
            // 写入数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            int index = 1;
            for (SearchResult result : results) {
                StringBuilder line = new StringBuilder();
                line.append(index++).append(",");
                line.append(escapeCsv(result.getKeyword())).append(",");
                line.append(result.getProductCount()).append(",");
                line.append(escapeCsv(result.getAccount())).append(",");
                line.append(sdf.format(new Date(result.getSearchTime()))).append(",");
                line.append(escapeCsv(result.getNotes() != null ? result.getNotes() : "")).append("\n");
                writer.write(line.toString());
            }
            
        } finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * 转义 CSV 字段（处理逗号、引号、换行）
     */
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        
        // 如果包含特殊字符，用引号包裹
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // 转义引号
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }

    /**
     * 导出为 Excel 格式（需要 Apache POI）
     */
    public static void exportToExcel(List<SearchResult> results, File outputFile) throws IOException {
        // 这个需要 Apache POI 库，暂时先用 CSV
        // 如果需要使用 Excel 格式，可以后续实现
        export(results, outputFile);
    }
}
