import { Button, ButtonProps } from 'antd'
import { ReactNode } from 'react'
import { usePermission } from '../hooks/usePermission'

interface PermissionButtonProps extends ButtonProps {
  permission: string
  children: ReactNode
}

export function PermissionButton({ permission, children, ...rest }: PermissionButtonProps) {
  const { hasPermission } = usePermission()
  if (!hasPermission(permission)) {
    return null
  }
  return <Button {...rest}>{children}</Button>
}
