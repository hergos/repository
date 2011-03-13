package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.*;
import java.awt.*;

public class GraphPanelController
{
   private GraphDesktopController _graphDesktopController;
   private JPanel _graphPanel;
   private ModeManager _modeManager;
   private JSplitPane _split;
   private JPanel _bottomPanelContainer;
   private int _standardDividerSize;

   public GraphPanelController(TableFramesModel tableFramesModel, GraphDesktopListener graphDesktopListener, ISession session, GraphPlugin plugin)
   {
      GraphDockHandleListener graphDockHandleListener = new GraphDockHandleListener()
      {
         @Override
         public void show(JPanel panel, int lastHeight)
         {
            onShow(panel, lastHeight);
         }

         @Override
         public void hide()
         {
            onHide();
         }
      };

      _modeManager = new ModeManager(tableFramesModel, session, plugin, new GraphDockHandleFactory(graphDockHandleListener));
      _graphDesktopController = new GraphDesktopController(graphDesktopListener, session, plugin, _modeManager);

      JScrollPane scrollPane = new JScrollPane(_graphDesktopController.getDesktopPane());

      _split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      _split.setTopComponent(scrollPane);
      _standardDividerSize = _split.getDividerSize();
      onHide();


      _graphPanel = new JPanel(new BorderLayout());
      _graphPanel.add(_split, BorderLayout.CENTER);
      _bottomPanelContainer = new JPanel(new GridLayout(1,1));
      _graphPanel.add(_bottomPanelContainer, BorderLayout.SOUTH);
   }

   private void onHide()
   {
      _split.setDividerSize(0);
      _split.setDividerLocation(Integer.MAX_VALUE);
   }

   private void onShow(JPanel panel, int lastHeight)
   {
      _split.setBottomComponent(panel);
      _split.setDividerLocation(_split.getHeight() - lastHeight - _standardDividerSize - 1);
      _split.setDividerSize(_standardDividerSize);
   }

   public GraphDesktopController getDesktopController()
   {
      return _graphDesktopController;
   }

   public void repaint()
   {
      _graphPanel.repaint();
      _graphDesktopController.repaint();
   }

   public JPanel getGraphPanel()
   {
      return _graphPanel;
   }

   public void initMode(Mode mode, ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean)
   {
      _modeManager.initMode(mode, zoomerXmlBean, printXmlBean, _graphDesktopController.createEdgesListener(), _graphDesktopController.getDesktopPane());

      onModeChanged(mode);
      _modeManager.addModeManagerListener(new ModeManagerListener()
      {
         @Override
         public void modeChanged(Mode newMode)
         {
            onModeChanged(newMode);
         }
      });

      //_graphPanel.add(_modeManager.getBottomPanel(), BorderLayout.SOUTH);
   }

   private void onModeChanged(Mode newMode)
   {
      _bottomPanelContainer.removeAll();
      _bottomPanelContainer.add(_modeManager.getBottomPanel());
      _bottomPanelContainer.revalidate();
      //_bottomPanelContainer.repaint();

      if (null != _split.getBottomComponent())
      {
         _split.remove(_split.getBottomComponent());
      }
      onHide();
   }

   public ModeManager getModeManager()
   {
      return _modeManager;
   }

   public void sessionEnding()
   {
      _graphDesktopController.sessionEnding();
   }
}
