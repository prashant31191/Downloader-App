package com.azdownloader.ui.fragment;

import com.azdownloader.get.DownloadManager;
import com.azdownloader.service.DownloadManagerService;

public class AllMissionsFragment extends MissionsFragment
{

	@Override
	protected DownloadManager setupDownloadManager(DownloadManagerService.DMBinder binder) {
		return binder.getDownloadManager();
	}
}
