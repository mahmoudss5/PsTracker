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

import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
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

  const query = useQuery({
    queryKey: ['problems'],
    queryFn: getAllProblems,
  });
  const problems = useMemo(() => {
    const rawProblems = query.data ?? [];
    if (!tag) return rawProblems;
    const lower = tag.toLowerCase();
    return rawProblems.filter((p) =>
      p.tags.some((t) => t.toLowerCase().includes(lower)),
    );
  }, [query.data, tag]);

  return {
    problems,
    isLoading: query.isLoading,
    error: query.error instanceof Error ? query.error.message : null,
    refetch: () => { void query.refetch(); },
  };
}
