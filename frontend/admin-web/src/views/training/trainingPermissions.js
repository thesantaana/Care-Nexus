const USER_STORAGE_KEY = 'care-nexus-user'

export const TRAINING_VIEW_PERMISSION = 'training:resource:view'
export const TRAINING_MANAGE_PERMISSION = 'training:resource:manage'

function normalizePermissionCodes(permissionCodes) {
  if (!Array.isArray(permissionCodes)) {
    return []
  }

  return [...new Set(permissionCodes.filter((code) => typeof code === 'string'))]
}

export function readStoredPermissionCodes() {
  const storedUser = localStorage.getItem(USER_STORAGE_KEY)
  if (!storedUser) {
    return []
  }

  try {
    return normalizePermissionCodes(JSON.parse(storedUser)?.permissionCodes)
  } catch {
    return []
  }
}

export function resolvePermissionCodes(permissionCodes) {
  return Array.isArray(permissionCodes)
    ? normalizePermissionCodes(permissionCodes)
    : readStoredPermissionCodes()
}

export function hasTrainingPermission(permissionCodes, permission) {
  return resolvePermissionCodes(permissionCodes).includes(permission)
}
