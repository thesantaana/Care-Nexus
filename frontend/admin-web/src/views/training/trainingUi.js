export const RESOURCE_TYPES = [
  { value: 'ARTICLE', label: '培训文章' },
  { value: 'VIDEO', label: '培训视频' },
  { value: 'PPT', label: '培训课件' }
]

export const STORAGE_MODES = [
  { value: 'TEXT', label: '文本内容' },
  { value: 'EXTERNAL_LINK', label: '外部链接' }
]

export const RESOURCE_STATUSES = [
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'OFFLINE', label: '已下架' }
]

export const CATALOG_STATUSES = [
  { value: 'ENABLED', label: '已启用' },
  { value: 'DISABLED', label: '已停用' }
]

export function optionLabel(options, value, fallback = '未知') {
  return options.find((option) => option.value === value)?.label || fallback
}

export function resourceTypeLabel(value) {
  return optionLabel(RESOURCE_TYPES, value)
}

export function storageModeLabel(value) {
  if (value === 'LOCAL_FILE') {
    return '本地文件'
  }
  return optionLabel(STORAGE_MODES, value)
}

export function resourceStatusLabel(value) {
  return optionLabel(RESOURCE_STATUSES, value)
}

export function catalogStatusLabel(value) {
  return optionLabel(CATALOG_STATUSES, value)
}

export function formatDateTime(value) {
  if (!value) {
    return '尚未发布'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }

  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(date)
}

export function errorMessage(error, fallback = '操作失败，请稍后重试') {
  return error?.message || fallback
}
