package com.htong.gzzd;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.SWT;

public class qq {

	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			qq window = new qq();
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
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		
		TableTreeViewer tableTreeViewer = new TableTreeViewer(shell, SWT.BORDER | SWT.FULL_SELECTION);
		TableTree tableTree = tableTreeViewer.getTableTree();
		tableTree.setBounds(30, 42, 251, 192);

	}
}
