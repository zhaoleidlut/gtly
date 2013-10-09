package com.htong.ui;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.htong.alg.MyLvBo;
import com.htong.dao.SQLDataDao;
import com.htong.domain.SQLData;
import com.htong.domain.SQLWell;
import com.htong.gzzd.GTDataComputerProcess;

public class SGTViewWindow {

	protected Shell shell;
	private float zaihe[];
	private float weiyi[];
	private DateTime ls_dateTime1;
	private JFreeChart jfreechart;
	
	private static final Logger log = Logger.getLogger(SGTViewWindow.class);
	private Text text_chongcheng;
	private Text text_chongci;
	private Text text_maxzaihe;
	private Text text_minzaihe;
	private Text text_time;
	private Combo combo_1;
	private DateTime dateTime;
	private Text text;
	private Text cyl;
	private Text cyl_mianji;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SGTViewWindow window = new SGTViewWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(620, 461);
		shell.setText("示功图查询");
		
		Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 12, 36, 18);
		label.setText("井号：");
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("日期：");
		label_1.setBounds(10, 44, 42, 24);
		
		ls_dateTime1 = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN);
		ls_dateTime1.setBounds(52, 39, 88, 29);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("时：");
		label_2.setBounds(161, 44, 24, 17);
		
		String hourStr[] = new String[24];
		for (int i = 0; i < 24; i++) {
			hourStr[i] = String.valueOf(i) + "时";
		}
		int index1 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar startC = Calendar.getInstance();
				startC.set(ls_dateTime1.getYear(), ls_dateTime1.getMonth(),
						ls_dateTime1.getDay(), dateTime.getHours(), (dateTime.getMinutes()/10)*10, 0);
				Date start = startC.getTime();
				Calendar endC = Calendar.getInstance();
				endC.set(ls_dateTime1.getYear(), ls_dateTime1.getMonth(),
						ls_dateTime1.getDay(), dateTime.getHours(), (dateTime.getMinutes()/10)*10+9, 59);
				Date end = endC.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				log.debug(sdf.format(start));
				log.debug(sdf.format(end));
				
				
//				WellDataDao wellDAO = new WellDataDao();
//				WellData d = wellDAO.getWellDataByNum(combo_1.getText().trim());
				
				SQLWell sqlWell = SQLWell.getWellsMap().get(combo_1.getText().trim());
				
				List<SQLData> sqlDataList = new SQLDataDao().getDatas(combo_1.getText().trim(),
						sdf.format(start), sdf.format(end));
				
				if(sqlDataList == null || sqlDataList.isEmpty()) {
					return;
				}
				
				String[] weiyiStr = sqlDataList.get(0).getWeiyi().split(";");
				String[] zaiheStr = sqlDataList.get(0).getZaihe().split(";");
				
				weiyi = new float[weiyiStr.length];
				zaihe = new float[zaiheStr.length];
				for(int i = 0;i<weiyiStr.length;i++) {
					weiyi[i] = Float.valueOf(weiyiStr[i]);
					zaihe[i] = Float.valueOf(zaiheStr[i]);
				}
				
				
				
				// 加入滤波算法
				MyLvBo.myLvBo(weiyi, zaihe);

				GTDataComputerProcess sp = new GTDataComputerProcess();
				
				Map<String, Object> map = sp.calcSGTData(weiyi, zaihe, sqlDataList.get(0).getChongci(), 0, sqlWell.getBengjing(),
						sqlWell.getMidu(), sqlWell.getHanshui());
				
				Float chongcheng = (Float)map.get("chongcheng");
				
				Float maxZaihe = (Float)map.get("maxZaihe");
				Float minZaihe = (Float)map.get("minZaihe");
				
				Float youxiaochongcheng = (Float)map.get("youxiaochongcheng");
				
				log.debug("原始理论产液量：" + map.get("liquidProduct"));
				
