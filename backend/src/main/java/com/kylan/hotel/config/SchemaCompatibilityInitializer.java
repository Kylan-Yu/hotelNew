package com.kylan.hotel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaCompatibilityInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        ensureHotelOrderTable();
        ensureAuditLogColumns();
        ensureOperationLogColumns();
    }

    private void ensureHotelOrderTable() {
        String ddl = """
                CREATE TABLE IF NOT EXISTS hotel_order (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID / Order ID',
                  order_no VARCHAR(64) NOT NULL COMMENT '订单号 / Order No',
                  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
                  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
                  source_channel VARCHAR(32) NOT NULL DEFAULT 'DIRECT' COMMENT '来源渠道 / Source Channel',
                  guest_name VARCHAR(64) NOT NULL COMMENT '住客姓名 / Guest Name',
                  guest_mobile VARCHAR(32) NOT NULL COMMENT '住客手机号 / Guest Mobile',
                  check_in_date DATE NOT NULL COMMENT '入住日期 / Check-in Date',
                  check_out_date DATE NOT NULL COMMENT '离店日期 / Check-out Date',
                  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '订单总金额 / Total Amount',
                  order_status VARCHAR(32) NOT NULL COMMENT '订单状态 / Order Status',
                  channel_order_no VARCHAR(128) DEFAULT NULL COMMENT '渠道订单号 / Channel Order No',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_hotel_order_order_no (order_no),
                  KEY idx_hotel_order_property_id (property_id),
                  KEY idx_hotel_order_room_type_id (room_type_id),
                  KEY idx_hotel_order_status (order_status)
                ) COMMENT='订单主表 / Hotel Order Table'
                """;
        jdbcTemplate.execute(ddl);
        log.info("schema compatibility ensured: hotel_order");
    }

    private void ensureAuditLogColumns() {
        ensureColumn(
                "audit_log_record",
                "group_id",
                "BIGINT NULL COMMENT '集团ID / Group ID'"
        );
        ensureColumn(
                "audit_log_record",
                "brand_id",
                "BIGINT NULL COMMENT '品牌ID / Brand ID'"
        );
        ensureColumn(
                "audit_log_record",
                "property_id",
                "BIGINT NULL COMMENT '门店ID / Property ID'"
        );
    }

    private void ensureOperationLogColumns() {
        ensureColumn(
                "operation_log_record",
                "group_id",
                "BIGINT NULL COMMENT '集团ID / Group ID'"
        );
        ensureColumn(
                "operation_log_record",
                "brand_id",
                "BIGINT NULL COMMENT '品牌ID / Brand ID'"
        );
        ensureColumn(
                "operation_log_record",
                "property_id",
                "BIGINT NULL COMMENT '门店ID / Property ID'"
        );
    }

    private void ensureColumn(String tableName, String columnName, String columnDdl) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(1)
                        FROM information_schema.columns
                        WHERE table_schema = DATABASE()
                          AND table_name = ?
                          AND column_name = ?
                        """,
                Integer.class,
                tableName,
                columnName
        );
        if (count == null || count == 0) {
            String ddl = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDdl;
            jdbcTemplate.execute(ddl);
            log.info("schema compatibility added column: {}.{}", tableName, columnName);
        }
    }
}
