package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.gui.action.TableCopySqlPartCommandBase;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class TableCopyColumnHeaderCommand implements ICommand
{
   private JTable _table;

   public TableCopyColumnHeaderCommand(JTable table)
   {
      _table = table;
   }

   @Override
   public void execute()
   {
      int nbrSelCols = _table.getSelectedColumnCount();
      int[] selCols = _table.getSelectedColumns();
      if (0 == selCols.length )
      {
         return;
      }

      StringBuffer buf = new StringBuffer();
      for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
      {
         TableColumn col = _table.getColumnModel().getColumn(selCols[colIdx]);

         if(0 == buf.length())
         {
            buf.append(col.getHeaderValue());
         }
         else
         {
            buf.append("," + col.getHeaderValue());
         }
      }
      final StringSelection ss = new StringSelection(buf.toString());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }
}
