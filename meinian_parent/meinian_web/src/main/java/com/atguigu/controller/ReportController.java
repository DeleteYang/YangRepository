package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.constant.MessageConstant;
import com.atguigu.entity.Result;
import com.atguigu.service.MemberService;
import com.atguigu.service.ReportService;
import com.atguigu.service.SetmealService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    MemberService memberService;

    @Reference
    SetmealService setmealService;

    @Reference
    ReportService reportService;

    /**获取运营数据表
     *导出Excel报表
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletResponse response, HttpServletRequest request){
        try {
            //远程调用报表服务获取报表数据
            Map<String, Object> result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //获得Excel模板文件绝对路径
            //file.separator这个代表系统目录中的间隔符，说白了就是斜线，不过有时候需要双线，有时候是单线，你用这个静态变量就解决兼容问题了。
            String temlateRealPath  = request.getSession().getServletContext().getRealPath("template")+ File.separator+"report_template.xlsx";

            //读取模板文件创建Excel表格对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));
            XSSFSheet sheetAt = workbook.getSheetAt(0);

            XSSFRow row = sheetAt.getRow(2);
            row.getCell(5).setCellValue(reportDate);//设置日期

            row = sheetAt.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheetAt.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheetAt.getRow(7);
            row.getCell(5).setCellValue(todayNewMember);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日出游数

            row = sheetAt.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周新出游数

            row = sheetAt.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月出游数

            int rowNum = 12;
            for (Map map : hotSetmeal) {
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count ");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheetAt.getRow(rowNum++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//百分比

            }

            //通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            // 下载的数据类型（excel类型）
            response.setContentType("application/vnd.ms-excel");
            //设置下载的形式（通过附件下载）
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            workbook.write(out);

            out.flush();
            out.close();
            workbook.close();

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL,null);

        }
    }


    /**
     * 运营数据统计（页面）
     * @return
     */
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData() {

        try {
            Map<String,Object> map = reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_SUCCESS);
        }

    }

    /**
     * 组织套餐预约人数占比（饼状图）
     * @return
     */
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() {

        //组织套餐名称，套餐对应的数据
        List<Map<String,Object>> setmealCount = setmealService.findSetmealCount();
        Map<String,Object> map = new HashMap<>();

        map.put("setmealCount", setmealCount);
        //组织套餐名称集合，
        ArrayList<String> list = new ArrayList<>();
        for (Map<String, Object> count : setmealCount) {
            String name = (String) count.get("name");
            list.add(name);
        }

        map.put("setmealNames", list);

        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,map);
    }

    /**
     * 会员统计
     * @return
     */
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() {
        //获取日历对象
        Calendar calendar = Calendar.getInstance();
        //根据当前时间，获取前12个月的日历(当前日历2020-04，12个月前，日历时间2019-05)
        //第二个参数，日历字段
        //第二个参数，要添加到字段中的日期或时间
        calendar.add(Calendar.MONTH, -12);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            //第一个参数是月份
            //第二个参数是月份加一个月
            calendar.add(Calendar.MONTH, 1);
            list.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
        }

        HashMap<String, Object> map = new HashMap<>();
        //把过去12个月的日期存储在map中
        map.put("months", list);
        //查询所有会员
        List<Integer> memberCount = memberService.findMemberCountByMonth(list);
        map.put("memberCount",memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);

    }
}
