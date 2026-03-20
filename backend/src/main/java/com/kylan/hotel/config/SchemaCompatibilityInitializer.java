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
        ensureDictTablesAndSeed();
        ensureSystemParamTableAndSeed();
        ensureMemberProfileGenderColumn();
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

    private void ensureDictTablesAndSeed() {
        String dictDdl = """
                CREATE TABLE IF NOT EXISTS sys_dict (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID / Dictionary ID',
                  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
                  dict_name VARCHAR(128) NOT NULL COMMENT '字典名称 / Dictionary Name',
                  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
                  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_dict_code (dict_code),
                  KEY idx_sys_dict_status_sort (status, sort_no)
                ) COMMENT='系统字典表 / System Dictionary Table'
                """;
        jdbcTemplate.execute(dictDdl);

        String dictItemDdl = """
                CREATE TABLE IF NOT EXISTS sys_dict_item (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典项ID / Dictionary Item ID',
                  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
                  item_code VARCHAR(64) NOT NULL COMMENT '字典项编码 / Dictionary Item Code',
                  item_name VARCHAR(128) NOT NULL COMMENT '字典项名称 / Dictionary Item Name',
                  item_value VARCHAR(128) DEFAULT NULL COMMENT '字典项值 / Dictionary Item Value',
                  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
                  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_dict_item_code (dict_code, item_code),
                  KEY idx_sys_dict_item_dict_status_sort (dict_code, status, sort_no)
                ) COMMENT='系统字典项表 / System Dictionary Item Table'
                """;
        jdbcTemplate.execute(dictItemDdl);

        jdbcTemplate.execute("""
                INSERT IGNORE INTO sys_dict(dict_code, dict_name, status, sort_no, remark, created_by, updated_by, deleted)
                VALUES
                ('GENDER', '性别 / Gender', 1, 10, '入住人及会员性别字典 / guest and member gender dictionary', 'system', 'system', 0),
                ('BUSINESS_MODE', '经营模式 / Business Mode', 1, 20, '民宿与酒店经营模式 / homestay and hotel mode', 'system', 'system', 0),
                ('ROOM_STATUS', '房间状态 / Room Status', 1, 30, '房态字典 / room status dictionary', 'system', 'system', 0)
                """);

        jdbcTemplate.execute("""
                INSERT IGNORE INTO sys_dict_item(dict_code, item_code, item_name, item_value, status, sort_no, remark, created_by, updated_by, deleted)
                VALUES
                ('GENDER', 'MALE', '男 / Male', 'MALE', 1, 10, '男性 / Male', 'system', 'system', 0),
                ('GENDER', 'FEMALE', '女 / Female', 'FEMALE', 1, 20, '女性 / Female', 'system', 'system', 0),
                ('GENDER', 'UNKNOWN', '未知 / Unknown', 'UNKNOWN', 1, 30, '未知性别 / Unknown', 'system', 'system', 0),
                ('BUSINESS_MODE', 'HOMESTAY', '民宿 / Homestay', 'HOMESTAY', 1, 10, '民宿经营模式 / homestay mode', 'system', 'system', 0),
                ('BUSINESS_MODE', 'HOTEL', '酒店 / Hotel', 'HOTEL', 1, 20, '酒店经营模式 / hotel mode', 'system', 'system', 0),
                ('ROOM_STATUS', 'VACANT_CLEAN', '空净 / Vacant Clean', 'VACANT_CLEAN', 1, 10, '可直接入住 / ready to check in', 'system', 'system', 0),
                ('ROOM_STATUS', 'OCCUPIED', '在住 / Occupied', 'OCCUPIED', 1, 20, '住客在住 / currently occupied', 'system', 'system', 0),
                ('ROOM_STATUS', 'VACANT_DIRTY', '待清扫 / Vacant Dirty', 'VACANT_DIRTY', 1, 30, '待保洁 / waiting for housekeeping', 'system', 'system', 0),
                ('ROOM_STATUS', 'MAINTENANCE', '维修 / Maintenance', 'MAINTENANCE', 1, 40, '维修中 / under maintenance', 'system', 'system', 0),
                ('ROOM_STATUS', 'LOCKED', '锁房 / Locked', 'LOCKED', 1, 50, '不可售 / not for sale', 'system', 'system', 0)
                """);
    }

    private void ensureSystemParamTableAndSeed() {
        String ddl = """
                CREATE TABLE IF NOT EXISTS sys_param (
                  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID / Parameter ID',
                  param_key VARCHAR(128) NOT NULL COMMENT '参数键 / Parameter Key',
                  param_value VARCHAR(512) NOT NULL COMMENT '参数值 / Parameter Value',
                  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
                  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
                  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
                  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
                  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
                  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
                  UNIQUE KEY uk_sys_param_key (param_key)
                ) COMMENT='系统参数表 / System Parameter Table'
                """;
        jdbcTemplate.execute(ddl);

        jdbcTemplate.execute("""
                INSERT IGNORE INTO sys_param(param_key, param_value, remark, created_by, updated_by, deleted)
                VALUES
                ('hms.currentProperty.prefer', 'true', '日志筛选默认按当前门店收敛 / log filters prefer current property by default', 'system', 'system', 0),
                ('hms.ota.callback.maxRetry', '5', 'OTA回调最大重试次数 / max retry count for OTA callbacks', 'system', 'system', 0),
                ('hms.ota.callback.firstRetrySeconds', '30', 'OTA回调首次重试秒数 / first retry delay seconds for OTA callbacks', 'system', 'system', 0)
                """);
    }

    private void ensureMemberProfileGenderColumn() {
        ensureColumn(
                "member_profile",
                "gender",
                "VARCHAR(32) NULL COMMENT '性别编码 / Gender Code'"
        );
        jdbcTemplate.execute("""
                UPDATE member_profile
                SET gender = 'UNKNOWN'
                WHERE (gender IS NULL OR gender = '')
                  AND deleted = 0
                """);
    }
}
