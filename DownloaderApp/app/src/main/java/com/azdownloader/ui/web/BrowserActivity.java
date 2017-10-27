package com.azdownloader.ui.web;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.ActionBar;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.azdownloader.R;
import com.azdownloader.ui.common.ToolbarActivity;
import com.azdownloader.ui.main.MainActivity;
import com.azdownloader.util.Utility;
import static com.azdownloader.BuildConfig.DEBUG;

public class BrowserActivity extends ToolbarActivity
{
	private static final String[] VIDEO_SUFFIXES = new String[]{
		".mp4",
		".flv",
		".rm",
		".rmvb",
		".wmv",
		".avi",
		".mkv",
		".webm"
	};
	
	private WebView mWeb;
	private ProgressBar mProgress;
	private EditText mUrl;
	private InputMethodManager mInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		// Toolbar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		// Custom view
		if (Build.VERSION.SDK_INT < 21) {
			ContextThemeWrapper wrap = new ContextThemeWrapper(this, R.style.Theme_AppCompat);
			LayoutInflater inflater = (LayoutInflater) wrap.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View custom = inflater.inflate(R.layout.browser_url, null);
			getSupportActionBar().setCustomView(custom);
		} else {
			getSupportActionBar().setCustomView(R.layout.browser_url);
		}
		
