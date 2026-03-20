import React from 'react'
import ReactDOM from 'react-dom/client'
import { Provider } from 'react-redux'
import { App as AntdApp, ConfigProvider } from 'antd'
import enUS from 'antd/locale/en_US'
import zhCN from 'antd/locale/zh_CN'
import { useTranslation } from 'react-i18next'
import { store } from './store'
import App from './App'
import './i18n'
import './styles/global.css'

function Root() {
  const { i18n } = useTranslation()
  const locale = i18n.language === 'zh-CN' ? zhCN : enUS

  return (
    <Provider store={store}>
      <ConfigProvider
        locale={locale}
        theme={{
          token: {
            colorPrimary: '#2563EB',
            borderRadius: 10,
            colorBgLayout: '#F8FAFC',
          },
          components: {
            Layout: {
              headerBg: 'transparent',
            },
            Menu: {
              darkItemBg: 'transparent',
              darkSubMenuItemBg: 'transparent',
            },
            Card: {
              colorBgContainer: '#FFFFFF',
            },
          },
        }}
      >
        <AntdApp>
          <App />
        </AntdApp>
      </ConfigProvider>
    </Provider>
  )
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Root />
  </React.StrictMode>,
)
