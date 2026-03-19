package com.kylan.hotel.service.impl;

import com.kylan.hotel.common.BusinessException;
import com.kylan.hotel.common.SecurityUtils;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.entity.*;
import com.kylan.hotel.domain.vo.DailyReportVO;
import com.kylan.hotel.domain.vo.DashboardVO;
import com.kylan.hotel.domain.vo.PropertyStatVO;
import com.kylan.hotel.mapper.*;
import com.kylan.hotel.service.FinanceOpsService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceOpsServiceImpl implements FinanceOpsService {

    private final FinanceRefundRecordMapper financeRefundRecordMapper;
    private final FinanceBillDetailMapper financeBillDetailMapper;
    private final InvoiceRecordMapper invoiceRecordMapper;
    private final HousekeepingTaskMapper housekeepingTaskMapper;
    private final MaintenanceTicketMapper maintenanceTicketMapper;
    private final ReportMapper reportMapper;
    private final HotelPaymentRecordMapper hotelPaymentRecordMapper;
    private final HotelOrderMapper hotelOrderMapper;
    private final HotelRoomMapper hotelRoomMapper;
    private final AuditLogRecordMapper auditLogRecordMapper;

    @Override
    public Long createRefund(RefundCreateRequest request) {
        FinanceRefundRecord entity = new FinanceRefundRecord();
        entity.setPaymentId(request.getPaymentId());
        entity.setOrderId(request.getOrderId());
        entity.setRefundAmount(request.getRefundAmount());
        entity.setRefundReason(request.getRefundReason());
        entity.setRefundStatus("SUCCESS");
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        financeRefundRecordMapper.insert(entity);
        writeAudit("FINANCE", String.valueOf(entity.getId()), "REFUND", "create refund record");
        return entity.getId();
    }

    @Override
    public List<FinanceRefundRecord> listRefunds() {
        return financeRefundRecordMapper.findAll().stream()
                .filter(item -> {
                    HotelOrderExt order = hotelOrderMapper.findById(item.getOrderId());
                    return order != null && canAccessProperty(order.getPropertyId());
                })
                .toList();
    }

    @Override
    public Long createBillDetail(BillDetailCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        FinanceBillDetail entity = new FinanceBillDetail();
        entity.setPropertyId(request.getPropertyId());
        entity.setOrderId(request.getOrderId());
        entity.setBillType(request.getBillType());
        entity.setBillItem(request.getBillItem());
        entity.setAmount(request.getAmount());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        financeBillDetailMapper.insert(entity);
        writeAudit("FINANCE", String.valueOf(entity.getId()), "BILL", "create bill detail");
        return entity.getId();
    }

    @Override
    public List<FinanceBillDetail> listBillDetails() {
        return financeBillDetailMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createInvoice(InvoiceCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        InvoiceRecord entity = new InvoiceRecord();
        entity.setOrderId(request.getOrderId());
        entity.setPropertyId(request.getPropertyId());
        entity.setInvoiceType(request.getInvoiceType() == null ? "ELECTRONIC" : request.getInvoiceType());
        entity.setInvoiceTitle(request.getInvoiceTitle());
        entity.setTaxNo(request.getTaxNo());
        entity.setAmount(request.getAmount() == null ? BigDecimal.ZERO : request.getAmount());
        entity.setInvoiceStatus("PENDING");
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        invoiceRecordMapper.insert(entity);
        writeAudit("INVOICE", String.valueOf(entity.getId()), "CREATE", "create invoice request");
        return entity.getId();
    }

    @Override
    public List<InvoiceRecord> listInvoices() {
        return invoiceRecordMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public Long createHousekeepingTask(HousekeepingTaskCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        HousekeepingTask entity = new HousekeepingTask();
        entity.setPropertyId(request.getPropertyId());
        entity.setRoomId(request.getRoomId());
        entity.setBizDate(request.getBizDate());
        entity.setTaskStatus("TODO");
        entity.setAssignee(request.getAssignee());
        entity.setRemark(request.getRemark());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        housekeepingTaskMapper.insert(entity);
        writeAudit("HOUSEKEEPING", String.valueOf(entity.getId()), "CREATE", "create housekeeping task");
        return entity.getId();
    }

    @Override
    public List<HousekeepingTask> listHousekeepingTasks() {
        return housekeepingTaskMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public void updateHousekeepingTaskStatus(BizStatusUpdateRequest request) {
        HousekeepingTask entity = new HousekeepingTask();
        entity.setId(request.getId());
        entity.setTaskStatus(request.getStatus());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        housekeepingTaskMapper.updateStatus(entity);
    }

    @Override
    public Long createMaintenanceTicket(MaintenanceTicketCreateRequest request) {
        SecurityUtils.assertPropertyAccessible(request.getPropertyId());
        MaintenanceTicket entity = new MaintenanceTicket();
        entity.setPropertyId(request.getPropertyId());
        entity.setRoomId(request.getRoomId());
        entity.setIssueType(request.getIssueType());
        entity.setIssueDescription(request.getIssueDescription());
        entity.setTicketStatus("OPEN");
        entity.setAssignee(request.getAssignee());
        entity.setCreatedBy(SecurityUtils.currentUsername());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        entity.setDeleted(0);
        maintenanceTicketMapper.insert(entity);
        writeAudit("MAINTENANCE", String.valueOf(entity.getId()), "CREATE", "create maintenance ticket");
        return entity.getId();
    }

    @Override
    public List<MaintenanceTicket> listMaintenanceTickets() {
        return maintenanceTicketMapper.findAll().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public void updateMaintenanceTicketStatus(BizStatusUpdateRequest request) {
        MaintenanceTicket entity = new MaintenanceTicket();
        entity.setId(request.getId());
        entity.setTicketStatus(request.getStatus());
        entity.setUpdatedBy(SecurityUtils.currentUsername());
        maintenanceTicketMapper.updateStatus(entity);
    }

    @Override
    public List<DailyReportVO> dailyReport() {
        return reportMapper.selectDailyReport().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public List<PropertyStatVO> propertyStats() {
        return reportMapper.selectPropertyStat().stream()
                .filter(item -> canAccessProperty(item.getPropertyId()))
                .toList();
    }

    @Override
    public DashboardVO dashboard() {
        List<DailyReportVO> daily = dailyReport();
        List<PropertyStatVO> stats = propertyStats();
        int todayOrderCount = daily.stream().mapToInt(item -> item.getOrderCount() == null ? 0 : item.getOrderCount()).sum();
        BigDecimal revenue = daily.stream().map(item -> item.getNetRevenue() == null ? BigDecimal.ZERO : item.getNetRevenue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int inHouse = (int) hotelRoomMapper.findAll().stream().filter(item -> "OCCUPIED".equals(item.getStatus())).count();
        int warningInventoryCount = (int) stats.stream().filter(item -> item.getOccupancyRate() != null && item.getOccupancyRate().compareTo(new BigDecimal("0.9")) >= 0).count();
        return DashboardVO.builder()
                .todayOrderCount(todayOrderCount)
                .todayRevenue(revenue)
                .inHouseCount(inHouse)
                .warningInventoryCount(warningInventoryCount)
                .build();
    }

    @Override
    public byte[] exportDailyReportExcel() {
        List<DailyReportVO> list = dailyReport();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("daily_report");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Property ID");
            header.createCell(1).setCellValue("Property Name");
            header.createCell(2).setCellValue("Order Count");
            header.createCell(3).setCellValue("Payment Amount");
            header.createCell(4).setCellValue("Refund Amount");
            header.createCell(5).setCellValue("Net Revenue");

            int rowIndex = 1;
            for (DailyReportVO item : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getPropertyId());
                row.createCell(1).setCellValue(item.getPropertyName());
                row.createCell(2).setCellValue(item.getOrderCount());
                row.createCell(3).setCellValue(item.getPaymentAmount() == null ? 0D : item.getPaymentAmount().doubleValue());
                row.createCell(4).setCellValue(item.getRefundAmount() == null ? 0D : item.getRefundAmount().doubleValue());
                row.createCell(5).setCellValue(item.getNetRevenue() == null ? 0D : item.getNetRevenue().doubleValue());
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BusinessException("export daily report failed");
        }
    }

    @Override
    public byte[] exportPropertyStatExcel() {
        List<PropertyStatVO> list = propertyStats();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("property_stats");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Property ID");
            header.createCell(1).setCellValue("Property Name");
            header.createCell(2).setCellValue("Room Count");
            header.createCell(3).setCellValue("Occupied Room Count");
            header.createCell(4).setCellValue("Occupancy Rate");

            int rowIndex = 1;
            for (PropertyStatVO item : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getPropertyId());
                row.createCell(1).setCellValue(item.getPropertyName());
                row.createCell(2).setCellValue(item.getRoomCount());
                row.createCell(3).setCellValue(item.getOccupiedRoomCount());
                row.createCell(4).setCellValue(item.getOccupancyRate() == null ? 0D : item.getOccupancyRate().doubleValue());
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BusinessException("export property stat failed");
        }
    }

    private boolean canAccessProperty(Long propertyId) {
        if (SecurityUtils.hasPermission("scope:all")) {
            return true;
        }
        return SecurityUtils.propertyScopes().contains(propertyId);
    }

    private void writeAudit(String module, String bizNo, String action, String content) {
        AuditLogRecord audit = new AuditLogRecord();
        audit.setModuleCode(module);
        audit.setBizNo(bizNo);
        audit.setActionType(action);
        audit.setContent(content);
        audit.setOperator(SecurityUtils.currentUsername());
        audit.setPropertyId(SecurityUtils.currentPropertyId());
        auditLogRecordMapper.insert(audit);
    }
}