		// Initialize WebView
		mProgress = Utility.findViewById(this, R.id.progress);
		mUrl = Utility.findViewById(getSupportActionBar().getCustomView(), R.id.browser_url);
		mWeb = Utility.findViewById(this, R.id.web);
		mWeb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);

				try {

					//  Snackbar.make(tvTagData," url \n"+url,Snackbar.LENGTH_LONG).show();
					//  String urlPath = obj.stream_url + "?client_id=nnlknlkl";
					Log.e("--post--","--Link--"+url);



					if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
						android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(url);
					} else {
						android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						android.content.ClipData clip = android.content.ClipData.newPlainText("text label",url);
						clipboard.setPrimaryClip(clip);
					}

					//	showDownloadAlert(strUrl,strVideoTitle+".mp4");

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				getSupportActionBar().setDisplayShowCustomEnabled(false);
				getSupportActionBar().setDisplayShowTitleEnabled(true);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				mProgress.setProgress(0);
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap fav) {
				if (isVideo(url)) {
					// If viewing a video, start download immediately
					view.stopLoading();
					Intent i = new Intent();
					i.setAction(MainActivity.INTENT_DOWNLOAD);
					i.setDataAndType(Uri.parse(url), "application/octet-stream");
					startActivity(i);
					finish();
				}
			}
		});
		mWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView v, int progress) {
				mProgress.setProgress(progress);
			}
			
			@Override
			public void onReceivedTitle(WebView v, String title) {
				getSupportActionBar().setTitle(title);
			}
		});
		mWeb.getSettings().setJavaScriptEnabled(true);
		mWeb.setDownloadListener(new DownloadListener() {

				@Override
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
					// Start MainActivity for downloading
					try {

						Log.i("=onDownloadStart=","=url="+url);

						if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
							android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clipboard.setText(url);
						} else {
							android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							android.content.ClipData clip = android.content.ClipData.newPlainText("text label",url);
							clipboard.setPrimaryClip(clip);
						}

						Intent i = new Intent();
						i.setAction(MainActivity.INTENT_DOWNLOAD);
						i.setDataAndType(Uri.parse(url), mimeType);
						startActivity(i);
						finish();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			
		});
		mUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent ev) {
					if (actionId == EditorInfo.IME_ACTION_GO) {
						String url = mUrl.getText().toString();
						
						if (!url.startsWith("http")) {
							url = "http://" + url;
						}
						
						mWeb.loadUrl(url);
						switchCustom();
						return true;
					}
					return false;
				}
		});
		mWeb.addJavascriptInterface(new MyJavascriptInterface(), "HTMLOUT");
		
		mWeb.loadUrl("https://www.google.com");




		switchCustom();
		
		mToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchCustom();
			}
		});
		
		// Parse Intent
		Intent i = getIntent();
		if (i.getAction().equals(Intent.ACTION_SEND)) {
			String data = i.getStringExtra(Intent.EXTRA_TEXT);
			mWeb.loadUrl(data);
			mUrl.setText(data);
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//setData();
			}
		},1000);

	}
























	protected void setData() {
		try{
			mWeb.setWebViewClient(new WebViewClient()
			{
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String strUrl)
				{

					Log.e("--post--","--Link--"+strUrl);


					try {

						//  Snackbar.make(tvTagData," url \n"+url,Snackbar.LENGTH_LONG).show();
						//  String urlPath = obj.stream_url + "?client_id=nnlknlkl";
						Log.e("--post--","--Link--"+strUrl);



						if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
							android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clipboard.setText(strUrl);
						} else {
							android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							android.content.ClipData clip = android.content.ClipData.newPlainText("text label",strUrl);
							clipboard.setPrimaryClip(clip);
						}

						//	showDownloadAlert(strUrl,strVideoTitle+".mp4");

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

					return true;
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


	}

	@Override
	protected int getLayoutResource() {
		return R.layout.browser;
	}

	Menu menu22;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.browser, menu);
		menu22 = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.detector:
				mWeb.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (mWeb.canGoBack()) {
			mWeb.goBack();
		} else {
			finish();
		}
	}
	
	private void switchCustom() {
		int opt = getSupportActionBar().getDisplayOptions();
		
		if ((opt & ActionBar.DISPLAY_SHOW_CUSTOM) != 0) {
			getSupportActionBar().setDisplayShowCustomEnabled(false);
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			mInput.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
		} else {
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			mInput.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
			mUrl.requestFocus();
			mUrl.setText(mWeb.getUrl());
			mUrl.setSelection(0, mUrl.getText().length());
		}
	}
	
	private void showVideoChoices(final String[] vids) {
		new AlertDialog.Builder(this)
			.setItems(vids, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Intent i = new Intent();
					i.setAction(MainActivity.INTENT_DOWNLOAD);
					i.setDataAndType(Uri.parse(vids[id]), "application/octet-stream");
					startActivity(i);
					finish();
				}
			})
			.show();
	}
	
	private static boolean isVideo(String url) {
		for (String suffix : VIDEO_SUFFIXES) {
			if (url.contains(suffix)) {
				return true;
			}
		}
		
		return false;
	}

	class MyJavascriptInterface {
		private final String TAG = MyJavascriptInterface.class.getSimpleName();
		
		private static final String PATTERN = "[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][&][;]]*";
		private static final String PATTERN_HTML5_VIDEO = "<video src=\"(.*?)\"";
		
		@JavascriptInterface
		public void processHTML(String html) {
			Pattern pattern = Pattern.compile(PATTERN);
			Matcher matcher = pattern.matcher(html);
			
			final ArrayList<String> vid = new ArrayList<String>();
			
			while (matcher.find()) {
				String url = matcher.group().replace("&amp;", "&");
				
				if (isVideo(url)) {
					
					vid.add(url);
					
					if (DEBUG) {
						Log.d(TAG, "found url:" + url);
					}
				}
			}
			
			// HTML5 videos
			pattern = Pattern.compile(PATTERN_HTML5_VIDEO);
			matcher = pattern.matcher(html);
			while (matcher.find()) {
				String url = matcher.group(1).replace("&amp;", "&");
				
				if (!vid.contains(url)) {
					vid.add(url);
				}
			}
			
			// Try detecting with api utils
			mWeb.post(new Runnable() {
				@Override
				public void run() {
					if (vid.size() == 0) {
						Toast.makeText(BrowserActivity.this, R.string.msg_no_video, Toast.LENGTH_SHORT).show();
					} else {
						String[] arr = new String[vid.size()];
						showVideoChoices(vid.toArray(arr));
					}
				}
			});
			
		}
	}
}
