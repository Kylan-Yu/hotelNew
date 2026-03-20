import { useEffect, useMemo, useState } from 'react'
import { fetchPublicDictItems } from '../api/systemApi'

export type DictOption = {
  label: string
  value: string
  sortNo?: number
}

export function useDictOptions(dictCode: string) {
  const [options, setOptions] = useState<DictOption[]>([])
  const [reloadSeq, setReloadSeq] = useState(0)

  useEffect(() => {
    const onDictUpdated = () => {
      setReloadSeq((prev) => prev + 1)
    }
    window.addEventListener('hms:dict-updated', onDictUpdated)
    return () => {
      window.removeEventListener('hms:dict-updated', onDictUpdated)
    }
  }, [])

  useEffect(() => {
    let canceled = false
    const load = async () => {
      try {
        const list = await fetchPublicDictItems(dictCode)
        if (canceled) {
          return
        }
        const normalized = (list || []).map((item: any) => ({
          label: item.itemName,
          value: item.itemValue || item.itemCode,
          sortNo: item.sortNo,
        }))
        setOptions(normalized)
      } catch {
        if (!canceled) {
          setOptions([])
        }
      }
    }
    load()
    return () => {
      canceled = true
    }
  }, [dictCode, reloadSeq])

  const labelMap = useMemo(() => {
    const map: Record<string, string> = {}
    options.forEach((item) => {
      map[item.value] = item.label
    })
    return map
  }, [options])

  return { options, labelMap }
}
