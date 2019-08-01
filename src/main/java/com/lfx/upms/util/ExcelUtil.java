package com.lfx.upms.util;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-01 16:01
 */
@UtilityClass
public class ExcelUtil {

    public static final String DEFAULT_SHEET_NAME = "sheet1";

    public static void export(String fileName, List<? extends BaseRowModel> result, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + StandardCharsets.UTF_8.encode(fileName) + ExcelTypeEnum.XLSX.getValue());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ServletOutputStream out = response.getOutputStream();
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
        Sheet sheet1 = new Sheet(1, 0, result.get(0).getClass());
        sheet1.setSheetName(DEFAULT_SHEET_NAME);
        writer.write(result, sheet1);
        writer.finish();
        out.flush();
    }
}
