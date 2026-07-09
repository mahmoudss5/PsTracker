import { useState, useEffect } from 'react';

export function useCfAvatar(codeforcesHandle: string | undefined | null) {
  const [cfAvatar, setCfAvatar] = useState<string | null>(null);

  useEffect(() => {
    if (!codeforcesHandle) {
      setCfAvatar(null);
      return;
    }
    
    fetch(`https://codeforces.com/api/user.info?handles=${codeforcesHandle}`)
      .then(res => res.json())
      .then(data => {
        if (data.status === 'OK' && data.result?.length > 0) {
          setCfAvatar(data.result[0].titlePhoto);
        }
      })
      .catch(console.error);
  }, [codeforcesHandle]);

  return cfAvatar;
}
