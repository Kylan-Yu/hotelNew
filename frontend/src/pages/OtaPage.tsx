import { App, Form, Input, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import { PermissionButton } from '../components/PermissionButton'
import {
  createOtaMapping,
  fetchOtaMappings,
  fetchOtaSyncLogs,
  otaPullOrders,
  otaPushInventory,
  otaPushPrice,
} from '../api/otaApi'
import { fetchProperties } from '../api/propertyApi'

interface OtaPageProps {
  view: 'config' | 'mapping'
}

const channelOptions = [
  { label: '抖音', value: 'DOUYIN' },
  { label: '美团', value: 'MEITUAN' },
  { label: '携程', value: 'CTRIP' },
]

export function OtaPage({ view }: OtaPageProps) {
  const [mappings, setMappings] = useState<any[]>([])
  const [syncLogs, setSyncLogs] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm<any>()

  const [channelCode, setChannelCode] = useState('DOUYIN')
  const [propertyId, setPropertyId] = useState<number | undefined>()

  const { message } = App.useApp()

  const loadData = async () => {
    const [mappingList, logs, propertyList] = await Promise.all([fetchOtaMappings(), fetchOtaSyncLogs(), fetchProperties()])
    setMappings(mappingList || [])
    setSyncLogs(logs || [])
    setProperties(propertyList || [])
    if (!propertyId && propertyList && propertyList.length > 0) {
      setPropertyId(propertyList[0].id)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  const propertyNameById = useMemo(() => {
    const map: Record<number, string> = {}
    ;(properties || []).forEach((item) => {
      map[item.id] = item.propertyName
    })
    return map
  }, [properties])

  const currentMappingCount = useMemo(() => {
    return mappings.filter((item) => item.channelCode === channelCode).length
  }, [mappings, channelCode])

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {view === 'config' ? '渠道配置' : '渠道映射'}
      </Typography.Title>

      {view === 'config' && (
        <>
          <Space style={{ marginBottom: 16 }} wrap>
            <Select style={{ width: 180 }} value={channelCode} options={channelOptions} onChange={setChannelCode} />
            <Select
              style={{ width: 240 }}
              placeholder="选择民宿"
              value={propertyId}
              options={properties.map((item) => ({ label: item.propertyName, value: item.id }))}
              onChange={setPropertyId}
            />
            <PermissionButton
              permission="ota:write"
              onClick={async () => {
                await otaPushInventory({ channelCode, propertyId })
                message.success('库存同步任务已触发')
                loadData()
              }}
            >
              推送库存
            </PermissionButton>
            <PermissionButton
              permission="ota:write"
              onClick={async () => {
                await otaPushPrice({ channelCode, propertyId })
                message.success('价格同步任务已触发')
                loadData()
              }}
            >
              推送价格
            </PermissionButton>
            <PermissionButton
              permission="ota:write"
              onClick={async () => {
                await otaPullOrders({ channelCode, propertyId })
                message.success('拉取订单任务已触发')
                loadData()
              }}
            >
              拉取订单
            </PermissionButton>
          </Space>

          <Table
            rowKey="id"
            dataSource={syncLogs}
            columns={[
              { title: '渠道', dataIndex: 'channelCode', width: 120, render: (value: string) => dictText.channelCode(value) },
              { title: '业务类型', dataIndex: 'bizType', width: 120 },
              { title: '业务标识', dataIndex: 'bizId', width: 180 },
              { title: '状态', dataIndex: 'syncStatus', width: 120, render: (value: string) => dictText.syncStatus(value) },
              { title: '时间', dataIndex: 'createdAt' },
            ]}
            pagination={{ pageSize: 8 }}
          />
        </>
      )}

      {view === 'mapping' && (
        <>
          <Space style={{ marginBottom: 16 }}>
            <Typography.Text>当前渠道映射数量：{currentMappingCount}</Typography.Text>
            <PermissionButton permission="ota:write" type="primary" onClick={() => setOpen(true)}>
              新增映射
            </PermissionButton>
          </Space>

          <Table
            rowKey="id"
            dataSource={mappings}
            columns={[
              { title: '民宿', dataIndex: 'propertyId', width: 160, render: (value: number) => propertyNameById[value] || `民宿${value}` },
              { title: '渠道', dataIndex: 'channelCode', width: 120, render: (value: string) => dictText.channelCode(value) },
              {
                title: '映射类型',
                dataIndex: 'mappingType',
                width: 140,
                render: (value: string) => {
                  if (value === 'PROPERTY') return '门店映射'
                  if (value === 'ROOM_TYPE') return '房型映射'
                  if (value === 'GOODS') return '商品映射'
                  return value
                },
              },
              { title: '本地业务ID', dataIndex: 'localBizId', width: 140 },
              { title: '渠道业务ID', dataIndex: 'channelBizId', width: 180 },
              { title: '备注', dataIndex: 'remark' },
            ]}
          />
        </>
      )}

      <Modal
        title="新增渠道映射"
        open={open}
        onOk={async () => {
          const values = await form.validateFields()
          await createOtaMapping(values)
          message.success('映射创建成功')
          setOpen(false)
          form.resetFields()
          loadData()
        }}
        onCancel={() => setOpen(false)}
      >
        <Form form={form} layout="vertical" initialValues={{ channelCode: 'DOUYIN', mappingType: 'ROOM_TYPE' }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="channelCode" label="渠道" rules={[{ required: true }]}>
            <Select options={channelOptions} />
          </Form.Item>
          <Form.Item name="mappingType" label="映射类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '门店映射', value: 'PROPERTY' },
                { label: '房型映射', value: 'ROOM_TYPE' },
                { label: '商品映射', value: 'GOODS' },
              ]}
            />
          </Form.Item>
          <Form.Item name="localBizId" label="本地业务ID" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="channelBizId" label="渠道业务ID" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
