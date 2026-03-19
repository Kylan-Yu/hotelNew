package com.kylan.hotel.service;

import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.entity.FinanceBillDetail;
import com.kylan.hotel.domain.entity.FinanceRefundRecord;
import com.kylan.hotel.domain.entity.HousekeepingTask;
import com.kylan.hotel.domain.entity.InvoiceRecord;
import com.kylan.hotel.domain.entity.MaintenanceTicket;
import com.kylan.hotel.domain.vo.DailyReportVO;
import com.kylan.hotel.domain.vo.DashboardVO;
import com.kylan.hotel.domain.vo.PropertyStatVO;

import java.util.List;

public interface FinanceOpsService {
    Long createRefund(RefundCreateRequest request);

    List<FinanceRefundRecord> listRefunds();

    Long createBillDetail(BillDetailCreateRequest request);

    List<FinanceBillDetail> listBillDetails();

    Long createInvoice(InvoiceCreateRequest request);

    List<InvoiceRecord> listInvoices();

    Long createHousekeepingTask(HousekeepingTaskCreateRequest request);

    List<HousekeepingTask> listHousekeepingTasks();

    void updateHousekeepingTaskStatus(BizStatusUpdateRequest request);

    Long createMaintenanceTicket(MaintenanceTicketCreateRequest request);

    List<MaintenanceTicket> listMaintenanceTickets();

    void updateMaintenanceTicketStatus(BizStatusUpdateRequest request);

    List<DailyReportVO> dailyReport();

    List<PropertyStatVO> propertyStats();

    DashboardVO dashboard();

    byte[] exportDailyReportExcel();

    byte[] exportPropertyStatExcel();
}
