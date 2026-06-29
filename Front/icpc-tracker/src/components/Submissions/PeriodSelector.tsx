import { useState } from 'react';
import { Calendar, ChevronDown } from 'lucide-react';
import type { TimePeriod, CustomRange } from '../../types/dashboard.types';

interface PeriodSelectorProps {
  period: TimePeriod;
  customRange?: CustomRange;
  onChange: (period: TimePeriod, customRange?: CustomRange) => void;
}

const PRESETS: { id: TimePeriod; label: string }[] = [
  { id: '1d',  label: 'Last Day' },
  { id: '7d',  label: 'Last 7 Days' },
  { id: '30d', label: 'Last 30 Days' },
  { id: 'all', label: 'All Time' },
];

export function PeriodSelector({ period, customRange, onChange }: PeriodSelectorProps) {
  const [showPicker, setShowPicker] = useState(false);
  const [fromStr, setFromStr] = useState<string>(() => {
    const d = new Date();
    d.setDate(d.getDate() - 7);
    return d.toISOString().slice(0, 10);
  });
  const [toStr, setToStr] = useState<string>(() => new Date().toISOString().slice(0, 10));

  const applyCustom = () => {
    const from = new Date(fromStr);
    const to = new Date(toStr);
    if (from <= to) {
      onChange('custom', { from, to });
      setShowPicker(false);
    }
  };

  return (
    <div className="flex flex-wrap items-center gap-2">
      {/* Preset pills */}
      <div className="flex items-center rounded-full bg-dashboard-elevated border border-dashboard-border p-0.5 gap-0.5">
        {PRESETS.map((preset) => (
          <button
            key={preset.id}
            id={`period-${preset.id}`}
            onClick={() => { onChange(preset.id); setShowPicker(false); }}
            className={`px-3 py-1.5 rounded-full text-xs font-semibold transition-all duration-200 whitespace-nowrap ${
              period === preset.id
                ? 'bg-dashboard-primary text-dashboard-primary-contrast shadow'
                : 'text-dashboard-muted hover:text-dashboard-text'
            }`}
          >
            {preset.label}
          </button>
        ))}
      </div>

      {/* Custom range trigger */}
      <div className="relative">
        <button
          id="period-custom"
          onClick={() => setShowPicker((v) => !v)}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold border transition-all duration-200 ${
            period === 'custom'
              ? 'bg-dashboard-primary/10 border-dashboard-primary text-dashboard-primary'
              : 'border-dashboard-border text-dashboard-muted hover:text-dashboard-text hover:border-dashboard-primary/40 bg-dashboard-elevated'
          }`}
        >
          <Calendar size={12} />
          {period === 'custom' && customRange
            ? `${customRange.from.toLocaleDateString()} — ${customRange.to.toLocaleDateString()}`
            : 'Custom Range'}
          <ChevronDown size={12} className={`transition-transform ${showPicker ? 'rotate-180' : ''}`} />
        </button>

        {showPicker && (
          <div className="absolute top-full left-0 mt-2 z-30 glass-panel p-4 flex flex-col gap-3 min-w-[260px] shadow-xl">
            <p className="text-xs font-bold uppercase tracking-wider text-dashboard-muted">
              Select Date Range
            </p>
            <div className="flex flex-col gap-2">
              <label className="text-xs font-semibold text-dashboard-muted">From</label>
              <input
                type="date"
                value={fromStr}
                max={toStr}
                onChange={(e) => setFromStr(e.target.value)}
                className="w-full rounded border border-dashboard-border bg-dashboard-elevated px-3 py-1.5 text-sm text-dashboard-text focus:outline-none focus:border-dashboard-primary/60"
              />
            </div>
            <div className="flex flex-col gap-2">
              <label className="text-xs font-semibold text-dashboard-muted">To</label>
              <input
                type="date"
                value={toStr}
                min={fromStr}
                max={new Date().toISOString().slice(0, 10)}
                onChange={(e) => setToStr(e.target.value)}
                className="w-full rounded border border-dashboard-border bg-dashboard-elevated px-3 py-1.5 text-sm text-dashboard-text focus:outline-none focus:border-dashboard-primary/60"
              />
            </div>
            <button
              onClick={applyCustom}
              className="primary-action w-full justify-center py-2 text-xs"
            >
              Apply Range
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
