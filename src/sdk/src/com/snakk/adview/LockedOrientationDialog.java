package com.snakk.adview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/**
 * This dialog locking screen orientation during showing
 * @author synergy
 *
 */
public class LockedOrientationDialog extends Dialog {
	
	private OnDismissListener unlockListener = null;
	
    public LockedOrientationDialog(Context context) {
        super(context);
        lockOrientation(context);
    }

    public LockedOrientationDialog(Context context, int theme) {
    	super(context, theme);
    	lockOrientation(context);
    }
    
    protected LockedOrientationDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
    	super(context, cancelable, cancelListener);
    	lockOrientation(context);
    }
    
    @Override
    public void setOnDismissListener(final OnDismissListener listener) {
    	if (unlockListener != null){
    		super.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					listener.onDismiss(dialog);
					unlockListener.onDismiss(dialog);					
				}
			});
    		
    	} else {
    		super.setOnDismissListener(listener);
    	}
    }
    
    private void lockOrientation(Context context){
    	if (context instanceof Activity) {
			final Activity activity = (Activity) context;
			if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED){
				switch (context.getResources().getConfiguration().orientation)
				{
				case Configuration.ORIENTATION_PORTRAIT:
					activity.setRequestedOrientation(
							ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					break;
				case Configuration.ORIENTATION_LANDSCAPE:
					activity.setRequestedOrientation(
							ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					break;
				}
				unlockListener = new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						activity.setRequestedOrientation(
								ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
					}
				};
				setOnDismissListener(unlockListener);	
			}		
		}
    }
}
