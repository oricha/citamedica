"use client";
import { useMemo } from 'react';

export interface AvailabilitySlot {
  start: string;
  end: string;
  available: boolean;
}

export interface AvailabilityCalendarProps {
  availability: AvailabilitySlot[];
  onSelect: (slot: { start: string; end: string }) => void;
}

// Minimal availability grid: groups slots by date and renders selectable times.
export default function AvailabilityCalendar({ availability, onSelect }: AvailabilityCalendarProps) {
  const grouped = useMemo(() => {
    const byDate: Record<string, AvailabilitySlot[]> = {};
    for (const slot of availability.filter((s) => s.available)) {
      const d = new Date(slot.start);
      const key = d.toISOString().slice(0, 10);
      if (!byDate[key]) byDate[key] = [];
      byDate[key].push(slot);
    }
    return byDate;
  }, [availability]);

  const dates = Object.keys(grouped).sort();

  if (!dates.length) {
    return <div className="text-gray-500">No hay disponibilidad visible en los próximos días.</div>;
  }

  return (
    <div className="space-y-6">
      {dates.map((date) => (
        <div key={date}>
          <h4 className="font-medium text-gray-900 mb-2">
            {new Date(date).toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
          </h4>
          <div className="flex flex-wrap gap-2">
            {grouped[date]
              .sort((a, b) => +new Date(a.start) - +new Date(b.start))
              .map((slot) => (
                <button
                  key={slot.start}
                  onClick={() => onSelect({ start: slot.start, end: slot.end })}
                  className="rounded border px-3 py-1 text-sm hover:bg-blue-50"
                >
                  {new Date(slot.start).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })}
                </button>
              ))}
          </div>
        </div>
      ))}
    </div>
  );
}

