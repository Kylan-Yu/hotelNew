import { App, Button, Card, Form, Input, Select, Space } from 'antd'
import { useTranslation } from 'react-i18next'
import { useNavigate } from 'react-router-dom'
import { login } from '../api/authApi'
import { setAuth } from '../features/auth/authSlice'
import { languageOptions } from '../i18n'
import { useAppDispatch } from '../store/hooks'

export function LoginPage() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const { message } = App.useApp()
  const { t, i18n } = useTranslation()

  const onFinish = async (values: { username: string; password: string }) => {
    try {
      const data = await login(values)
      if (!data?.accessToken) {
        message.error(t('login.noAccessToken'))
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
      message.success(t('login.loginSuccess'))
      navigate('/workbench/dashboard', { replace: true })
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || t('login.loginFailed')
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
      <Card
        title={t('login.title')}
        style={{ width: 420 }}
        extra={(
          <Space>
            <span>{t('common.language')}:</span>
            <Select
              style={{ width: 120 }}
              value={i18n.language === 'zh-CN' ? 'zh-CN' : 'en'}
              options={languageOptions}
              onChange={(lang) => i18n.changeLanguage(lang)}
            />
          </Space>
        )}
      >
        <Form layout="vertical" onFinish={onFinish} initialValues={{ username: 'admin', password: 'Admin@123' }}>
          <Form.Item label={t('login.username')} name="username" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label={t('login.password')} name="password" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>
            {t('login.signIn')}
          </Button>
        </Form>
      </Card>
    </div>
  )
}
