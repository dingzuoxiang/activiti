package com.activiti.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelStyleUtil {
    /**
     * @return * @param null
     * @Author
     * @Description //TODO 样式居中
     * @Date 2019/7/26 14:51
     * @Param
     */
    public static void center(CellStyle cellStyle) {
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
    }

    /**
     * @return * @param null
     * @Author
     * @Description //TODO 单元格合并
     * @Date 2019/7/26 14:54
     * @Param wbSheet :工作表对象
     * firstRow ：合并的开始行
     * lastRow：合并的结束行
     * firstCol: 合并的开始列
     * lastColL: 合并的结束列
     */
    public static void mergeCell(Sheet wbSheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        wbSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }


    /**
     * @return *
     * @Author
     * @Description //TODO 标题样式 :加粗，垂直居中
     * @Date 2019/7/26 14:21
     * @Param
     */
    public static CellStyle getTitleStyle(Workbook wb, Boolean isBold, int FontISize) {
        // 标题样式（加粗，垂直居中）
        CellStyle cellStyle = wb.createCellStyle();
        center(cellStyle);
        Font font = wb.createFont();
        font.setBold(isBold);   //加粗
        font.setFontHeightInPoints((short) FontISize);  //设置标题字体大小
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * @return *
     * @Author
     * @Description //TODO 表头样式
     * @Date 2019/7/26 14:30
     * @Param
     */
    public static CellStyle getHeadStyle(Workbook wb, int fontSize) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);//设置表头的背景颜色
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillBackgroundColor(HSSFColor.AQUA.index); //设置表头的背景颜色
        center(cellStyle);
        //设置字体
        Font font = setFont(wb, fontSize);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * @return * @param null
     * @Author
     * @Description //TODO body通用样式: 居中，设置字体大小
     * @Date 2019/7/26 15:11
     * @Param
     */
    public static CellStyle getBodyStyle(Workbook wb, int fontSize) {
        CellStyle cellStyle = wb.createCellStyle();
        //设置单元格样式
        center(cellStyle);
        Font font = setFont(wb, fontSize);
        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * @return * @param null
     * @Author
     * @Description //TODO 设置单元格字体居中、并设置字体颜色
     * @Date 2019/7/26 15:26
     * @Param
     */
    public static CellStyle getFontStyle(Workbook wb, int fontSize, short color) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = setFont(wb, fontSize, color);
        center(cellStyle);
        cellStyle.setFont(font);
        return cellStyle;
    }


    /**
     * @return * @param null
     * @Author
     * @Description //TODO 设置单元格字体
     * @Date 2019/7/26 15:16
     * @Param
     */
    public static Font setFont(Workbook wb, int fontSize, short color) {
        //设置字体
        Font font = wb.createFont();
        font.setColor(color);
        font.setFontHeightInPoints((short) fontSize);
        return font;
    }

    public static Font setFont(Workbook wb, int fontSize) {
        //设置字体
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) fontSize);
        return font;
    }

    /**
     * @Author
     * @Description //TODO 设置cell边框
     * @Date 2019/7/27 16:16
     * @Param
     * @return  * @param null
     */
    public static CellStyle setCellBorder(Workbook workbook){
        CellStyle cellStyle = workbook.createCellStyle();
        //设置了边框属性
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        cellStyle.setBorderTop(BorderStyle.THIN);//上边框
        cellStyle.setBorderRight(BorderStyle.THIN);//右边框
        //设置边框颜色黑色
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        return cellStyle;
    }
}
