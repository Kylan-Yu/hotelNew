package com.kylan.hotel.controller;

import com.kylan.hotel.common.ApiResponse;
import com.kylan.hotel.domain.dto.*;
import com.kylan.hotel.domain.entity.FinanceBillDetail;
import com.kylan.hotel.domain.entity.FinanceRefundRecord;
import com.kylan.hotel.domain.entity.HousekeepingTask;
import com.kylan.hotel.domain.entity.InvoiceRecord;
import com.kylan.hotel.domain.entity.MaintenanceTicket;
import com.kylan.hotel.domain.vo.DailyReportVO;
import com.kylan.hotel.domain.vo.DashboardVO;
import com.kylan.hotel.domain.vo.PropertyStatVO;
import com.kylan.hotel.service.FinanceOpsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance-ops")
@RequiredArgsConstructor
public class FinanceOpsController {

    private final FinanceOpsService financeOpsService;

    @PostMapping("/refunds")
    @PreAuthorize("hasAuthority('finance:write')")
    public ApiResponse<Long> createRefund(@Valid @RequestBody RefundCreateRequest request) {
        return ApiResponse.success(financeOpsService.createRefund(request));
    }

    @GetMapping("/refunds")
    @PreAuthorize("hasAuthority('finance:read')")
    public ApiResponse<List<FinanceRefundRecord>> listRefunds() {
        return ApiResponse.success(financeOpsService.listRefunds());
    }

    @PostMapping("/bills")
    @PreAuthorize("hasAuthority('finance:write')")
    public ApiResponse<Long> createBill(@Valid @RequestBody BillDetailCreateRequest request) {
        return ApiResponse.success(financeOpsService.createBillDetail(request));
    }

    @GetMapping("/bills")
    @PreAuthorize("hasAuthority('finance:read')")
    public ApiResponse<List<FinanceBillDetail>> listBills() {
        return ApiResponse.success(financeOpsService.listBillDetails());
    }

    @PostMapping("/invoices")
    @PreAuthorize("hasAuthority('finance:write')")
    public ApiResponse<Long> createInvoice(@Valid @RequestBody InvoiceCreateRequest request) {
        return ApiResponse.success(financeOpsService.createInvoice(request));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAuthority('finance:read')")
    public ApiResponse<List<InvoiceRecord>> listInvoices() {
        return ApiResponse.success(financeOpsService.listInvoices());
    }

    @PostMapping("/housekeeping/tasks")
    @PreAuthorize("hasAuthority('ops:write')")
    public ApiResponse<Long> createHousekeepingTask(@Valid @RequestBody HousekeepingTaskCreateRequest request) {
        return ApiResponse.success(financeOpsService.createHousekeepingTask(request));
    }

    @GetMapping("/housekeeping/tasks")
    @PreAuthorize("hasAuthority('ops:read')")
    public ApiResponse<List<HousekeepingTask>> listHousekeepingTasks() {
        return ApiResponse.success(financeOpsService.listHousekeepingTasks());
    }

    @PatchMapping("/housekeeping/tasks/status")
    @PreAuthorize("hasAuthority('ops:write')")
    public ApiResponse<Void> updateHousekeepingStatus(@Valid @RequestBody BizStatusUpdateRequest request) {
        financeOpsService.updateHousekeepingTaskStatus(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/maintenance/tickets")
    @PreAuthorize("hasAuthority('ops:write')")
    public ApiResponse<Long> createMaintenanceTicket(@Valid @RequestBody MaintenanceTicketCreateRequest request) {
        return ApiResponse.success(financeOpsService.createMaintenanceTicket(request));
    }

    @GetMapping("/maintenance/tickets")
    @PreAuthorize("hasAuthority('ops:read')")
    public ApiResponse<List<MaintenanceTicket>> listMaintenanceTickets() {
        return ApiResponse.success(financeOpsService.listMaintenanceTickets());
    }

    @PatchMapping("/maintenance/tickets/status")
    @PreAuthorize("hasAuthority('ops:write')")
    public ApiResponse<Void> updateMaintenanceStatus(@Valid @RequestBody BizStatusUpdateRequest request) {
        financeOpsService.updateMaintenanceTicketStatus(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<List<DailyReportVO>> dailyReport() {
        return ApiResponse.success(financeOpsService.dailyReport());
    }

    @GetMapping("/reports/property-stats")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<List<PropertyStatVO>> propertyStats() {
        return ApiResponse.success(financeOpsService.propertyStats());
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('report:read')")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(financeOpsService.dashboard());
    }

    @GetMapping("/reports/daily/export")
    @PreAuthorize("hasAuthority('report:export')")
    public ResponseEntity<byte[]> exportDailyReport() {
        byte[] bytes = financeOpsService.exportDailyReportExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("daily-report.xlsx").build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @GetMapping("/reports/property-stats/export")
    @PreAuthorize("hasAuthority('report:export')")
    public ResponseEntity<byte[]> exportPropertyStats() {
        byte[] bytes = financeOpsService.exportPropertyStatExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("property-stats.xlsx").build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
