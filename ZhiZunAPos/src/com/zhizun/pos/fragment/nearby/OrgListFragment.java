package com.zhizun.pos.fragment.nearby;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;

import com.baidu.location.BDLocation;
import com.ch.epw.helper.MyPullToRefreshListHelper;
import com.ch.epw.task.QueryCourseListTask;
import com.ch.epw.task.TaskCallBack;
import com.ch.epw.utils.LocationUtils;
import com.ch.epw.widget.pulltorefresh.PullToRefreshListView;
import com.zhizun.pos.R;
import com.zhizun.pos.activity.OrgIntroDetailActivity;
import com.zhizun.pos.activity.SearchNearbyHotsActivity;
import com.zhizun.pos.activity.CourseListViewActivity.SearchParaEnum;
import com.zhizun.pos.adapter.OrgListViewAdapter;
import com.zhizun.pos.base.BaseBean;
import com.zhizun.pos.bean.CourseListItemList;
import com.zhizun.pos.bean.CourseListViewBean;
/**
 * 
 * 附近	课程  机构
 * 2015-7-8下午5:22:08
 */
public class OrgListFragment extends Fragment {
	private final static String TAG = OrgListFragment.class.getName();
	Activity activity;

	protected ListView mListView;
	protected PullToRefreshListView mPullListView;
	private ArrayList<CourseListItemList> listItems;

	private RadioButton radiobutton_org;
	
	private View rootView;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(rootView == null){
			rootView = inflater.inflate(R.layout.course_list_fragment, null);
			mPullListView = (PullToRefreshListView) rootView.findViewById(R.id.ll_course_nearby);
			radiobutton_org = (RadioButton) activity.findViewById(R.id.radiobutton_org);
			initView();
		}
		
		ViewGroup parent = (ViewGroup)rootView.getParent();
		if(parent != null){
			parent.removeView(rootView);
		}
		
		return rootView;
	}

	private void initView() {
		listItems = new ArrayList<CourseListItemList>();

		// 将Activity、mPullListView、listItems绑定
		final MyPullToRefreshListHelper mvHelper = new MyPullToRefreshListHelper(
				activity, mPullListView, listItems);
		mListView = mPullListView.getRefreshableView();
		mListView.setDivider(getResources().getDrawable(R.drawable.itembg));
		mListView.setDividerHeight(8);
		mListView.setAdapter(new OrgListViewAdapter(activity, listItems));

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object item = mListView.getAdapter().getItem(position);
				if (item != null) {
					CourseListItemList course = (CourseListItemList) item;
					Intent intent = new Intent(activity, OrgIntroDetailActivity.class);
					intent.putExtra("orgId", course.getOrgId());
					intent.putExtra("category", course.getCategory());
					startActivity(intent);
					activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
			}
		});

		final TaskCallBack getDataCallBack = new TaskCallBack() {
			@Override
			public void onTaskFinshed(BaseBean result) {
				CourseListViewBean courseList = (CourseListViewBean) result;
				if (courseList != null && "0".equals(courseList.getStatus())) {
					mvHelper.setPageCount(courseList.getDataCount());
					if(Integer.parseInt(courseList.getDataCount()) > 0){
						listItems.addAll(courseList.getCourseItemList());
					}
					radiobutton_org.setText("找机构（"+courseList.getDataCount()+"）");
				}
				// 必须在每次task执行结束调用mvController.refreshEnd()
				mvHelper.refreshEnd();
			}
		};

		// 调用QueryCourseListTask时传递的参数列表
		String[] searchParams = new String[SearchParaEnum.values().length];
		BDLocation location = LocationUtils.getLastKnownLocation();
		if(location != null){
			searchParams[SearchParaEnum.lat.ordinal()] = String.valueOf(location.getLatitude());
			searchParams[SearchParaEnum.lng.ordinal()] = String.valueOf(location.getLongitude());
			searchParams[SearchParaEnum.sort.ordinal()] = "distance:1";
			searchParams[SearchParaEnum.filter.ordinal()] = "distance:0,"+SearchNearbyHotsActivity.DEF_SEARCH_RADIUS;
		}
		searchParams[SearchParaEnum.searchType.ordinal()] = "1";
		mvHelper.setGetDataCallBack(QueryCourseListTask.class, searchParams,
				getDataCallBack);
		mvHelper.readyForRefresh();
	}

}
