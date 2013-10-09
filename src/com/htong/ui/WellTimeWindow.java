package com.htong.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.htong.dao.SQLDataDao;
import com.htong.domain.SQLData;

public class WellTimeWindow {
	private List<SQLData> sqlDataList = new ArrayList<SQLData>();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			SQLData data = (SQLData)element;
			switch(columnIndex) {
			case 0:return data.getWellNum();
			case 1:return sdf.format(data.getTime());
			case 2:return String.valueOf(data.getChongci());
			}
			
			return element.toString();
		}
	}


	protected Shell shell;
	private Text text;
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			WellTimeWindow window = new WellTimeWindow();
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
		shell.setSize(379, 501);
		shell.setText("辅助查询");
		
		Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 10, 45, 17);
		label.setText("井号：");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(73, 4, 73, 23);
		
		final TableViewer tableViewer = new TableViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10, 33, 343, 420);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(64);
		tableColumn.setText("井号");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
		tblclmnNewColumn.setWidth(149);
		tblclmnNewColumn.setText("时间");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("冲次");
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(sqlDataList);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SQLDataDao dao = new SQLDataDao();
				
				sqlDataList.clear();
				sqlDataList.addAll(dao.getAllDatas(text.getText().trim()));
				tableViewer.refresh();
			}
		});
		button.setBounds(167, 4, 57, 27);
		button.setText("查询");

	}
}
