import { buildApiUrl, normalizeApiBaseUrl } from './http'

describe('http helpers', () => {
  it('uses default api base url when empty', () => {
    expect(normalizeApiBaseUrl(undefined)).toBe('http://localhost:8080/api')
    expect(normalizeApiBaseUrl('')).toBe('http://localhost:8080/api')
    expect(normalizeApiBaseUrl('   ')).toBe('http://localhost:8080/api')
  })

  it('trims tailing slash from api base url', () => {
    expect(normalizeApiBaseUrl('http://localhost:9000/api/')).toBe('http://localhost:9000/api')
    expect(normalizeApiBaseUrl('/api///')).toBe('/api')
  })

  it('builds full api path with slash normalization', () => {
    expect(buildApiUrl('/orders/export')).toContain('/orders/export')
    expect(buildApiUrl('orders/export')).toContain('/orders/export')
  })
})
