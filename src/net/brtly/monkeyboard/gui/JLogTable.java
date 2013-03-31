/*******************************************************************************
 * This file is part of MonkeyBoard
 * Copyright © 2013 Oliver Bartley
 * 
 * MonkeyBoard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MonkeyBoard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MonkeyBoard.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.brtly.monkeyboard.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * JTable subclass that simplifies writing log entries to a table
 * @author obartley
 *
 */
public class JLogTable extends JTable {

	public static interface LogTableStyleProvider {
		public AttributeSet getLogEventStyle(int priority);
	}

	public class DefaultLogTableStyleProvider implements LogTableStyleProvider {

		@Override
		public AttributeSet getLogEventStyle(int priority) {
			SimpleAttributeSet set = new SimpleAttributeSet();

			StyleConstants.setFontFamily(set, "Lucida Console");
			StyleConstants.setFontSize(set, 13);

			if (priority >= FATAL_INT) {
				StyleConstants.setBold(set, true);
				StyleConstants.setForeground(set, Color.red);
			} else if (priority >= ERROR_INT) {
				StyleConstants.setForeground(set, Color.red.darker());
			} else if (priority >= WARN_INT) {
				StyleConstants.setForeground(set, Color.red.darker().darker());
			} else if (priority >= INFO_INT) {
				StyleConstants.setForeground(set, Color.black);
			} else if (priority >= DEBUG_INT) {
				StyleConstants.setForeground(set, Color.darkGray);
			} else {
				StyleConstants.setBold(set, true);
				StyleConstants.setForeground(set, Color.gray);
			}
			return set;
		}

	}

	private class LogElement {
		private int _priority;
		private Object[] _values;

		public LogElement(int priority, Object... values) {
			_priority = priority;
			_values = values;
		}

		public int getPriority() {
			return _priority;
		}

		public Object[] getValues() {
			return _values;
		}

		public Object getValue(int index) {
			try {
				return _values[index];
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

	}

	private class LogTableAdapter extends AbstractTableModel implements
			TableCellRenderer {

		private List<LogElement> _model;
		private int _bufferSize = DEFAULT_BUFFER_SIZE;
		private String[] _columnNames;
		private DefaultLogTableStyleProvider _defaultStyleProvider;

		public LogTableAdapter(String... columnNames) {
			_model = new ArrayList<LogElement>();
			_columnNames = columnNames;
		}

		@Override
		public String getColumnName(int col) {
			if (col == 0) {
				return "Level";
			}
			try {
				return _columnNames[col - 1];
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		@Override
		public int getColumnCount() {
			return _columnNames.length + 1;
		}

		@Override
		public int getRowCount() {
			return _model.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			try {

				if (col == 0) {
					return getPriorityAsString(_model.get(row).getPriority());
				} else {
					return _model.get(row).getValue(col - 1);
				}
			} catch (NullPointerException e) {
				return null;
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}

		public String getPriorityAsString(int priority) {
			if (priority >= FATAL_INT) {
				return "FATAL";
			} else if (priority >= ERROR_INT) {
				return "ERROR";
			} else if (priority >= WARN_INT) {
				return "WARN";
			} else if (priority >= INFO_INT) {
				return "INFO";
			} else if (priority >= DEBUG_INT) {
				return "DEBUG";
			} else {
				return "TRACE";
			}
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel cell = new JLabel(String.valueOf(value));

			LogTableStyleProvider styleProvider = getStyleProvider();

			AttributeSet set;
			if (styleProvider == null) {
				if (_defaultStyleProvider == null) {
					_defaultStyleProvider = new DefaultLogTableStyleProvider();
				}
				set = _defaultStyleProvider.getLogEventStyle(_model.get(row)
						.getPriority());
			} else {
				set = styleProvider.getLogEventStyle(_model.get(row)
						.getPriority());
			}

			cell.setForeground(StyleConstants.getForeground(set));
			cell.setBackground(StyleConstants.getBackground(set));
			cell.setFont(new Font(StyleConstants.getFontFamily(set),
					Font.PLAIN, StyleConstants.getFontSize(set)));
			if (StyleConstants.isBold(set)) {
				Font f = cell.getFont();
				cell.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
			}
			return cell;
		}

		public int getBufferSize() {
			return _bufferSize;
		}

		public void setBufferSize(int bufferSize) {
			_bufferSize = bufferSize;
		}

		public void append(int priority, Object... values) {
			_model.add(new LogElement(priority, values));
			if (_model.size() > _bufferSize) {
				_model.remove(0);
			}
//			this.fireTableRowsUpdated(_model.size() - 1, _model.size());
			this.fireTableDataChanged();
			autoScroll();
		}
	}

	public static final int DEFAULT_BUFFER_SIZE = 10000;

	public static final int ALL_INT = -2147483648;
	public static final int TRACE_INT = 5000;
	public static final int DEBUG_INT = 10000;
	public static final int INFO_INT = 20000;
	public static final int WARN_INT = 30000;
	public static final int ERROR_INT = 40000;
	public static final int FATAL_INT = 50000;
	public static final int OFF_INT = 2147483647;

	private int _priority = ALL_INT;

	private LogTableStyleProvider _styleProvider;
	private LogTableAdapter _adapter;
	private boolean _autoScroll;

	public JLogTable(String... columns) {
		_adapter = new LogTableAdapter(columns);
		this.setModel(_adapter);
		this.setDefaultRenderer(Object.class, _adapter);
		_adapter.addTableModelListener(this);
		_autoScroll = true;
		
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		        autoScroll();
		    }
		});
	}

	public LogTableStyleProvider getStyleProvider() {
		return _styleProvider;
	}

	public void setStyleProvider(LogTableStyleProvider styleProvider) {
		_styleProvider = styleProvider;
	}
	
	public boolean getAutoScroll() {
		return _autoScroll;
	}
	
	public void setAutoScroll(boolean autoScroll) {
		_autoScroll = autoScroll;
		autoScroll();
	}
	
	public void autoScroll() {
		if (_autoScroll) {
			scrollToBottom();
		} else {
			System.out.println("derp");
		}
	}

	public void scrollToBottom() {
		scrollRectToVisible(getCellRect(getRowCount()-1, 0, true));
	}

	public void log(int priority, Object... eventData) {
		_adapter.append(priority, eventData);
	}

	public void trace(Object... eventData) {
		log(TRACE_INT, eventData);
	}

	public void debug(Object... eventData) {
		log(DEBUG_INT, eventData);
	}

	public void info(Object... eventData) {
		log(INFO_INT, eventData);
	}

	public void warn(Object... eventData) {
		log(WARN_INT, eventData);
	}

	public void error(Object... eventData) {
		log(ERROR_INT, eventData);
	}

	public void fatal(Object... eventData) {
		log(FATAL_INT, eventData);
	}

	public int getLevel() {
		return _priority;
	}

	public void setLevel(int level) {
		_priority = level;
	}
}
