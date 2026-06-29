/**
 * useSubmissions.ts
 *
 * Fetches submissions from the real API and converts the backend shape
 * (SubmissionResponseDto) into the SubmissionEntry type used by the UI
 * (SubmissionsPage, useSubmissionsStats, etc.).
 *
 * Usage:
 *   const { submissions, isLoading, error, refetch } = useSubmissions();
 *   // or for coach viewing a specific user:
 *   const { submissions, isLoading, error } = useSubmissions({ userId: 42 });
 */

import { useState, useEffect, useCallback } from 'react';
import type { SubmissionEntry, Verdict } from '../types/dashboard.types';
import type { SubmissionResponse } from '../types/api.types';
import {
  getMySubmissions,
  getSubmissionsByUserId,
} from '../services/submissionsService.tsx';

// ── Verdict normalisation ─────────────────────────────────────────────────────
// Codeforces returns verdicts like "OK", "WRONG_ANSWER", "TIME_LIMIT_EXCEEDED"
// etc.  The backend stores them as-is.  Map them to our 5-value union.

const VERDICT_MAP: Record<string, Verdict> = {
  // Codeforces raw values
  OK:                    'AC',
  WRONG_ANSWER:          'WA',
  TIME_LIMIT_EXCEEDED:   'TLE',
  RUNTIME_ERROR:         'RE',
  MEMORY_LIMIT_EXCEEDED: 'MLE',
  // Already-normalised values (in case backend normalises them)
  AC:  'AC',
  WA:  'WA',
  TLE: 'TLE',
  RE:  'RE',
  MLE: 'MLE',
};

function normaliseVerdict(raw: string): Verdict {
  return VERDICT_MAP[raw.toUpperCase()] ?? 'WA';
}

/** Convert a backend SubmissionResponseDto to the frontend SubmissionEntry */
function toSubmissionEntry(dto: SubmissionResponse): SubmissionEntry {
  return {
    id:           String(dto.id),
    problemName:  dto.problemName,
    verdict:      normaliseVerdict(dto.verdict),
    // Backend stores language in the Codeforces sync; it is not yet a field in
    // SubmissionResponseDto — use a sensible placeholder until the backend adds it.
    language:     'C++',
    runtimeMs:    dto.timeConsumedMs,
    // Backend stores memory in bytes; convert to KB for display consistency
    memoryKB:     Math.round(dto.memoryConsumedBytes / 1024),
    date:         dto.createdAt,
  };
}

// ── Hook ─────────────────────────────────────────────────────────────────────

interface UseSubmissionsOptions {
  /** If provided, fetches submissions for that specific user (coach view). */
  userId?: number;
}

interface UseSubmissionsResult {
  submissions: SubmissionEntry[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useSubmissions(
  options: UseSubmissionsOptions = {},
): UseSubmissionsResult {
  const { userId } = options;

  const [submissions, setSubmissions] = useState<SubmissionEntry[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const raw: SubmissionResponse[] = userId
        ? await getSubmissionsByUserId(userId)
        : await getMySubmissions();

      // Sort newest-first so the default table view is chronological
      const sorted = [...raw].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );

      setSubmissions(sorted.map(toSubmissionEntry));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load submissions');
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { submissions, isLoading, error, refetch: fetchData };
}
