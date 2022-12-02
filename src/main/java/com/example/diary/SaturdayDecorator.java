package com.example.diary;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.DayOfWeek;

import java.util.Calendar;

public class SaturdayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int saturday = day.getDate().with(DayOfWeek.SATURDAY).getDayOfMonth();
        return (saturday == day.getDay());
//                && day.isBefore(CalendarDay.from(day.getYear(), day.getMonth(), 1)) &&
//                day.isAfter(CalendarDay.from(day.getYear(), day.getMonth(), day.getDate().lengthOfMonth()));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.BLUE));
    }
}