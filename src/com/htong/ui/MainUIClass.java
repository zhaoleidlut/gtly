package com.htong.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

import com.htong.dao.SQLWellDao;
import com.htong.domain.CalcResult;
import com.htong.domain.SQLWell;
import com.htong.service.GTLYService;
import org.eclipse.wb.swt.ResourceManager;

public class MainUIClass extends ApplicationWindow {
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			CalcResult calc = (CalcResult)element;
			switch(columnIndex) {
			case 0 : return calc.getWellNum();
			case 1 : return calc.getTime();
			case 2 : return calc.getValue();
			case 3 : return calc.getValueDun();
			case 4 : return calc.getValueOld();
			case 5 : return calc.getAreaValue();
			case 6 : return calc.getTimeLong();
			case 7 : return calc.getNum();
					
			default : return null;
			}
		}
	}
	
	private Table table;
	private Combo combo_wells;
	private DateTime dateTime;
	
	private TableViewer tableViewer;
	
	private List<CalcResult> calcResultList = new ArrayList<CalcResult>();

	/**
	 * Create the application window.
	 */
	public MainUIClass() {
		super(null);
		setBlockOnOpen(true);
		createActions();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		Label label = new Label(container, SWT.NONE);
		label.setBounds(10, 17, 36, 20);
		label.setText("井号：");
		
		combo_wells = new Combo(container, SWT.NONE);
		combo_wells.setBounds(49, 15, 87, 20);
		
		dateTime = new DateTime(container, SWT.BORDER | SWT.DROP_DOWN);
		dateTime.setBounds(205, 15, 92, 21);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, dateTime.getYear());
				c.set(Calendar.MONTH, dateTime.getMonth());
				c.set(Calendar.DATE, dateTime.getDay());
				if(combo_wells.getText().trim().startsWith("所有")) {
					calcResultList.clear();
					for(SQLWell well : SQLWell.getWells()) {
						CalcResult calc = new GTLYService().getDayResult(well.getWellnum(), c.getTime());
						calcResultList.add(calc);
					}
				} else {
					CalcResult calc = new GTLYService().getDayResult(combo_wells.getText().trim(), c.getTime());
					calcResultList.clear();
					calcResultList.add(calc);
				}
				
				//tableViewer.setInput(calcResultList);
				tableViewer.refresh();
			}
		});
		btnNewButton.setBounds(322, 8, 84, 36);
		btnNewButton.setText("日产液量查询");
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("日期：");
		label_1.setBounds(168, 17, 36, 20);
		
		Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, dateTime.getYear());
				c.set(Calendar.MONTH, dateTime.getMonth());
				c.set(Calendar.DATE, dateTime.getDay());
				if(combo_wells.getText().trim().startsWith("所有")) {
					calcResultList.clear();
					for(SQLWell well : SQLWell.getWells()) {
						List<CalcResult> calcList = new GTLYService().getMonthResult(well.getWellnum(), c.getTime());
						calcResultList.addAll(calcList);
					}
				} else {
					List<CalcResult> calcList = new GTLYService().getMonthResult(combo_wells.getText().trim(), c.getTime());
					calcResultList.clear();
					calcResultList.addAll(calcList);
				}
				
				tableViewer.refresh();
			}
		});
		button.setText("月产液量查询");
		button.setBounds(423, 8, 84, 36);
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 50, 818, 500);
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_4 = tableViewerColumn_4.getColumn();
		tableColumn_4.setWidth(55);
		tableColumn_4.setText("井号");
		
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(101);
		tableColumn.setText("时间");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnm = tableViewerColumn_1.getColumn();
		tblclmnm.setWidth(101);
		tblclmnm.setText("产液量（m³）");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.setWidth(89);
		tableColumn_2.setText("产液量（吨）");
		
		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_5.getColumn();
		tableColumn_1.setWidth(121);
		tableColumn_1.setText("未修正产液量（吨）");
		
		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_6 = tableViewerColumn_7.getColumn();
		tableColumn_6.setWidth(108);
		tableColumn_6.setText("面积产液量（吨）");
		
		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_5 = tableViewerColumn_6.getColumn();
		tableColumn_5.setWidth(161);
		tableColumn_5.setText("时间段");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_3.getColumn();
		tableColumn_3.setWidth(64);
		tableColumn_3.setText("功图个数");
		
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(calcResultList);
		
		
		Button btnexcel = new Button(container, SWT.NONE);
		btnexcel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				String filePath = System.getProperty("user.dir");
