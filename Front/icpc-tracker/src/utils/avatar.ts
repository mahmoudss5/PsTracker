import type { SyntheticEvent } from 'react';

export function getFallbackAvatar(seed?: string | null): string {
  const initials = (seed?.trim() || 'User')
    .split(/\s+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();
  const safeInitials = encodeURIComponent(initials || 'U');

  return `data:image/svg+xml,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128"><rect width="128" height="128" rx="24" fill="#243b63"/><text x="64" y="76" fill="#dbeafe" font-family="Arial,sans-serif" font-size="42" font-weight="700" text-anchor="middle">${safeInitials}</text></svg>`,
  )}`;
}

export function handleAvatarError(
  event: SyntheticEvent<HTMLImageElement>,
  fallbackUrl: string,
) {
  const image = event.currentTarget;

  if (image.src === fallbackUrl) {
    return;
  }

  image.onerror = null;
  image.src = fallbackUrl;
}
