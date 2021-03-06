package com.blackmoonit.database;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Completely static provider contracts make it impossible to use OOP in
 * order to handle common definitions and helper methods. These classes and
 * interfaces were designed to work together and mimic static contracts as
 * well as provide access to common definitions and helper methods. The end
 * goal is to make it easier to create ContentProvider contracts, which in 
 * turn allow use of a generic ContentProvider such as {@link ContractedProvider}.
 *
 * @author Ryan Fischbach
 */
public class ProviderContract {
	
	/**
	 * Database meta information definitions required by the ProviderContract.
	 */
	static public interface Database {		
		/**
		 * Gets your statically created {@link #DbProviderInfo} object (simple singleton). 
		 * Please create your static object like the sample code provided.<br><code>
		 * static public YourDatabaseContract mDbContract = new YourDatabaseContract();<br>
		 * static public DbProviderInfo mDbInfo = new DbProviderInfo(mDbContract);<br>
		 * private YourDatabaseContract() {} //remember to make your class "final"
		 * </code><br>
		 * @return Rerturn the static variable you created.
		 */
		public DbProviderInfo getDbInfo();
		
		/**
		 * The name of this Data Dictionary. By default, this name will be used 
		 * to augment the {@link DbProviderInfo#getAuthority() provider authority} and 
		 * {@link DbProviderInfo#getBaseMIMEsubtype() MIME subtype} strings to help 
		 * ensure their global namespace uniqueness.
		 */
		public String getDbName();

	    /**
	     * Data actions are specified in the username@authority section of an
	     * ObserverUri. Use DATA_ACTION_NULL for a standard ContentUri.
	     * @see TableProviderInfo#getObserverUri(String)
	     * @see Database#DATA_ACTION_INSERT
	     * @see Database#DATA_ACTION_UPDATE
	     * @see Database#DATA_ACTION_DELETE
	     * @see DbProviderInfo#ensureContentUri(Uri)
	     */
		static public final String DATA_ACTION_NULL = "";
	    /**
	     * Data actions are specified in the username@authority section of an
	     * ObserverUri. Use DATA_ACTION_NULL for a standard ContentUri.
	     * @see TableProviderInfo#getObserverUri(String)
	     * @see Database#DATA_ACTION_NULL
	     * @see Database#DATA_ACTION_UPDATE
	     * @see Database#DATA_ACTION_DELETE
	     * @see DbProviderInfo#ensureContentUri(Uri)
	     */
		static public final String DATA_ACTION_INSERT = "insert@";
	    /**
	     * Data actions are specified in the username@authority section of an
	     * ObserverUri. Use DATA_ACTION_NULL for a standard ContentUri.
	     * @see TableProviderInfo#getObserverUri(String)
	     * @see Database#DATA_ACTION_NULL
	     * @see Database#DATA_ACTION_INSERT
	     * @see Database#DATA_ACTION_DELETE
	     * @see DbProviderInfo#ensureContentUri(Uri)
	     */
		static public final String DATA_ACTION_UPDATE = "update@";
	    /**
	     * Data actions are specified in the username@authority section of an
	     * ObserverUri. Use DATA_ACTION_NULL for a standard ContentUri.
	     * @see TableProviderInfo#getObserverUri(String)
	     * @see Database#DATA_ACTION_NULL
	     * @see Database#DATA_ACTION_INSERT
	     * @see Database#DATA_ACTION_UPDATE
	     * @see DbProviderInfo#ensureContentUri(Uri)
	     */
		static public final String DATA_ACTION_DELETE = "delete@";

		/**
		 * MIME category used for returning a set of records.
		 */
		static public final String MIME_CATEGORY_RESULT_SET = "vnd.android.cursor.dir";
		
		/**
		 * MIME category used for returning a single record.
		 */
		static public final String MIME_CATEGORY_RESULT_ONE = "vnd.android.cursor.item";
		
		/**
		 * Convenience ascending constant for use in ORDER_BY clause of query strings. 
		 */
		static public final String SORT_LoHi = " ASC";
		
		/**
		 * Convenience descending constant for use in ORDER_BY clause of query strings.
		 */
		static public final String SORT_HiLo = " DESC";

	}
	
	static public class DbProviderInfo {
		protected final ProviderContract.Database mDbContract;
		
		public DbProviderInfo(Database aDbContract) {
			mDbContract = aDbContract;
		}

		/**
		 * @return Returns the database name.
		 */
		public ProviderContract.Database getDbContract() {
			return mDbContract;
		}

		/**
		 * Defines the authority that will be used to access your provider.
		 * @return Returns the Authority part of the Uri used to access your provider.
		 */
		public String getAuthority() {
			return "com.blackmoonit.provider."+mDbContract.getDbName();
		}

