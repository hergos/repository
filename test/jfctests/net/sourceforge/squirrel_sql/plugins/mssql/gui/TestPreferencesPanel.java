/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.mssql.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.mssql.prefs.PreferencesManager;

public class TestPreferencesPanel {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new BorderLayout());
        ApplicationArguments.initialize(new String[0]);


        final PluginQueryTokenizerPreferencesManager prefsManager = 
            new PluginQueryTokenizerPreferencesManager();
        IQueryTokenizerPreferenceBean bean = prefsManager.getPreferences();        
        final PluginQueryTokenizerPreferencesPanel p = 
            new PluginQueryTokenizerPreferencesPanel(prefsManager, bean, 
                                                     "MS SQL-Server");
        JScrollPane sp = new JScrollPane(p);
        f.getContentPane().add(sp, BorderLayout.CENTER);
        JButton button = new JButton("Save");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                p.applyChanges();
                PreferencesManager.unload();
            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);
        buttonPanel.add(exitButton);
        f.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        f.setBounds(200, 50,700, 700);
        f.setVisible(true);
    }

}
