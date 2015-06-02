/*
 * Copyright (C) 2012- BeiJing ChangLiaoTianXia Tech Co.,ltd  All Rights Reserved
 * 
 * 
 * created by mjg 20121027 18:18
 * 
 */

package org.cuteson.ojm.util;

import android.util.Log;

public class CLog 
{
	public static int outputPriority = 0; 
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARNING = 4;
	public static final int ERROR = 5;
	public static final int FUNCALL = 6;
	
	public static void v(String TAG, String info){
		if (outputPriority >= VERBOSE )
			return;
		Log.v(TAG, getFileLineMethod()+ " " + info);
	}
	
	public static void d(String TAG, String info){
		if (outputPriority >= DEBUG )
			return;

		Log.d(TAG, getFileLineMethod()+ " " + info);
	}
	
	public static void i(String TAG, String info){
		if (outputPriority >= INFO )
			return;
		Log.i(TAG, getFileLineMethod()+ " " + info);
	}

	public static void w(String TAG, String info){
		if (outputPriority >= WARNING)
			return;
		
		Log.w(TAG, getFileLineMethod()+ " " + info);
	}
	
	public static void e(String TAG, String info){
		if (outputPriority >= ERROR)
			return;
		Log.e(TAG, getFileLineMethod()+ " " + info);
	}
	
	public static boolean can()
	{
		
		return true;
	}
	
	public static String getFileLineMethod() 
	{ 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2]; 
		StringBuffer toStringBuffer = new StringBuffer("[").append( 
		traceElement.getFileName()).append(" | ").append( 
		traceElement.getLineNumber()).append(" | ").append( 
		traceElement.getMethodName()).append("]"); 
		return toStringBuffer.toString(); 
		
	} 
		
	public static String _FILE_() 
	{ 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2]; 
		return traceElement.getFileName(); 
		
	} 

	public static String _CLASS_() 
	{ 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2]; 
		return traceElement.getClassName(); 
	} 
	

	public static String _FUNC_() 
	{ 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2]; 
		return traceElement.getMethodName(); 
	} 


	public static int _LINE_() 
	{ 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[2]; 
		return traceElement.getLineNumber(); 
	} 
	
}