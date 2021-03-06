package com.ch.epw.jz.fragment;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ch.epw.utils.Constant;
import com.ch.epw.utils.UIHelper;
import com.ch.epw.widget.pulltorefresh.PullToRefreshBase;
import com.ch.epw.widget.pulltorefresh.PullToRefreshListView;
import com.ch.epw.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.zhizun.pos.AppException;
import com.zhizun.pos.R;
import com.zhizun.pos.adapter.ListViewNoticeReceiveBoxParentAdapter;
import com.zhizun.pos.app.AppContext;
import com.zhizun.pos.base.BaseFragment;
import com.zhizun.pos.bean.NoticeBox;
import com.zhizun.pos.bean.NoticeBoxList;

/**
 * 通知公告 家长端 创建人：李林中 创建日期：2014-12-15 上午10:45:29 作用： 修改
 * =================================================== 修改人 修改日期 原因(描述)
 * ===================================================
 */
public class TongzhigonggaoFragment extends BaseFragment {
	private final static String TAG = TongzhigonggaoFragment.class.getName();
	Activity activity;
	Integer dataCount;// 总数量 共用字段
	NoticeBoxList noticeBoxList;
	List<NoticeBox> list;
	NoticeBox noticeBox;
	ListViewNoticeReceiveBoxParentAdapter listViewNoticeReceiveBoxParentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

	private void contentList() {
		// 下拉刷新和上啦加载代码开始=------
		// 下拉刷新控件
		Log.i("tag", "mPullListView=" + mPullListView);
		// 上拉加载不可用
		mPullListView.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		mPullListView.setScrollLoadEnabled(true);
		// 得到真正的listview
		mListView = mPullListView.getRefreshableView();
		mListView.setDivider(getResources().getDrawable(R.drawable.itembg));
		mListView.setDividerHeight(25);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				list = null;
				mCurPage = 1;
				hasMoreData = true;
				mPullListView.setHasData(true);
				new QueryInBoxListTask().execute(1 + "", Constant.LOADDATACOUNT
						+ "");
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {

				int page = (int) (Math.ceil(dataCount
						/ (double) Constant.LOADDATACOUNT));

				mCurPage = mCurPage + 1;
				Log.i(TAG, "mCurPage=" + mCurPage);
				if (mCurPage > page) {
					mCurPage = page;
					hasMoreData = false;
					mPullListView.setHasMoreData(hasMoreData);
					return;
				}

				Log.i(TAG, "mCurPage2=" + mCurPage);
				Log.i(TAG, "dataCount=" + dataCount);
				Log.i(TAG, "page=" + page);
				new QueryInBoxListTask().execute(mCurPage + "",
						Constant.LOADDATACOUNT + "");
			}
		});
		setLastUpdateTime();
		// 进入自动刷新
		mPullListView.doPullRefreshing(true, 500);

		// 下拉刷新和上啦加载代码结束=------
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(activity, R.layout.list_tzgg_jz, null);
		mPullListView = (PullToRefreshListView) view
				.findViewById(R.id.ll_activity_tzgg_parent);
		contentList();
		return view;
	}

	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 获取动态 创建人：李林中 创建日期：2014-12-9 上午10:42:07 作用： 修改
	 * =================================================== 修改人 修改日期 原因(描述)
	 * ===================================================
	 */
	private class QueryInBoxListTask extends
			AsyncTask<String, Void, NoticeBoxList> {
		AppException e;

		// 执行这个方法 此方法重新开启一个来执行网络接口读数据的
		// 操作，将得到的结果List<Preferential>，传送给onPostExecute方法
		@Override
		protected NoticeBoxList doInBackground(String... params) {

			try {
				noticeBoxList = AppContext.getApp().queryInBoxList(params[0],
						params[1]);

			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.e = e;
				noticeBoxList = null;
			}
			return noticeBoxList;
		}

		// 这个方法可以执行UI线程里操作界面的东西，类似于new handler里面的东东
		@Override
		protected void onPostExecute(NoticeBoxList result) {

			super.onPostExecute(result);
			if (result != null) {

				if (result.getStatus().equals("0")) {
					dataCount = Integer.parseInt(noticeBoxList.getDataCount());
					if (dataCount == 0) {
						mPullListView.setHasData(false);
						mListView.setDividerHeight(0);
					}else if (dataCount == 1) {
						mListView.setDivider(null);
						mListView.setDividerHeight(0);
					}
					if (list != null && list.size() > 0) {
						list.addAll(result.getNoticeBoxList());
						listViewNoticeReceiveBoxParentAdapter
								.notifyDataSetChanged();
					} else {
						list = result.getNoticeBoxList();
						listViewNoticeReceiveBoxParentAdapter = new ListViewNoticeReceiveBoxParentAdapter(
								activity, list);
						// 得到实际的ListView
						mListView
								.setAdapter(listViewNoticeReceiveBoxParentAdapter);
					}

				} else {
					UIHelper.ToastMessage(activity, result.getStatusMessage());
					return;
				}
			} else {
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				if (null != e) {
					e.makeToast(activity);
				}
				return;
			}

			mPullListView.onPullDownRefreshComplete();
			mPullListView.onPullUpRefreshComplete();
			mPullListView.setHasMoreData(hasMoreData);
			setLastUpdateTime();
		}

	}

}
