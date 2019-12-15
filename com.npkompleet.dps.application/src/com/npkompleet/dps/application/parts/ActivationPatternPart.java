package com.npkompleet.dps.application.parts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtchart.Chart;
import org.eclipse.swtchart.ILineSeries;
import org.eclipse.swtchart.ISeries.SeriesType;

import com.npkompleet.dps.application.util.ChartDataSingleton;

public class ActivationPatternPart {
	Chart chart;
	ChartDataSingleton chartData = ChartDataSingleton.getInstance();
	long periodLCM;
	Color[] colorList = new Color[] { new Color(Display.getDefault(), 255, 0, 0), // Red
			new Color(Display.getDefault(), 255, 255, 0), // Yellow
			new Color(Display.getDefault(), 0, 0, 255), // Blue
			new Color(Display.getDefault(), 0, 255, 0) // Green
	};

	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		chart = new Chart(parent, SWT.NONE);
		chart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		// set titles
		chart.getTitle().setText("Activation Patterns");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Period");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Number");

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				createChart();
			}
		});
	}

	@Focus
	public void onFocus() {

	}

	private void createChart() {
		LinkedHashMap<String, BigInteger> dataMap = (LinkedHashMap<String, BigInteger>) chartData
				.getActivationPatternData();
		int[] holder = dataMap.values().stream().mapToInt(BigInteger::intValue).toArray();
		periodLCM = ChartDataSingleton.lcm_of_array_elements(holder);
		int index = 0;
		for (String task : dataMap.keySet()) {
			ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, task);
			int taskPeriod = dataMap.get(task).intValue();
			List<Double> xValues = new ArrayList<>();
			List<Double> yValues = new ArrayList<>();
			double x = 0;
			double y = 0;
			for (int i = 0; i < periodLCM / taskPeriod; i++) {
				xValues.add(x);
				yValues.add(y);
				xValues.add(x);
				y++;
				yValues.add(y);
				x += taskPeriod;
			}
			xValues.add(Double.valueOf(periodLCM));
			yValues.add(y);

			lineSeries.setXSeries(xValues.stream().mapToDouble(Double::doubleValue).toArray());
			lineSeries.setYSeries(yValues.stream().mapToDouble(Double::doubleValue).toArray());
			lineSeries.setLineColor(colorList[index % dataMap.size()]);
			index++;

		}
		chart.getAxisSet().adjustRange();
	}

}
