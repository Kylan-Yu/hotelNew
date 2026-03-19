import { App, Button, Card, Form, Input } from 'antd'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/authApi'
import { setAuth } from '../features/auth/authSlice'
import { useAppDispatch } from '../store/hooks'

export function LoginPage() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const { message } = App.useApp()

  const onFinish = async (values: { username: string; password: string }) => {
    try {
      const data = await login(values)
      if (!data?.accessToken) {
        message.error('登录失败：未获取到 accessToken')
        return
      }
      dispatch(
        setAuth({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
          username: data.username,
          nickname: data.nickname || data.username,
          permissions: data.permissions || [],
          propertyScopes: data.propertyScopes || [],
          currentPropertyId: data.currentPropertyId,
        }),
      )
      message.success('登录成功')
      navigate('/workbench/dashboard', { replace: true })
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || '登录失败'
      message.error(msg)
    }
  }

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 100%)',
      }}
    >
      <Card title="民宿多店管理系统登录 | Homestay Multi-Store Login" style={{ width: 420 }}>
        <Form layout="vertical" onFinish={onFinish} initialValues={{ username: 'admin', password: 'Admin@123' }}>
          <Form.Item label="用户名 / Username" name="username" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="密码 / Password" name="password" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>
            登录 / Sign In
          </Button>
        </Form>
      </Card>
    </div>
  )
}
