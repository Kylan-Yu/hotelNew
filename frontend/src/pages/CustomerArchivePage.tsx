import { App, Button, Card, Form, Input, Modal, Select, Space, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { createMemberPreference, fetchMemberPreferences, fetchMembers } from '../api/memberApi'
import { dictText } from '../constants/businessDict'
import { useDictOptions } from '../hooks/useDictOptions'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function CustomerArchivePage() {
  const [members, setMembers] = useState<any[]>([])
  const [preferences, setPreferences] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm<any>()
  const { message } = App.useApp()
  const { labelMap: genderLabelMap } = useDictOptions('GENDER')

  const loadData = async () => {
    const [memberList, preferenceList] = await Promise.all([fetchMembers(), fetchMemberPreferences()])
    setMembers(memberList || [])
    setPreferences(preferenceList || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredMembers = useMemo(() => {
    if (!keyword.trim()) return members
    const text = keyword.toLowerCase()
    return members.filter((item) => {
      return (
        String(item.memberNo || '').toLowerCase().includes(text) ||
        String(item.memberName || '').toLowerCase().includes(text) ||
        String(item.mobile || '').toLowerCase().includes(text)
      )
    })
  }, [members, keyword])

  return (
    <Card
      title="客户档案"
      extra={
        <Space>
          <Input.Search
            allowClear
            placeholder="按会员编号/姓名/手机号搜索"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            style={{ width: 280 }}
          />
          <Button type="primary" onClick={() => setOpen(true)}>
            新增常住客偏好
          </Button>
        </Space>
      }
    >
      <Table
        rowKey="id"
        dataSource={filteredMembers}
        style={{ marginBottom: 16 }}
        columns={[
          { title: '会员编号', dataIndex: 'memberNo', width: 160 },
          { title: '姓名', dataIndex: 'memberName', width: 120 },
          { title: '性别', dataIndex: 'gender', width: 120, render: (value: string) => genderLabelMap[value] || value || '-' },
          { title: '手机号', dataIndex: 'mobile', width: 140 },
          { title: '等级', dataIndex: 'levelCode', width: 90 },
          { title: '积分余额', dataIndex: 'pointBalance', width: 100 },
          { title: '状态', dataIndex: 'status', width: 120, render: (value: string) => dictText.memberStatus(value) },
          { title: '归属民宿', dataIndex: 'propertyName' },
        ]}
        pagination={DEFAULT_TABLE_PAGINATION}
      />

      <Table
        rowKey="id"
        dataSource={preferences}
        columns={[
          { title: '偏好ID', dataIndex: 'id', width: 100 },
          { title: '会员ID', dataIndex: 'memberId', width: 100 },
          { title: '偏好类型', dataIndex: 'preferenceType', width: 180 },
          { title: '偏好值', dataIndex: 'preferenceValue' },
        ]}
        pagination={DEFAULT_TABLE_PAGINATION}
      />

      <Modal
        title="新增常住客偏好"
        open={open}
        onOk={async () => {
          const values = await form.validateFields()
          await createMemberPreference(values)
          message.success('偏好创建成功')
          setOpen(false)
          form.resetFields()
          loadData()
        }}
        onCancel={() => setOpen(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="memberId" label="会员" rules={[{ required: true }]}>
            <Select options={members.map((item) => ({ label: `${item.memberName}(${item.mobile})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="preferenceType" label="偏好类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '房间朝向', value: 'ROOM_ORIENTATION' },
                { label: '床型偏好', value: 'BED_TYPE' },
                { label: '楼层偏好', value: 'FLOOR' },
                { label: '安静需求', value: 'QUIET_NEED' },
                { label: '其他', value: 'OTHER' },
              ]}
            />
          </Form.Item>
          <Form.Item name="preferenceValue" label="偏好内容" rules={[{ required: true }]}>
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}






