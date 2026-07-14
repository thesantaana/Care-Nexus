export const DEFAULT_AVATAR_URL = '/assets/default-avatar.png';

type UserProfileSource = {
  userId: number;
  displayName: string;
  avatarUrl?: string;
};

export type ResolvedUserProfile = {
  displayName: string;
  avatarDataUrl: string;
};

export function profileStorageKey(userId: number) {
  return `carenexus-portal-profile:${userId}`;
}

export function resolveUserProfile(user: UserProfileSource): ResolvedUserProfile {
  const fallback = {
    displayName: user.displayName,
    avatarDataUrl: user.avatarUrl || DEFAULT_AVATAR_URL,
  };

  try {
    const saved = JSON.parse(localStorage.getItem(profileStorageKey(user.userId)) || '{}') as Partial<ResolvedUserProfile>;
    return {
      displayName: saved.displayName?.trim() || fallback.displayName,
      avatarDataUrl: saved.avatarDataUrl || fallback.avatarDataUrl,
    };
  } catch {
    return fallback;
  }
}
