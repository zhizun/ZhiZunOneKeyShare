package com.zhizun.pos.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ch.epw.bean.send.CommentsSend;
import com.ch.epw.task.DeleteCommentTask;
import com.ch.epw.task.DeleteCommonFsiTask;
import com.ch.epw.task.SendCommentsTask;
import com.ch.epw.task.TaskCallBack;
import com.ch.epw.utils.BaseTools;
import com.ch.epw.utils.Constant;
import com.ch.epw.utils.StringUtils;
import com.ch.epw.utils.UIHelper;
import com.ch.epw.utils.URLs;
import com.ch.epw.view.NoScrollGridView;
import com.ch.epw.view.NoScrollListView;
import com.zhizun.pos.R;
import com.zhizun.pos.activity.ImageShowActivity;
import com.zhizun.pos.app.AppContext;
import com.zhizun.pos.base.MyBaseAdapter;
import com.zhizun.pos.bean.Comments;
import com.zhizun.pos.bean.Like;
import com.zhizun.pos.bean.Photo;
import com.zhizun.pos.bean.Remark;
import com.zhizun.pos.bean.StudentClass;
import com.zhizun.pos.bean.StudentInfo;
import com.zhizun.pos.bean.UserInfo;
import com.zhizun.pos.widget.actionsheet.ActionSheet;

/**
 * 在校点评 教师端ListViewAdapter
 * 
 * @param <index2>
 */
public class ListViewRemarkTeacherAdapter extends MyBaseAdapter {

	public static final String TAG = ListViewRemarkTeacherAdapter.class
			.getName();
	private Context context;// 运行上下文
	private List<Remark> listItems; // 数据集合

	public ListViewRemarkTeacherAdapter(Context context, List<Remark> listItems) {
		super();
		this.context = context;
		this.listItems = listItems;
	}

	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	protected void setFocusOn(Remark remark) {
		if (remark == null) {
			return;
		}
		remark.setFoucsOn(true);
		if (listItems.size() > 1) {
			for (Remark dyn : listItems) {
				if (!dyn.getRemarkId().equals(remark.getRemarkId())) {
					dyn.setFoucsOn(false);
				}
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.list_zxdp_js_item,
					null);
			holder.tv_list_jtzy_js_item_title_sender = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_title_sender);
			holder.ll_list_zxdp_js_item_starlist = (NoScrollListView) convertView
					.findViewById(R.id.ll_list_zxdp_js_item_starlist);
			holder.ngv_list_zxdt_js_item_imagelist = (NoScrollGridView) convertView
					.findViewById(R.id.ngv_list_zxdt_js_item_imagelist);

			holder.tv_list_jtzy_js_item_title_sendtime = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_title_sendtime);

