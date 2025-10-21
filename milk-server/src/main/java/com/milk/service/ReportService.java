package com.milk.service;

import com.milk.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * ClassName: ReportService
 * Package: com.milk.service
 * Description:
 *
 * @Author 何坤燃
 * @Create 2025/2/12 23:02
 * @Version 1.0
 */
public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
    EvaluationReportVO getEvaluationStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}
