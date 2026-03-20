import type { TableProps } from 'antd'

export const PAGE_SIZE_OPTIONS = ['10', '20', '50', '100']

export const DEFAULT_TABLE_PAGINATION: TableProps<any>['pagination'] = {
  defaultPageSize: 10,
  showSizeChanger: true,
  pageSizeOptions: PAGE_SIZE_OPTIONS,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条`,
}
