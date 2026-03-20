import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import en from '../locales/en.json'
import zhCN from '../locales/zh-CN.json'

const LANG_KEY = 'hms_lang'
const supported = ['en', 'zh-CN']

const pickDefaultLanguage = () => {
  const stored = localStorage.getItem(LANG_KEY)
  if (stored && supported.includes(stored)) {
    return stored
  }
  return 'en'
}

i18n
  .use(initReactI18next)
  .init({
    resources: {
      en: { translation: en },
      'zh-CN': { translation: zhCN },
    },
    lng: pickDefaultLanguage(),
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
  })

i18n.on('languageChanged', (lng: string) => {
  localStorage.setItem(LANG_KEY, lng)
})

export const languageOptions = [
  { label: 'English', value: 'en' },
  { label: '中文', value: 'zh-CN' },
]

export default i18n
