package org.squirrelsql.session;

import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import org.squirrelsql.AppState;
import org.squirrelsql.ApplicationCloseListener;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.SplitPositionSaver;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.objecttree.*;
import org.squirrelsql.session.sql.NewSqlTabHelper;
import org.squirrelsql.session.sql.SqlTabCtrl;
import org.squirrelsql.workaround.SessionTabSelectionRepaintWA;

import java.util.ArrayList;
import java.util.List;


public class SessionCtrl
{
   private SplitPositionSaver _objecttreeSplitPosSaver = new SplitPositionSaver(getClass(), "objecttree.split.loc");

   private static final String PREF_PRE_SELECT_SQL_TAB = "preselect.sql";


   private final TabPane _objectTreeAndSqlTabPane;
   private final ApplicationCloseListener _applicationCloseListener;


   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());
   private SplitPane _objectTabSplitPane = new SplitPane();
   private SqlTabCtrl _sqlTabCtrl;
   private final BorderPane _sessionPane;
   private final Tab _sessionTab;
   private SessionTabContext _sessionTabContext;

   public SessionCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;

      _sessionPane = new BorderPane();

      _sessionPane.setTop(new ActionManager().createToolbar());

      _objectTreeAndSqlTabPane = createObjectTreeAndSqlTabPane();

      _sessionPane.setCenter(_objectTreeAndSqlTabPane);

      _sessionTab = new Tab();
      _sessionTab.setGraphic(SessionUtil.createSessionTabHeader(_sessionTabContext));
      _sessionTab.setContent(_sessionPane);


      _sessionTab.setOnClosed(e -> onClose());
      _sessionTab.setOnSelectionChanged(this::onSelectionChanged);

      NewSqlTabHelper.registerSessionTabListener(sessionTabContext, _sessionTab);

      _applicationCloseListener = this::onClose;
      AppState.get().addApplicationCloseListener(_applicationCloseListener, ApplicationCloseListener.FireTime.WITHIN_SESSION_FIRE_TIME);
   }

   private void onSelectionChanged(Event e)
   {
      if(_sessionTab.isSelected())
      {
         AppState.get().getSessionManager().setCurrentlyActiveOrActivatingContext(_sessionTabContext);
         onTabChanged(_objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem());
      }
   }

   private TabPane createObjectTreeAndSqlTabPane()
   {
      TabPane ret = new TabPane();

      Tab objectsTab = createObjectsTab();
      ret.getTabs().add(objectsTab);
      new ActionManager().setActionScope(ActionScope.OBJECT_TREE);


      _sqlTabCtrl = new SqlTabCtrl(_sessionTabContext);

      Tab sqlTab = _sqlTabCtrl.getSqlTab();
      ret.getTabs().add(sqlTab);

      if(_pref.getBoolean(PREF_PRE_SELECT_SQL_TAB, false))
      {
         ret.getSelectionModel().select(sqlTab);
         onSwitchedToSqlTab();
      }


      SessionTabSelectionRepaintWA.forceTabContentRepaintOnSelection(ret);

      ret.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTabChanged(newValue));

      return ret;
   }

   private void onSwitchedToSqlTab()
   {
      _sqlTabCtrl.requestFocus();
      new ActionManager().setActionScope(ActionScope.SQL_EDITOR);
   }

   private void onTabChanged(Tab newSelectedTab)
   {
      if(_sqlTabCtrl.getSqlTab() == newSelectedTab)
      {
         onSwitchedToSqlTab();
      }
      else
      {
         new ActionManager().setActionScope(ActionScope.OBJECT_TREE);
      }
   }

   private Tab createObjectsTab()
   {
      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      TreeView<ObjectTreeNode> objectsTree = new TreeView<>();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      removeEmptySchemasIfRequested(objectsTree, _sessionTabContext.getSession());


      _objectTabSplitPane.setOrientation(Orientation.HORIZONTAL);
      _objectTabSplitPane.getItems().add(objectsTree);
      _objectTabSplitPane.getItems().add(new TreeDetailsController(objectsTree, _sessionTabContext.getSession()).getComponent());

      _objecttreeSplitPosSaver.apply(_objectTabSplitPane);

      TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
      aliasItem.setExpanded(true);
      objectsTree.getSelectionModel().select(aliasItem);



      objectsTab.setContent(_objectTabSplitPane);

      return objectsTab;
   }


   private void removeEmptySchemasIfRequested(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      if(false == session.getSchemaCache().getAliasPropertiesDecorator().isHideEmptySchemasInObjectTree())
      {
         return;
      }

      List<TreeItem<ObjectTreeNode>> schemas = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY);
      removeEmptyNodes(schemas);

      List<TreeItem<ObjectTreeNode>> catalogs = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY);
      removeEmptyNodes(catalogs);

   }

   private void removeEmptyNodes(List<TreeItem<ObjectTreeNode>> nodes)
   {
      List<TreeItem<ObjectTreeNode>> toRemove = new ArrayList<>();

      for (TreeItem<ObjectTreeNode> schema : nodes)
      {
         if(0 == schema.getChildren().size())
         {
            toRemove.add(schema);
         }
      }

      for (TreeItem<ObjectTreeNode> del : toRemove)
      {
         del.getParent().getChildren().remove(del);
      }
   }


   private void onClose()
   {
      _objecttreeSplitPosSaver.save(_objectTabSplitPane);


      _pref.set(PREF_PRE_SELECT_SQL_TAB, _objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem() == _sqlTabCtrl.getSqlTab());

      _sqlTabCtrl.close();
      _sessionTabContext.getSession().close();

      AppState.get().getSessionManager().sessionClose(_sessionTabContext);

      AppState.get().removeApplicationCloseListener(_applicationCloseListener);
   }

   public Tab getSessionTab()
   {
      return _sessionTab;
   }
}
