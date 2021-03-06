package com.blackmoonit.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Standard application preference class. v11+ defines a preference-headers xml file that references the 
 * other preference xml files.<br>
 * <br>
 * REQUIRED RESOURCES (listed as Parameters):
 * @param layout.app_prefs - UI layout for v1-v10, optional in v11+
 * @param layout.app_pref_headers - Pref Headers for UI layout on v11+
 * @param string.prefs_name - filename of prefs (optional, uses getPackageName()+".prefs" otherwise).
 * 
 * @author Ryan Fischbach
 */
public abstract class AppPreferenceBase extends PreferenceActivity {
	protected int R_layout_app_prefs = 0;
	protected int R_layout_app_pref_headers = 0;
	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;
	protected Method mRecreate = null;
	
	protected static int getResId(Context aContext, String aResType, String aResName) {
		return aContext.getApplicationContext().getResources().getIdentifier(aResName, 
				aResType, aContext.getPackageName());
	}
	
	protected int getResId(String aResType, String aResName) {
		return getResId(this,aResType, aResName);
	}
	
	/**
	 * Checks to see if using new v11+ way of handling PrefFragments.
	 * @return Returns false pre-v11, else checks to see if using headers.
	 */
	public boolean isNewV11Prefs() {
		if (mHasHeaders!=null && mLoadHeaders!=null) {
			try {
				return (Boolean)mHasHeaders.invoke(this);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return false;
	}
	
	/**
	 * Wrapper for {@link #invalidateHeaders()}.
	 */
	public void recreatePrefsView() {
		if (mRecreate!=null) {
			try {
				mRecreate.invoke(this);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}
	
	@Override
	public void onCreate(Bundle aSavedState) {
		R_layout_app_prefs = getResId("layout","app_prefs");
		if (R_layout_app_prefs==0) {
			R_layout_app_prefs = getResId("xml","app_prefs");
		}
		R_layout_app_pref_headers = getResId("layout","app_pref_headers");
		if (R_layout_app_pref_headers==0) {
			R_layout_app_pref_headers = getResId("xml","app_pref_headers");
		}
		try {
			mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
			mHasHeaders = getClass().getMethod("hasHeaders");
			mRecreate = getClass().getMethod("recreate");
		} catch (NoSuchMethodException e) {
		}
		super.onCreate(aSavedState);
		convertOldPrefs();
		if (!isNewV11Prefs()) {
			loadPrefLayouts();
		}
		setup();
	}

	/**
	 * Called before the prefs are loaded from resrouce.
	 */
	protected void convertOldPrefs() {
		//default no-op
	}
	
	protected int[] getPrefResources() {
		return new int[] {R_layout_app_prefs};
	}

	@SuppressWarnings("deprecation")
	protected void loadPrefLayouts() {
		for (int thePrefResId:getPrefResources()) {
			if (thePrefResId!=0) {
				addPreferencesFromResource(thePrefResId);
			}
		}
	}

	/**
	 * v11+ way to loadPrefLayouts via an xml file with preference-header tags.
	 */
	@Override
	public void onBuildHeaders(List<Header> aTarget) {
		if (R_layout_app_pref_headers==0 || mLoadHeaders==null)
			return;
		try {
			mLoadHeaders.invoke(this,new Object[]{R_layout_app_pref_headers,aTarget});
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}		
	}

	protected abstract void setup();
	
	/**
	 * Loads the prefs file refered to by the string resource {@code prefs_name}.
	 * 
	 * @param aContext - the Context
	 * @return Returns the SharedPreferences from the file refered to by string.prefs_name
	 */
	static public SharedPreferences getPrefs(Context aContext) {
		return PreferenceManager.getDefaultSharedPreferences(aContext);
	}
	
	static public void setDefaultPrefs(Context aContext, int[] aPrefResourceIds, boolean bResetPrefs) {
		for (int thePrefResId:aPrefResourceIds) {
			PreferenceManager.setDefaultValues(aContext,thePrefResId,bResetPrefs);
		}
	}
	
	/**
	 * Erases all preferences, thus causing all references to use their default values.
	 */
	static public void clearPrefs(Context aContext, int[] aPrefResIds) {
		SharedPreferences.Editor theEditor = getPrefs(aContext).edit().clear();
		if (aPrefResIds!=null) {
			setDefaultPrefs(aContext,aPrefResIds,true);
		}
		theEditor.commit();
	}
	
	@SuppressWarnings("deprecation")
	protected void resetPrefScreens() {
		if (isNewV11Prefs()) {
			recreatePrefsView();
		} else {
			setPreferenceScreen(null);
			loadPrefLayouts();
		}
	}
	
	public void resetPrefs() {
		clearPrefs(this,getPrefResources());
		resetPrefScreens();
		setup();
	}
	
	static public class PreferencePane extends PreferenceFragment {

		@Override
		public void onCreate(Bundle aSavedState) {
			super.onCreate(aSavedState);
			Context anAct = getActivity();
			String thePrefScreenLayoutName = getArguments().getString("layout-res");
			if (thePrefScreenLayoutName!=null && !thePrefScreenLayoutName.equals("")) {
				int thePrefRes = anAct.getResources().getIdentifier(thePrefScreenLayoutName,
						"layout",anAct.getPackageName());
				//check xml folder if not in the layout folder
				if (thePrefRes==0) {
					thePrefRes = anAct.getResources().getIdentifier(thePrefScreenLayoutName,
							"xml",anAct.getPackageName());
				}
				if (thePrefRes!=0) {
					addPreferencesFromResource(thePrefRes);
				}
			}
		}
		
	}

	/**
	 * Returns the current application's friendly version name.
	 * 
	 * @param aContext - application context
	 * @return
	 *      Returns the version name as defined by the application. Appends an "*" if the
	 *      application is also debuggable.
	 */
	static public String getAppVersionName(Context aContext) {
		String theResult = "e^πi-1";
		if (aContext!=null) {
			PackageManager pm = aContext.getPackageManager();
			PackageInfo pi;
			try {
				pi = pm.getPackageInfo(aContext.getPackageName(),0);
				theResult = pi.versionName;
				if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)!=0) {
					theResult += "*";
				}
			} catch (NameNotFoundException e) {
				//use default result if there was a nnfe
			}
		}
		return theResult;
	}

}
