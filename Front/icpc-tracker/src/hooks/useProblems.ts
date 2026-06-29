/**
 * useProblems.ts
 *
 * Fetches the problem catalogue from GET /api/problems.
 * Supports optional filtering by tag name client-side.
 *
 * Usage:
 *   const { problems, isLoading, error } = useProblems();
 *   const { problems } = useProblems({ tag: 'dp' });
 */

import { useState, useEffect, useCallback, useMemo } from 'react';
import type { ProblemResponse } from '../types/api.types';
import { getAllProblems } from '../services/problemService';

interface UseProblemsOptions {
  /** Filter problems that include this tag (case-insensitive substring match). */
  tag?: string;
}

interface UseProblemsResult {
  problems: ProblemResponse[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useProblems(options: UseProblemsOptions = {}): UseProblemsResult {
  const { tag } = options;

  const [rawProblems, setRawProblems] = useState<ProblemResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getAllProblems();
      setRawProblems(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load problems');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const problems = useMemo(() => {
    if (!tag) return rawProblems;
    const lower = tag.toLowerCase();
    return rawProblems.filter((p) =>
      p.tags.some((t) => t.toLowerCase().includes(lower)),
    );
  }, [rawProblems, tag]);

  return { problems, isLoading, error, refetch: fetchData };
}
