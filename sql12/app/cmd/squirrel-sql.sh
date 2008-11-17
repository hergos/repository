#! /bin/sh

[ ${JAVA_HOME} ] && JAVA=${JAVA_HOME}/bin/java || [ %JAVA_HOME ]  && JAVA=%JAVA_HOME/bin/java  || JAVA=java


# Are we running within Cygwin on some version of Windows?
cygwin=false;
case "`uname -s`" in
	CYGWIN*) cygwin=true ;;
esac

# Squirrel home.
SQUIRREL_SQL_HOME='%INSTALL_PATH'

# SQuirreL home in Unix format.
if $cygwin ; then
	UNIX_STYLE_HOME=`cygpath "$SQUIRREL_SQL_HOME"`
else
	UNIX_STYLE_HOME=$SQUIRREL_SQL_HOME
fi

# First entry in classpath is the Squirrel application.
TMP_CP=$UNIX_STYLE_HOME/squirrel-sql.jar

# Then add all library jars to the classpath.
for a in $UNIX_STYLE_HOME/lib/*; do
	TMP_CP="$TMP_CP":"$a";
done

# Set the update app's classpath to use jars in download area first, then the installed jars
UPDATE_CP=$TMP_CP
for a in $UNIX_STYLE_HOME/update/downloads/core/*; do
    UPDATE_CP="$a":"$UPDATE_CP"
done


# Now add the system classpath to the classpath. If running
# Cygwin we also need to change the classpath to Windows format.
if $cygwin ; then
	TMP_CP=`cygpath -w -p $TMP_CP`
	UPDATE_CP=`cygpath -w -p $UPDATE_CP`
	TMP_CP=$TMP_CP';'$CLASSPATH
	UPDATE_CP=$UPDATE_CP';'$CLASSPATH
else
	TMP_CP=$TMP_CP:$CLASSPATH
	UPDATE_CP=$UPDATE_CP:$CLASSPATH
fi

# Check for updates and prompt to apply if any are available
if [ -f $UNIX_STYLE_HOME/update/downloads/core/squirrel-sql.jar -a -f $UNIX_STYLE_HOME/update/changeList.xml ]; then
        $JAVA -cp $UPDATE_CP -Dlog4j.defaultInitOverride=true -Dprompt=true net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchUpdateApplication -l $SQUIRREL_SQL_HOME/update-log4j.properties
fi

#To add translation working directories to your classpath edit and uncomment this line:
#$JAVA -Xmx256m -cp $TMP_CP:<your working dir here> net.sourceforge.squirrel_sql.client.Main --log-config-file $SQUIRREL_SQL_HOME/log4j.properties --squirrel-home $SQUIRREL_SQL_HOME $1 $2 $3 $4 $5 $6 $7 $8 $9

#To change the language edit and uncomment this line:
#$JAVA -Xmx256m -cp $TMP_CP:<your working dir here> -Duser.language=<your language here> net.sourceforge.squirrel_sql.client.Main --log-config-file $SQUIRREL_SQL_HOME/log4j.properties --squirrel-home $SQUIRREL_SQL_HOME $1 $2 $3 $4 $5 $6 $7 $8 $9

# Launch SQuirreL application
$JAVA -Xmx256m -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main --log-config-file $SQUIRREL_SQL_HOME/log4j.properties --squirrel-home $SQUIRREL_SQL_HOME $1 $2 $3 $4 $5 $6 $7 $8 $9
