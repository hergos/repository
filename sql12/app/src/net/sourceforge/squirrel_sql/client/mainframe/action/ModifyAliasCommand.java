package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This <CODE>ICommand</CODE> allows the user to modify an existing
 * <TT>ISQLAlias</TT>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ModifyAliasCommand implements ICommand
{
	/** Application API. */
	private final IApplication _app;

	/** <TT>ISQLAlias</TT> to be modified. */
	private final ISQLAlias _sqlAlias;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	sqlAlias	<TT>ISQLAlias</TT> to be modified.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> or
	 *			<tt>IApplication</tt> passed.
	 */
	public ModifyAliasCommand(IApplication app, ISQLAlias sqlAlias)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == nu;;");
		}
		_app = app;
		_sqlAlias = sqlAlias;
	}

	/**
	 * Display a dialog allowing user to maintain the <TT>ISQLAlias</TT>.
	 */
	public void execute()
	{
		_app.getWindowManager().showModifyAliasInternalFrame(_sqlAlias);
	}
}