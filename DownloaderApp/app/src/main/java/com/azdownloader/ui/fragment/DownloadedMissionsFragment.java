package com.azdownloader.ui.fragment;

import com.azdownloader.get.DownloadManager;
import com.azdownloader.get.FilteredDownloadManagerWrapper;
import com.azdownloader.service.DownloadManagerService;

public class DownloadedMissionsFragment extends MissionsFragment
{
	@Override
	protected DownloadManager setupDownloadManager(DownloadManagerService.DMBinder binder) {
		return new FilteredDownloadManagerWrapper(binder.getDownloadManager(), true);
	}
}
