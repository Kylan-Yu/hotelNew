import { useAppSelector } from '../store/hooks'

export function usePermission() {
  const permissions = useAppSelector((state) => state.auth.permissions || [])

  const hasPermission = (permission: string) => {
    if (permissions.includes('*')) {
      return true
    }
    return permissions.includes(permission)
  }

  return { hasPermission, permissions }
}
