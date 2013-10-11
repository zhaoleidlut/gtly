package com.htong.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.eclipse.swt.widgets.FileDialog;
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

import com.ht.scada.oildata.calc.GTDataComputer;
import com.ht.scada.oildata.calc.GTReturnKeyEnum;
import com.htong.alg.MyLvBo;
import com.htong.dao.SQLDataDao;
import com.htong.domain.SQLData;
import com.htong.domain.SQLWell;
import com.htong.gzzd.GTDataComputerProcess;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class GQSGTViewWindow {

	protected Shell shell;
	private float zaihe[];
	private float weiyi[];
	private JFreeChart jfreechart;
	
	private static final Logger log = Logger.getLogger(GQSGTViewWindow.class);
	private Text text_chongcheng;
	private Text text_chongci;
	private Text text_maxzaihe;
	private Text text_minzaihe;
	private Text text_time;
	private Combo combo_1;
	private Text text;
	private Text text_cyl;
	private Text text_1;
	private Text text_cc;
	private Text text_bj;
	private Text text_2;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GQSGTViewWindow window = new GQSGTViewWindow();
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
		label_1.setText("数据文件：");
		label_1.setBounds(10, 44, 63, 24);
		
		String hourStr[] = new String[24];
		for (int i = 0; i < 24; i++) {
			hourStr[i] = String.valueOf(i) + "时";
		}
		int index1 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				float chongCi = Float.parseFloat(text_cc.getText().trim());
				float bengJing = Float.parseFloat(text_bj.getText().trim());
				float midu = 1;
				float hanShui = 0.9f;
				
				
				
				String fileName = text_1.getText();
				File file = new File(fileName);
				if(!file.exists()) {
					return;
				}
				FileInputStream in = null;
				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				InputStreamReader reader = null;
				try {
					reader = new InputStreamReader(in, "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				BufferedReader bufferedReader = new BufferedReader(reader);
				String s = "";
				
				/***********************富林导出功图*****************************/
//				weiyi = new float[200];
//				zaihe = new float[200];
//				try {
//					int i = 1;
//					while((s=bufferedReader.readLine())!=null) {
//						String ss[] = s.split("  ");  //1:2.1m 3.2KN
//						for(String sss : ss) {
//							String ssss[] = sss.trim().split(":");
//							String sssss = ssss[1].trim();	//2.1m 3.2KN
//							String ssssss[]=sssss.split(" ");
//							String s1 = ssssss[0].replace("m", "");
//							String s2 = ssssss[1].replace("KN", "");
//							weiyi[i-1] = Float.parseFloat(s1);
//							zaihe[i-1] = Float.parseFloat(s2);
//							i++;
//						}
//					}
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
				
				/*************************高青公司功图文件*********************************/
				weiyi = new float[72];
				zaihe = new float[72];
				try {
					int i = 1;
					while((s=bufferedReader.readLine())!=null) {
						String ss[] = s.split("=");  //DATA=1.42,88.3;12,33;
						if(ss[0].equals("DATA")) {
							String sss[] = ss[1].split(";");
							for(String ssss : sss) {
								String sssss[] = ssss.trim().split(",");
								weiyi[i-1] = Float.parseFloat(sssss[0]);
								zaihe[i-1] = Float.parseFloat(sssss[1]);
								i++;
							}
							break;
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// 加入滤波算法
				MyLvBo.myLvBo(weiyi, zaihe);
				
				GTDataComputer dcp = new com.ht.scada.oildata.calc.GTDataComputerProcess();
				Map<GTReturnKeyEnum, Object> resultMap = dcp.calcSGTData(weiyi, zaihe, null, chongCi, bengJing, midu, hanShui);
				
				log.debug("新方法产液量" + resultMap.get(GTReturnKeyEnum.LIQUID_PRODUCT));
				
				
				Float chongcheng = (Float)resultMap.get(GTReturnKeyEnum.CHONG_CHENG);
				
				Float maxZaihe = (Float)resultMap.get(GTReturnKeyEnum.MAX_ZAIHE);
				Float minZaihe = (Float)resultMap.get(GTReturnKeyEnum.MIN_ZAIHE);
				
				Float youxiaochongcheng = (Float)resultMap.get(GTReturnKeyEnum.YOU_XIAO_CHONG_CHENG);
				Float cyl = (Float)resultMap.get(GTReturnKeyEnum.LIQUID_PRODUCT);
//				
//				log.debug("原始理论产液量：" + map.get("liquidProduct"));
//				
				
////				zaihe = new float[]{1,2};
////				weiyi = new float[]{3,4};
//				
				text_chongcheng.setText(String.valueOf(chongcheng));
				text_chongci.setText(text_cc.getText());
				text_maxzaihe.setText(String.valueOf(maxZaihe));
				text_minzaihe.setText(String.valueOf(minZaihe));
//				text_time.setText(sdf.format(sqlDataList.get(0).getTime()));
				text.setText(String.valueOf(youxiaochongcheng));
				text_cyl.setText(String.valueOf(cyl*24));
//				cyl_mianji.setText(String.valueOf((Float)map.get("areaProduct")*24));
//				
				((XYPlot) jfreechart.getPlot()).setDataset(createDataset());
			}
		});
		button.setText("计算");
		button.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		button.setBounds(521, 41, 63, 25);
		
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
		
		Label label_8 = new Label(shell, SWT.NONE);
		label_8.setBounds(421, 329, 61, 17);
		label_8.setText("有效冲程：");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(484, 326, 73, 23);
		
		Label label_9 = new Label(shell, SWT.NONE);
		label_9.setText("产液量：");
		label_9.setBounds(421, 358, 54, 17);
		
		text_cyl = new Text(shell, SWT.BORDER);
		text_cyl.setBounds(484, 355, 73, 23);
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(80, 44, 282, 23);
		
		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog fileDialog = new FileDialog(Display.getDefault()
						.getActiveShell().getShell(), SWT.OPEN);

				String fileName = fileDialog.open();
				if(fileName == null || fileName.equals("")) {
					return;
				} else {
					text_1.setText(fileName);
				}
			}
		});
		button_1.setBounds(368, 41, 42, 27);
		button_1.setText("选择");
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(191, 13, 36, 17);
		label_2.setText("冲次：");
		
		Label label_11 = new Label(shell, SWT.NONE);
		label_11.setText("泵径：");
		label_11.setBounds(343, 13, 36, 17);
		
		text_cc = new Text(shell, SWT.BORDER);
		text_cc.setText("2");
		text_cc.setBounds(230, 7, 73, 23);
		
		text_bj = new Text(shell, SWT.BORDER);
		text_bj.setText("56");
		text_bj.setBounds(379, 9, 73, 23);
		
		Label label_10 = new Label(shell, SWT.NONE);
		label_10.setText("井号：");
		label_10.setBounds(421, 81, 36, 18);
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(484, 80, 70, 22);
		
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