			holder.tv_notice_receivebox_detail_title_receiversinfo = (TextView) convertView
					.findViewById(R.id.tv_notice_receivebox_detail_title_receiversinfo);
			holder.tv_list_jtzy_js_item_title_btnishow = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_title_btnishow);

			holder.ll_list_jtzy_js_item_commentlist = (NoScrollListView) convertView
					.findViewById(R.id.ll_list_jtzy_js_item_commentlist);

			holder.tv_list_jtzy_js_item_content = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_content);
			holder.tv_list_jtzy_js_item_title_receivecount = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_title_receivecount);
			holder.tv_list_jtzy_js_item_title_short = (TextView) convertView
					.findViewById(R.id.tv_list_jtzy_js_item_title_short);

			holder.tv_list_js_ssh_commentcount = (TextView) convertView
					.findViewById(R.id.tv_list_js_ssh_commentcount);

			holder.et_list_common_pinglun_reply = (EditText) convertView
					.findViewById(R.id.et_list_common_pinglun_reply);
			holder.btn_list_common_pinglun_send = (Button) convertView
					.findViewById(R.id.btn_list_common_pinglun_send);
			holder.ll_list_zxdt_js_ssh_delete = (LinearLayout) convertView
					.findViewById(R.id.ll_list_zxdt_js_ssh_delete);
			holder.ll_list_zxdt_js_ssh_share = (LinearLayout) convertView
					.findViewById(R.id.ll_list_zxdt_js_ssh_share);
			holder.ll_list_zxdt_js_ssh_comment = (LinearLayout) convertView
					.findViewById(R.id.ll_list_zxdt_js_ssh_comment);

			holder.ll_list_commom_zan = (LinearLayout) convertView
					.findViewById(R.id.ll_list_commom_zan);
			holder.tv_list_common_zan = (TextView) convertView
					.findViewById(R.id.tv_list_common_zan);
			holder.tv_list_common_zan_count = (TextView) convertView
					.findViewById(R.id.tv_list_common_zan_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.resetContentAndListener();

		final Remark remark = listItems.get(position);
		final String loginUserId = AppContext.getApp().getUserLoginSharedPre()
				.getUserInfo().getUserId();
		final String sendUserId = remark.getUserId();

		if (null != remark.getSendTime() && !remark.getSendTime().equals("")) {// 已发送
			holder.tv_list_jtzy_js_item_title_sendtime.setText("发送时间："
					+ remark.getSendTime());
		} else {
			holder.tv_list_jtzy_js_item_title_sendtime.setText("发送时间：");
		}
		if (null != remark.getUserName() && !remark.getUserName().equals("")) {//
			holder.tv_list_jtzy_js_item_title_sender.setText("发送人："
					+ remark.getUserName());
		} else {
			holder.tv_list_jtzy_js_item_title_sender.setText("发送人：");
		}
		if (null != remark.getContent() && !remark.getContent().equals("")) {
			holder.tv_list_jtzy_js_item_content.setVisibility(View.VISIBLE);
			holder.tv_list_jtzy_js_item_content.setText(remark.getContent());
			holder.tv_list_jtzy_js_item_content.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					BaseTools.copyText(context, holder.tv_list_jtzy_js_item_content.getText().toString());
					return false;
				}
			});
		} else {
			holder.tv_list_jtzy_js_item_content.setVisibility(View.GONE);
		}
		if (null != remark.getReceiverCount()
				&& !remark.getReceiverCount().toString().trim().equals("")) {
			holder.tv_list_jtzy_js_item_title_receivecount.setText(" 共"
					+ remark.getReceiverCount() + "人");
		} else {
			holder.tv_list_jtzy_js_item_title_receivecount.setText("");
		}

		StringBuilder text = new StringBuilder();
		List<String> recvListForShort = new ArrayList<String>();
		ArrayList<StudentClass> recvList = remark.getStudentClassesList();
		for (StudentClass studentClass : recvList) {
			text.append("<font color='#818181'>" + studentClass.getName()
					+ "</font><br>");
			List<StudentInfo> stuList = studentClass.getStudentInfoList();
			for (StudentInfo studentInfo : stuList) {
				text.append(studentInfo.getName() + " ");
				if (recvListForShort.size() < Constant.FSI_RECVERS_FOR_SHORT_LEN) {
					recvListForShort.add(studentInfo.getName());
				}
			}
			text.append("<br>");
		}
		holder.tv_notice_receivebox_detail_title_receiversinfo.setText(Html
				.fromHtml(text.toString()));
		if (recvListForShort.size() > 0) {
			holder.tv_list_jtzy_js_item_title_short.setText(StringUtils
					.listToString(recvListForShort)+"...");
		} else {
			holder.tv_list_jtzy_js_item_title_short.setText("");
		}
		holder.tv_list_jtzy_js_item_title_btnishow
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (holder.tv_list_jtzy_js_item_title_btnishow
								.getText()
								.toString()
								.equals(context.getResources().getString(
										R.string.show_more))) {
							holder.tv_list_jtzy_js_item_title_btnishow
									.setText(R.string.hide_more);
							holder.tv_notice_receivebox_detail_title_receiversinfo
									.setVisibility(View.VISIBLE);
							holder.tv_list_jtzy_js_item_title_short
									.setVisibility(View.GONE);

						} else {
							holder.tv_list_jtzy_js_item_title_btnishow
									.setText(R.string.show_more);
							holder.tv_notice_receivebox_detail_title_receiversinfo
									.setVisibility(View.GONE);
							holder.tv_list_jtzy_js_item_title_short
									.setVisibility(View.VISIBLE);
						}

					}
				});
		if (null != remark.getCommentCount()
				&& !remark.getCommentCount().toString().trim().equals("")) {
			holder.tv_list_js_ssh_commentcount.setText("（"
					+ remark.getCommentCount() + "）");
		}
		if (remark.getRatingList() != null && remark.getRatingList().size() > 0) {
			ListViewRatingAdapter listViewRatingAdapter = new ListViewRatingAdapter(
					context, remark.getRatingList());
			holder.ll_list_zxdp_js_item_starlist
					.setAdapter(listViewRatingAdapter);
		} else {
			holder.ll_list_zxdp_js_item_starlist.setAdapter(null);
		}
		if (remark.getPhotoList() != null && remark.getPhotoList().size() > 0) {
			GridViewImagesAdapter listViewImagesAdapter = new GridViewImagesAdapter(
					context, remark.getPhotoList());
			holder.ngv_list_zxdt_js_item_imagelist.setVisibility(View.VISIBLE);
			holder.ngv_list_zxdt_js_item_imagelist
					.setAdapter(listViewImagesAdapter);
		} else {
//			holder.ngv_list_zxdt_js_item_imagelist.setAdapter(null);
			holder.ngv_list_zxdt_js_item_imagelist.setVisibility(View.GONE);
		}
		holder.ngv_list_zxdt_js_item_imagelist
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ArrayList<String> imgsUrl = new ArrayList<String>();
						for (Photo photo : remark.getPhotoList()) {
							imgsUrl.add(URLs.URL_IMG_API_HOST
									+ photo.getThumbPath() + "/"
									+ photo.getThumbSaveName());
						}
						Intent intent = new Intent();
						intent.putStringArrayListExtra("infos", imgsUrl);
						intent.putExtra("imgPosition", position);
						intent.setClass(context, ImageShowActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
		if (remark.getLikeList() != null && remark.getLikeList().size() > 0) {
			List<Like> list = remark.getLikeList();

			StringBuffer sBuffer = new StringBuffer();
			for (Like like : list) {
				sBuffer.append(like.getUserAppe());
				sBuffer.append(",");
			}
			String str = sBuffer.toString();
			if (str.endsWith(",")) {
				str = str.substring(0, str.length() - 1);
			}
			holder.ll_list_commom_zan.setVisibility(View.VISIBLE);
			holder.tv_list_common_zan.setText(str);
			holder.tv_list_common_zan_count.setText(" 共" + list.size() + "人");

		} else {
			holder.ll_list_commom_zan.setVisibility(View.GONE);
		}
		if (remark.getCommentCount() > 0) {

			final ListViewCommentsAdapter commentsAdapter = new ListViewCommentsAdapter(
					context, remark.getCommentsList());
			holder.ll_list_jtzy_js_item_commentlist.setAdapter(commentsAdapter);

			holder.ll_list_jtzy_js_item_commentlist
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, final int position, long id) {
							final Comments comment = (Comments) parent
									.getAdapter().getItem(position);
							String commentUser = comment.getUserId();
							if (loginUserId != null
									&& loginUserId.equals(commentUser)) {
								ActionSheet
										.showSheet(
												context,"","",
												new ActionSheet.OnActionSheetSelected() {
													@Override
													public void onClick(
															View view) {
														if (view.getId() == R.id.actionsheet_content) {
															new DeleteCommentTask(
																	context,
																	new TaskCallBack() {
																		@Override
																		public void onTaskFinshed() {
																			remark.getCommentsList()
																					.remove(comment);
																			ListViewRemarkTeacherAdapter.this
																					.notifyDataSetChanged();
																		}
																	})
																	.execute(
																			AppContext
																					.getApp()
																					.getToken(),
																			comment.getCommentID());
														}
													}
												}, null);
							} else {
								remark.setReferComment(comment);
								holder.et_list_common_pinglun_reply
										.requestFocus();
								holder.et_list_common_pinglun_reply
										.setHint("回复 " + comment.getUserAppe());
								setFocusOn(remark);
							}
						}
					});
		} else {
			holder.ll_list_jtzy_js_item_commentlist.setAdapter(null);
		}

		// 根据登录用户是否是发表点评用户确定是否显示删除按钮
		if (loginUserId != null && loginUserId.equals(sendUserId)) {
			holder.ll_list_zxdt_js_ssh_delete.setVisibility(View.VISIBLE);
			holder.ll_list_zxdt_js_ssh_delete
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							ActionSheet.showSheet(context,"","",
									new ActionSheet.OnActionSheetSelected() {
										@Override
										public void onClick(View view) {
											if (view.getId() == R.id.actionsheet_content) {
												new DeleteCommonFsiTask(
														Constant.COMMNETTYPE_ZXDP,
														context,
														new TaskCallBack() {
															@Override
															public void onTaskFinshed() {
																listItems
																		.remove(position);
																ListViewRemarkTeacherAdapter.this
																		.notifyDataSetChanged();
															}
														}).execute(AppContext
														.getApp().getToken(),
														remark.getRemarkId());
											}
										}
									}, null);

						}
					});
		} else {
			holder.ll_list_zxdt_js_ssh_delete.setVisibility(View.INVISIBLE);
		}

		holder.ll_list_zxdt_js_ssh_share
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String imgUrlString = "";
						if (remark.getPhotoList().size() > 0) {
							imgUrlString = URLs.formatImgURL(remark.getPhotoList().get(0));
						}
						AppContext.getApp().showShare(context, 
								remark.getOrgId(),
								remark.getRemarkId(), 
								Constant.COMMNETTYPE_ZXDP,
								remark.getContent(),
								imgUrlString);
					}
				});

		// 点击评论按钮，清除评论对象
		holder.ll_list_zxdt_js_ssh_comment
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						remark.resetInputStatus();
						setFocusOn(remark);
						holder.et_list_common_pinglun_reply.requestFocus();
						holder.et_list_common_pinglun_reply.setText("");
						holder.et_list_common_pinglun_reply
								.setHint(R.string.text_comment_default_prompt);
					}
				});

		holder.btn_list_common_pinglun_send
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String contentString = holder.et_list_common_pinglun_reply
								.getText().toString().trim();

						if (null == contentString || contentString.equals("")) {
							UIHelper.ToastMessage(context, "发送的内容不能为空！");
							return;
						}
						CommentsSend commentsSend = new CommentsSend();
						Comments referComment = remark.getReferComment(); // 引用的comment
						if (null == referComment) {
							commentsSend.setContent(contentString);
							commentsSend.setRefId(remark.getRemarkId());
							commentsSend.setType(Constant.COMMNETTYPE_ZXDP);
							commentsSend.setReplyCommentId("");
							commentsSend.setTargetUserId(remark.getUserId());
							commentsSend.setToken(AppContext.getApp()
									.getToken());
						} else {
							commentsSend.setContent(contentString);
							commentsSend.setRefId(remark.getRemarkId());
							commentsSend.setReplyCommentId(referComment
									.getCommentID());
							commentsSend.setTargetUserId(remark
									.getUserId());
							commentsSend.setType(Constant.COMMNETTYPE_ZXDP);
							commentsSend.setToken(AppContext.getApp()
									.getToken());
						}
						holder.et_list_common_pinglun_reply.setText("");
						holder.et_list_common_pinglun_reply
								.setHint(R.string.text_comment_default_prompt);
						new SendCommentsTask(remark.getCommentsList(), context,
								new TaskCallBack() {
									@Override
									public void onTaskFinshed() {
										remark.resetInputStatus();
										ListViewRemarkTeacherAdapter.this
												.notifyDataSetChanged();
									}
								}).execute(commentsSend);
						((InputMethodManager) context
								.getSystemService(Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										holder.et_list_common_pinglun_reply
												.getWindowToken(), 0);
					}
				});

		// 获得焦点时保存当前位置
		holder.et_list_common_pinglun_reply
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_UP) {
							setFocusOn(remark);
						}
						return false;
					}
				});

		// 输入完成时，保存当前的输入内容
		holder.et_list_common_pinglun_reply
				.addTextChangedListener(new TextWatcher() {
					@Override
					public void afterTextChanged(Editable s) {
						String typeingComment = holder.et_list_common_pinglun_reply
								.getText().toString();
						if (typeingComment.length() > Constant.COMMENT_MAX_LEN) {
							typeingComment = typeingComment.substring(0,
									Constant.COMMENT_MAX_LEN);
							UIHelper.ToastMessage(context, "输入的内容不能大于"
									+ Constant.COMMENT_MAX_LEN + "个字符");
							holder.et_list_common_pinglun_reply
									.setText(typeingComment);
						}
						if (remark.isFoucsOn()) {
							remark.setTypeingComment(typeingComment);
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}
				});

		// 如果当前动态动态正引用一条评论回复
		if (remark.getReferComment() != null
				&& remark.getReferComment().getUserAppe() != null) {
			holder.et_list_common_pinglun_reply.setHint("回复 "
					+ remark.getReferComment().getUserAppe());
		}

		// 如果有保存的数据，回复输入未提交的内容
		if (remark.getTypeingComment() != null
				&& !remark.getTypeingComment().equals("")) {
			holder.et_list_common_pinglun_reply.setText(remark
					.getTypeingComment());
		} else {
			holder.et_list_common_pinglun_reply.setText("");
			holder.et_list_common_pinglun_reply
					.setHint(R.string.text_comment_default_prompt);
		}

		// 如果是由于输入框输入法键盘导致的页面重绘，直接获取焦点；对于其他导致的重绘，重置各参数
		if (remark.isFoucsOn()) {
			// 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
			holder.et_list_common_pinglun_reply.requestFocus();
			if (holder.et_list_common_pinglun_reply.getText().length() > 0) {
				holder.et_list_common_pinglun_reply
						.setSelection(holder.et_list_common_pinglun_reply
								.getText().length());
			}
		}

		return convertView;
	}

	static class ViewHolder {

		TextView tv_list_jtzy_js_item_title_sender;// 发送人

		TextView tv_list_jtzy_js_item_title_sendtime;// 发送时间
		TextView tv_list_jtzy_js_item_title_receivecount;// 共有多少接收人
		TextView tv_list_jtzy_js_item_title_short;// 接收人

		TextView tv_list_jtzy_js_item_title_btnishow;// 更多
		TextView tv_notice_receivebox_detail_title_receiversinfo;// 接受人详细
		TextView tv_list_jtzy_js_item_content;// 作业内容
		TextView tv_list_js_ssh_commentcount;// 评论数量
		LinearLayout ll_list_zxdt_js_ssh_delete;// 删除动态
		LinearLayout ll_list_zxdt_js_ssh_share;// 分享
		LinearLayout ll_list_zxdt_js_ssh_comment;// 评论

		NoScrollListView ll_list_jtzy_js_item_commentlist;// 评论列表
		NoScrollListView ll_list_zxdp_js_item_starlist;// 评分列表
		NoScrollGridView ngv_list_zxdt_js_item_imagelist;// 图片列表
		EditText et_list_common_pinglun_reply;// 回复；评论
		Button btn_list_common_pinglun_send;// 发送评论

		LinearLayout ll_list_commom_zan;//
		TextView tv_list_common_zan;
		TextView tv_list_common_zan_count;// 共多少人赞过

		protected void resetContentAndListener() {
			if (tv_list_jtzy_js_item_title_sendtime != null) {
				tv_list_jtzy_js_item_title_sendtime.setText("");
			}
			if (tv_list_jtzy_js_item_title_sender != null) {
				tv_list_jtzy_js_item_title_sender.setText("");
			}
			if (tv_list_jtzy_js_item_title_receivecount != null) {
				tv_list_jtzy_js_item_title_receivecount.setText("");
			}
			if (tv_list_jtzy_js_item_title_short != null) {
				tv_list_jtzy_js_item_title_short.setText("");
			}
			if (tv_list_jtzy_js_item_title_btnishow != null) {
				tv_list_jtzy_js_item_title_btnishow.setOnClickListener(null);
			}
			if (ngv_list_zxdt_js_item_imagelist != null) {
				ngv_list_zxdt_js_item_imagelist.setOnItemClickListener(null);
				ngv_list_zxdt_js_item_imagelist.setAdapter(null);
			}
			if (ll_list_jtzy_js_item_commentlist != null) {
				ll_list_jtzy_js_item_commentlist.setOnItemClickListener(null);
				ll_list_jtzy_js_item_commentlist.setAdapter(null);
			}
			if (ll_list_zxdp_js_item_starlist != null) {
				ll_list_zxdp_js_item_starlist.setOnItemClickListener(null);
				ll_list_zxdp_js_item_starlist.setAdapter(null);
			}
			if (ll_list_zxdt_js_ssh_delete != null) {
				ll_list_zxdt_js_ssh_delete.setOnClickListener(null);
			}
			if (ll_list_zxdt_js_ssh_share != null) {
				ll_list_zxdt_js_ssh_share.setOnClickListener(null);
			}
			if (btn_list_common_pinglun_send != null) {
				btn_list_common_pinglun_send.setOnClickListener(null);
			}
		}

	}

}