		/**
		 * MIME subtype used to differentiate records and record sets from other providers.
		 * @return Returns a string which will be used by the TableDefinitions contained
		 * in this contract to help ensure unique MIME types for our provider results.
		 */
		public String getBaseMIMEsubtype() {
			return "vnd.blackmoonit."+mDbContract.getDbName();
		}
		
		/**
		 * MIME type used for the database itself. Specific table rows should use 
		 * MyDbContract.MyTableContract.mTableInfo.getMIMEsubtype().
		 * @return MIME type string
		 * @see TableProviderInfo#getMIMEsubtype()
		 * @see TableProviderInfo#getMIMEtypeForResultSet()
		 * @see TableProviderInfo#getMIMEtypeForSingularResult() 
		 */
		public String getMIMEtype() {
			return Database.MIME_CATEGORY_RESULT_SET + "/" + getBaseMIMEsubtype();
		}

		/**
		 * The passed in Uri is converted from a potentially DataAction-specific ObserverUri 
		 * to a standard ContentUri. Use this function in your ContentObserver's onChange
		 * method to convert the returned Uri back into a ContentUri so you can use it with 
		 * the Provider again. It is safe to pass in either an ObserverUri or ContentUri.
		 * @param aUri - either an ObserverUri or ContentUri.
		 * @return Returns a ContentUri.
		 * @see TableProviderInfo#ensureContentUri(Uri)
		 */
		static public Uri ensureContentUri(Uri aUri) {
			if (aUri!=null) {
				String theAuthority = aUri.getAuthority();
				if (theAuthority!=null) {
					int idx = theAuthority.indexOf("@");
					if (idx>=0) {
						aUri = aUri.buildUpon().encodedAuthority(theAuthority.substring(idx+1)).build();
					}
				}
			}
			return aUri;
		}

	}
	
	//==================================================================================
	
	/**
	 * Table meta information definitions required by the ProviderContract.
	 */
	static public interface Table extends BaseColumns {
		/**
		 * Gets your statically created {@link #TableProviderInfo} object (simple singleton). 
		 * Please create your static object like the sample code provided.<br><code>
		 * static public YourTableContract mTableContract = new YourTableContract();<br>
		 * static public TableProviderInfo mTableInfo = <br>
		 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		 * new TableProviderInfo(mDbContract, mTableContract);<br>
		 * private YoutTableContract() {} //remember to make your class "final"
		 * </code><br>
		 * @return Rerturn the static variable you created.
		 */
		public TableProviderInfo getTableInfo();

		/**
		 * Gets the table name for which this dictionary defines.
		 * @return Returns the name of the table this dictionary defines.
		 */
		public String getTableName();

		/**
		 * Return the default sort order as defined by the orderBy parameter of the 
		 * {@link android.content.ContentProvider#query(Uri, String[], String, String[], String) query()} 
		 * method.
		 * @return Returns the orderBy string parameter that is default for this table.
		 * Returning NULL will generally be an "unordered" result.
		 */
		public String getDefaultSortOrder();
		
		/**
		 * Gets the fieldname the provider will use as a record ID field. 
		 * _ID is predefined, so it is the recommended default.
		 * @return Returns your ID field name, consider returning _ID as the default.
		 */
		public String getIdFieldName();

	}
	
	static public class TableProviderInfo {
		protected final Database mDbContract;
		protected final Table mTableContract;
		
		public TableProviderInfo(Database aDbContract, Table aTableContract) {
			mDbContract = aDbContract;
			mTableContract = aTableContract;
		}
		
		/**
		 * @return Returns the database contract.
		 */
		public Database getDbContract() {
			return mDbContract;
		}
		
		/**
		 * @return Returns the table contract.
		 */
		public Table getTableContract() {
			return mTableContract;
		}
		
		/**
		 * The content Uri for this table. If NULL is passed in as the ID parameter, 
		 * the "result set" Uri is returned with no ID path segment appended.<br>
		 * NOTE: a value of "#" for the ID parameter is reserved as the  
		 * {@link #getContentUriWithIdPattern() path pattern}.<br>
		 * NOTE on NULL IDs: if NULL is desired as an ID value... unknown how to support it. 
		 * Tests will need to be conducted and the results should replace this text.
		 * @param aIDstring - ID parameter already converted to String to place on the Uri.
		 * If NULL is passed in, the standard "result set" Uri is returned.
		 * @return Returns the Uri to be used to interact with this table. 
		 * If NULL is passed as the ID value, then the "result set" Uri will
		 * be returned, leaving the ID path segment out.
		 */
		public Uri getContentUri(String aIDstring) {
			Uri theUri = getObserverUri(Database.DATA_ACTION_NULL);
			if (aIDstring!=null) {
				theUri = Uri.withAppendedPath(theUri,aIDstring);
			}
			return theUri;
		}
		
