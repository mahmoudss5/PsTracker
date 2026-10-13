import { useState, useEffect } from 'react';

interface AvatarState {
  handle: string;
  url: string | null;
}

function getPhotoUrl(photo: unknown): string | null {
  if (typeof photo !== 'string' || photo.length === 0) {
    return null;
  }

  try {
    const url = new URL(photo, 'https://codeforces.com');

    if (url.hostname === 'userpic.codeforces.org') {
      return `/codeforces-image${url.pathname}${url.search}`;
    }

    return url.toString();
  } catch {
    return null;
  }
}

export function useCfAvatar(codeforcesHandle: string | undefined | null) {
  const [avatar, setAvatar] = useState<AvatarState>({ handle: '', url: null });
  const handle = codeforcesHandle?.trim() ?? '';

  useEffect(() => {
    if (!handle) {
      return;
    }

    const controller = new AbortController();
    const params = new URLSearchParams({ handles: handle });

    fetch(`/codeforces-api/user.info?${params.toString()}`, {
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`Codeforces API returned ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        const user = Array.isArray(data?.result) ? data.result[0] : null;
        const avatar =
          data?.status === 'OK' && user
            ? user.avatar ?? user.titlePhoto
            : null;

        setAvatar({
          handle,
          url: getPhotoUrl(avatar),
        });
      })
      .catch((error: unknown) => {
        if (error instanceof Error && error.name === 'AbortError') {
          return;
        }

        console.error('Could not load Codeforces avatar:', error);
        setAvatar({ handle, url: null });
      });

    return () => controller.abort();
  }, [handle]);

  return avatar.handle === handle ? avatar.url : null;
}
