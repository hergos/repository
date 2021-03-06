package org.squirrelsql.services;

import com.google.common.base.Strings;
import javafx.application.Application;
import org.squirrelsql.AppState;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler
{
   private final Properties _properties = new Properties();
   private String _message;

   public PropertiesHandler(Application.Parameters parameters)
   {
      String propertiesPath = parameters.getNamed().get("p");

      if(Strings.isNullOrEmpty(propertiesPath))
      {
         _message = "No properties file was passed on the command line.\n" + getPropsInfo();
      }
      else if (false == new File(propertiesPath).exists())
      {
         _message = "The properties file " + propertiesPath + " passed on the command line does not exist.\n" + getPropsInfo();
      }
      else if (false == new File(propertiesPath).isFile())
      {
         _message = "The properties file " + propertiesPath + " passed on the command line is no a file.\n" + getPropsInfo();
      }

      if(null != _message)
      {
         return;
      }


      try
      {
         _properties.load(new FileInputStream(new File(propertiesPath)));
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getPropsInfo()
   {
      return "To pass a properties file use the command line option --p=<propertiesFilePath>.\n" +
            "SQuirreL will now use its default properties. Those are:\n" +
            SquirrelProperty.getDefaultsString();
   }

   public String getProperty(SquirrelProperty squirrelProperty)
   {
      String ret = _properties.getProperty(squirrelProperty.getKey());

      if(null == ret)
      {
         ret = squirrelProperty.getDefaultValue();
      }

      return ret;
   }

   public void doAfterBootstrap()
   {
      if (false == Strings.isNullOrEmpty(_message))
      {
         AppState.get().getMessagePanelCtrl().info(_message);
      }
   }
}