		/**
		 * ContentObservers that wish to know what kind of action caused the onChange to 
		 * fire would need to register their Uri using this function with the appropriate
		 * Database.DATA_ACTION_* constant passed in.
		 * @param aDataAction - one of {@link Database#DATA_ACTION_INSERT} or 
		 * {@link DATA_ACTION_UPDATE} or {@link DATA_ACTION_DELETE} or {@link Database#DATA_ACTION_NULL}
		 * @return Returns the Uri to register to observe particular Provider actions.
		 */
		public Uri getObserverUri(String aDataAction) {
			Uri theUri = Uri.parse(ContentResolver.SCHEME_CONTENT+"://"+aDataAction+
					mDbContract.getDbInfo().getAuthority()+"/"+
					mTableContract.getTableName());
			return theUri;
		}

		/**
		 * This method is used internally by the SqlContractProvider to convert the 
		 * standard Observer notification ContentUri into a specific data action Uri
		 * whose term I have coined as an ObserverUri.
		 * @param aUri - ContentUri to convert to an ObserverUri for a specific data action.
		 * @param aDataAction - encode this data action within the Uri passed in.
		 * @return Returns an ObserverUri encoded for a specific data action.
		 * @author Ryan Fischbach
		 */
		static public Uri cnvContentUriToObserverUri(Uri aUri, String aDataAction) {
			if (aUri!=null) {
				return aUri.buildUpon().encodedAuthority(aDataAction+aUri.getAuthority()).build();
			}
			return aUri;
		}
		
		/**
		 * The passed in Uri is converted from a potentially DataAction-specific ObserverUri 
		 * to a standard ContentUri. Use this function in your ContentObserver's onChange
		 * method to convert the returned Uri back into a ContentUri so you can use it with 
		 * the Provider again. It is safe to pass in either an ObserverUri or ContentUri.
		 * @param aUri - either an ObserverUri or ContentUri.
		 * @return Returns a ContentUri.
		 * @see DbProviderInfo#ensureContentUri(Uri)
		 */
		public Uri ensureContentUri(Uri aUri) {
			return DbProviderInfo.ensureContentUri(aUri);
		}

		
		/**
		 * Defines which segment of the Uri path is the ID portion.
		 * Base 0 position of an ID path segment means that our basic Uri path of
		 * "content://authority/tablename/ID" will return a result of 1
		 * (0 being that of "tablename").
		 * @return Returns the segment number (base 0) of the ID portion of the Uri
		 * returned in {@link #getContentUri(String)}.
		 */
		public int getUriPathIdPosition() {
			return 1;
		}
		
		/**
		 * The content Uri match pattern for a single record of this table. 
		 * Use this to match incoming Uri's in your provider.
		 * @return Returns {@link #getContentUri(String) getContentUri("#")}.
		 */
		public Uri getContentUriWithIdPattern() {
			return getContentUri("#");
		}

		/**
		 * Adds this table's Uri matcher for a single row.
		 * @param aMatcher - the provider matcher being constructed.
		 * @param aMatchCode - the match code to return when an incoming provider Uri matches.
		 */
		public void addTableRowUri(UriMatcher aMatcher, int aMatchCode) {
			aMatcher.addURI(mDbContract.getDbInfo().getAuthority(),
					mTableContract.getTableName()+"/#",aMatchCode);
		}
		
		/**
		 * Adds this table's Uri matcher for a set of rows.
		 * @param aMatcher - the provider matcher being constructed.
		 * @param aMatchCode - the match code to return when an incoming provider Uri matches.
		 */
		public void addTableSetUri(UriMatcher aMatcher, int aMatchCode) {
			aMatcher.addURI(mDbContract.getDbInfo().getAuthority(),
					mTableContract.getTableName(),aMatchCode);
		}
		
		/**
		 * Gets the MIME subtype for this table's data.
		 * @return By default, this returns the data dictionary's 
		 * {@link #getBaseSubType() base subtype} with the table's 
		 * name appended to it.
		 */
		public String getMIMEsubtype() {
			return mDbContract.getDbInfo().getBaseMIMEsubtype()+"."+
					mTableContract.getTableName();
		}
		
		/**
		 * The provider will use this method to return the MIME type for result sets.
		 * @return Returns the full MIME type for result sets.
		 */
		public String getMIMEtypeForResultSet() {
			return Database.MIME_CATEGORY_RESULT_SET+"/"+getMIMEsubtype();
		}
		
		/**
		 * The provider will use this method to return the MIME type for singular results.
		 */
		public String getMIMEtypeForSingularResult() {
			return Database.MIME_CATEGORY_RESULT_ONE+"/"+getMIMEsubtype();
		}
	
	}

}
