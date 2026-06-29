import { useMemo } from 'react';
import type {
  SubmissionEntry,
  TimePeriod,
  CustomRange,
  TraineeSubmissionsStats,
  VerdictsBreakdown,
} from '../types/dashboard.types';

const DAY_NAMES = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

function filterByPeriod(
  submissions: SubmissionEntry[],
  period: TimePeriod,
  customRange?: CustomRange,
): SubmissionEntry[] {
  const now = new Date();

  if (period === 'all') return submissions;

  if (period === 'custom' && customRange) {
    const from = new Date(customRange.from);
    from.setHours(0, 0, 0, 0);
    const to = new Date(customRange.to);
    to.setHours(23, 59, 59, 999);
    return submissions.filter((s) => {
      const d = new Date(s.date);
      return d >= from && d <= to;
    });
  }

  const cutoffMs: Record<Exclude<TimePeriod, 'all' | 'custom'>, number> = {
    '1d': 24 * 60 * 60 * 1000,
    '7d': 7 * 24 * 60 * 60 * 1000,
    '30d': 30 * 24 * 60 * 60 * 1000,
  };

  const periodMs = cutoffMs[period as '1d' | '7d' | '30d'];
  if (!periodMs) return submissions;

  const cutoff = new Date(now.getTime() - periodMs);
  return submissions.filter((s) => new Date(s.date) >= cutoff);
}

function computeLongestStreak(submissions: SubmissionEntry[]): number {
  if (submissions.length === 0) return 0;

  // Collect unique date strings (YYYY-MM-DD)
  const dateSet = new Set(
    submissions.map((s) => new Date(s.date).toISOString().slice(0, 10)),
  );
  const sortedDates = Array.from(dateSet).sort();

  let longest = 1;
  let current = 1;

  for (let i = 1; i < sortedDates.length; i++) {
    const prev = new Date(sortedDates[i - 1]);
    const curr = new Date(sortedDates[i]);
    const diffDays = (curr.getTime() - prev.getTime()) / (1000 * 60 * 60 * 24);
    if (diffDays === 1) {
      current++;
      longest = Math.max(longest, current);
    } else {
      current = 1;
    }
  }

  return longest;
}

function buildDailyActivity(
  submissions: SubmissionEntry[],
  period: TimePeriod,
  customRange?: CustomRange,
): { date: string; count: number }[] {
  // Determine the date range window to display
  const now = new Date();
  let days = 30;
  if (period === '1d') days = 1;
  else if (period === '7d') days = 7;
  else if (period === '30d') days = 30;
  else if (period === 'custom' && customRange) {
    days = Math.ceil(
      (customRange.to.getTime() - customRange.from.getTime()) / (1000 * 60 * 60 * 24),
    ) + 1;
  } else {
    // all — last 30 days for the chart
    days = 30;
  }

  const countMap: Record<string, number> = {};

  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now);
    d.setDate(d.getDate() - i);
    countMap[d.toISOString().slice(0, 10)] = 0;
  }

  submissions.forEach((s) => {
    const key = new Date(s.date).toISOString().slice(0, 10);
    if (key in countMap) countMap[key] = (countMap[key] || 0) + 1;
  });

  return Object.entries(countMap).map(([date, count]) => ({ date, count }));
}

export function useSubmissionsStats(
  submissions: SubmissionEntry[],
  period: TimePeriod,
  customRange?: CustomRange,
): { filtered: SubmissionEntry[]; stats: TraineeSubmissionsStats } {
  return useMemo(() => {
    const filtered = filterByPeriod(submissions, period, customRange);

    const verdictsBreakdown: VerdictsBreakdown = { AC: 0, WA: 0, TLE: 0, RE: 0, MLE: 0 };
    const languageCounts: Record<string, number> = {};
    const acSubmissions = filtered.filter((s) => s.verdict === 'AC');

    filtered.forEach((s) => {
      verdictsBreakdown[s.verdict]++;
      languageCounts[s.language] = (languageCounts[s.language] || 0) + 1;
    });

    const acceptedCount = verdictsBreakdown.AC;
    const totalSubmissions = filtered.length;
    const acceptanceRate =
      totalSubmissions > 0 ? Math.round((acceptedCount / totalSubmissions) * 100) : 0;

    // Problems solved = unique problems with at least one AC
    const solvedProblems = new Set(acSubmissions.map((s) => s.problemName));
    const problemsSolved = solvedProblems.size;

    // Runtime stats (AC only)
    const runtimes = acSubmissions.map((s) => s.runtimeMs);
    const avgRuntimeMs = runtimes.length > 0
      ? Math.round(runtimes.reduce((a, b) => a + b, 0) / runtimes.length)
      : 0;
    const bestRuntimeMs = runtimes.length > 0 ? Math.min(...runtimes) : 0;

    // Longest streak
    const longestStreak = computeLongestStreak(filtered);

    // Most active day of the week
    const dayCounts: Record<string, number> = {};
    filtered.forEach((s) => {
      const dayName = DAY_NAMES[new Date(s.date).getDay()];
      dayCounts[dayName] = (dayCounts[dayName] || 0) + 1;
    });
    const mostActiveDay = Object.entries(dayCounts).sort((a, b) => b[1] - a[1])[0]?.[0] ?? '—';

    // Daily activity for chart
    const dailyActivity = buildDailyActivity(filtered, period, customRange);

    const stats: TraineeSubmissionsStats = {
      totalSubmissions,
      acceptedCount,
      acceptanceRate,
      longestStreak,
      avgRuntimeMs,
      bestRuntimeMs,
      problemsSolved,
      mostActiveDay,
      verdictsBreakdown,
      languageCounts,
      dailyActivity,
    };

    return { filtered, stats };
  }, [submissions, period, customRange]);
}