//				zaihe = new float[]{1,2};
//				weiyi = new float[]{3,4};
				
				text_chongcheng.setText(String.valueOf(chongcheng));
				text_chongci.setText(String.valueOf(sqlDataList.get(0).getChongci()));
				text_maxzaihe.setText(String.valueOf(maxZaihe));
				text_minzaihe.setText(String.valueOf(minZaihe));
				text_time.setText(sdf.format(sqlDataList.get(0).getTime()));
				text.setText(String.valueOf(youxiaochongcheng));
				cyl.setText(String.valueOf((Float)map.get("liquidProduct")*24));
				cyl_mianji.setText(String.valueOf((Float)map.get("areaProduct")*24));
				
				((XYPlot) jfreechart.getPlot()).setDataset(createDataset());
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		button.setBounds(305, 40, 63, 25);
		
		XYDataset categorydataset = createDataset();
		jfreechart = createChart(categorydataset);
		
		ChartComposite chartComposite = new ChartComposite(shell, SWT.NONE, jfreechart);
		chartComposite.setBounds(10, 76, 387, 311);
		
		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(421, 119, 42, 25);
		label_3.setText("冲程：");
		
		text_chongcheng = new Text(shell, SWT.BORDER);
		text_chongcheng.setBounds(484, 116, 70, 22);
		
		Label label_4 = new Label(shell, SWT.NONE);
		label_4.setBounds(421, 150, 54, 31);
		label_4.setText("冲次：");
		
		text_chongci = new Text(shell, SWT.BORDER);
		text_chongci.setBounds(484, 147, 70, 22);
		
		Label label_5 = new Label(shell, SWT.NONE);
		label_5.setBounds(421, 187, 54, 26);
		label_5.setText("最大载荷：");
		
		text_maxzaihe = new Text(shell, SWT.BORDER);
		text_maxzaihe.setBounds(484, 184, 70, 22);
		
		Label label_6 = new Label(shell, SWT.NONE);
		label_6.setBounds(421, 219, 54, 18);
		label_6.setText("最小载荷：");
		
		text_minzaihe = new Text(shell, SWT.BORDER);
		text_minzaihe.setBounds(484, 219, 70, 22);
		
		Label label_7 = new Label(shell, SWT.NONE);
		label_7.setBounds(421, 254, 54, 22);
		label_7.setText("数据时间：");
		
		text_time = new Text(shell, SWT.BORDER);
		text_time.setBounds(421, 282, 163, 22);
		
		combo_1 = new Combo(shell, SWT.NONE);
		combo_1.setBounds(52, 9, 88, 25);
		
		dateTime = new DateTime(shell, SWT.BORDER | SWT.DROP_DOWN | SWT.TIME);
		dateTime.setBounds(191, 44, 88, 24);
		
		Label label_8 = new Label(shell, SWT.NONE);
		label_8.setBounds(440, 316, 61, 17);
		label_8.setText("有效冲程：");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(507, 313, 73, 23);
		
		Label label_9 = new Label(shell, SWT.NONE);
		label_9.setText("产液量：");
		label_9.setBounds(440, 355, 54, 17);
		
		cyl = new Text(shell, SWT.BORDER);
		cyl.setBounds(507, 355, 73, 23);
		
		Label label_10 = new Label(shell, SWT.NONE);
		label_10.setBounds(415, 387, 79, 17);
		label_10.setText("面积产液量：");
		
		cyl_mianji = new Text(shell, SWT.BORDER);
		cyl_mianji.setBounds(507, 384, 73, 23);
		
		List<SQLWell> wellList = SQLWell.getWells();
		if(wellList!=null && !wellList.isEmpty()) {
			String[] items = new String[wellList.size()];
			for(int i = 0;i<wellList.size();i++) {
				items[i] = wellList.get(i).getWellnum();
			}
			combo_1.setItems(items);
			combo_1.select(0);
		}

	}
	
	private XYDataset createDataset() {
		XYSeries xyseries = new XYSeries("(位移：载荷)", false);
		if (zaihe == null) {
			return null;
		}
		int j = 0;
		for (float f : zaihe) {
			xyseries.add(weiyi[j], zaihe[j]);
			j++;
		}
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		xyseriescollection.addSeries(xyseries);
		return xyseriescollection;
	}
	private  JFreeChart createChart(XYDataset categorydataset) {
		Font font = new Font("SansSerif", 12, 10);
		JFreeChart jfreechart = ChartFactory.createXYLineChart(" 地面示功图",
				"位移  米", "载荷 千牛", categorydataset, PlotOrientation.VERTICAL,
				false, true, false);
		TextTitle texttitle = jfreechart.getTitle();
		texttitle.setFont(font);

		// jfreechart.setBackgroundPaint(Color.CYAN); // 设置背景颜色
		XYPlot categoryplot = (XYPlot) jfreechart.getPlot();
		categoryplot.setBackgroundPaint(Color.white);
		categoryplot.setRangeGridlinePaint(Color.red);
		categoryplot.setRangeGridlinesVisible(true);
		categoryplot.getDomainAxis().setLabelFont(font);
		categoryplot.setDomainGridlinePaint(Color.RED);

		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();

		numberaxis.setLabelFont(font);

		XYLineAndShapeRenderer lineandshaperenderer = (XYLineAndShapeRenderer) categoryplot
				.getRenderer();
		lineandshaperenderer.setSeriesPaint(0, Color.BLUE);

		return jfreechart;
	}
}