//				InputStreamReader fromFile = null;
//				try {
//					fromFile = new InputStreamReader(new FileInputStream(
//							filePath), "GBK");
//				} catch (UnsupportedEncodingException e1) {
//					e1.printStackTrace();
//				} catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				}
//				BufferedReader fromFileBuffer = new BufferedReader(fromFile);
				
				final FileDialog fdlg = new FileDialog(getShell(),SWT.SAVE);
				fdlg.setFilterExtensions(new String[]{"*.xls"});
				fdlg.setFileName("丹东华通产液量.xls");
				fdlg.setText("保存");
				fdlg.setOverwrite(true);
				final String path = fdlg.open();
				
				if(path == null) {
		    		return;
		    	}

				Workbook wb = new HSSFWorkbook();
			    Sheet sheet = wb.createSheet("产液量");

			    // head
			    Row rowHead = sheet.createRow(0);
			    CellStyle style = wb.createCellStyle();
			    style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			    //style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			    
			    Font font = wb.createFont();
			    font.setFontHeightInPoints((short)12);
			    font.setBoldweight((short) 20);
			    style.setFont(font);

			    
			    Cell cell = rowHead.createCell(0);
			    cell.setCellValue("井号");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(1);
			    cell.setCellValue("时间");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(2);
			    cell.setCellValue("产液量(m³)");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(3);
			    cell.setCellValue("产液量(吨)");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(4);
			    cell.setCellValue("未修正产液量(吨)");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(5);
			    cell.setCellValue("时间段");
			    cell.setCellStyle(style);
			    
			    cell = rowHead.createCell(6);
			    cell.setCellValue("功图个数");
			    cell.setCellStyle(style);
			    
			    
			    for(int i = 0;i<calcResultList.size();i++) {
			    	Row row = sheet.createRow(i+1);
			    	
			    	row.createCell(0).setCellValue(calcResultList.get(i).getWellNum());
				    row.createCell(1).setCellValue(calcResultList.get(i).getTime());	
				    row.createCell(2).setCellValue(calcResultList.get(i).getValue());
				    row.createCell(3).setCellValue(calcResultList.get(i).getValueDun());
				    row.createCell(4).setCellValue(calcResultList.get(i).getValueOld());
				    row.createCell(5).setCellValue(calcResultList.get(i).getTimeLong());
				    row.createCell(6).setCellValue(calcResultList.get(i).getNum());
			    }
			    // Write the output to a file
			    try {
					FileOutputStream fileOut = new FileOutputStream(path);
					wb.write(fileOut);
					fileOut.close();
					
					MessageDialog.openInformation(getShell(), "提示", "数据成功导出到 "+path);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		});
		btnexcel.setBounds(596, 15, 87, 29);
		btnexcel.setText("数据导出Excel");
		
		Button button_1 = new Button(container, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new SGTViewWindow().open();
			}
		});
		button_1.setText("示功图查询");
		button_1.setBounds(513, 15, 77, 29);
		
		Button button_2 = new Button(container, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new WellTimeWindow().open();
			}
		});
		button_2.setBounds(689, 17, 36, 27);
		button_2.setText("辅助");
		
		initControlsValue();

		return container;
	}
	
	private void initControlsValue() {
		List<SQLWell> wellList = SQLWell.getWells();
		if(wellList!=null && !wellList.isEmpty()) {
			String[] items = new String[wellList.size()+1];
			for(int i = 0;i<wellList.size();i++) {
				items[i] = wellList.get(i).getWellnum();
			}
			items[wellList.size()] = "所有井";
			combo_wells.setItems(items);
			combo_wells.select(0);
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}



	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			MainUIClass window = new MainUIClass();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(ResourceManager.getPluginImage("gtly", "icons/huatong.ico"));
		super.configureShell(newShell);
		newShell.setText("丹东华通功图量油测试软件");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(854, 610);
	}
}
