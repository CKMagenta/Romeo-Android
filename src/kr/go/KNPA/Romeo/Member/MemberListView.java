package kr.go.KNPA.Romeo.Member;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Util.IndexPath;
import kr.go.KNPA.Romeo.Util.IndexPath.Iterator;


import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MemberListView extends ListView {

	public Department rootDepartment = null;
	public MemberListView(Context context) {
		this(context, null);
	}

	public MemberListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MemberListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if(this.rootDepartment == null) {
			MemberManager.sharedManager().getMembers();
			this.rootDepartment = Department.root();
		}	
		
		
		MemberListAdapter adapter = new MemberListAdapter();
		this.setOnItemClickListener(adapter);
		this.setAdapter(adapter);
	}
	

	private class MemberListAdapter extends BaseAdapter implements OnItemClickListener {
		
		private CellNode models = new CellNode();
		
		public MemberListAdapter() {
			ArrayList<Department> deps = rootDepartment.departments;

			models.isRoot = true;
			models.setUnfolded(true);
			for(int i=0; i<deps.size(); i++) {
				CellNode node = new CellNode.Builder().unfolded(true)
													  .type(CellNode.CELLNODE_DEPARTMENT)
													  .parentIndexPath(null)
													  .index(i)
													  .unfolded(false)
													  .build();
				models.add(node);
			}
		}
		
		@Override
		public int getCount() {
			return models.count();
		}

		@Override
		public Object getItem(int position) {
			IndexPath path = getIndexPathFromPosition(position);
			return objectForRowAtIndexPath(path);
		}

		@Override
		public long getItemId(int position) {
			return getIndexPathFromPosition(position).indexPathToLong();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			IndexPath path = getIndexPathFromPosition(position);

			Object model = objectForRowAtIndexPath(path);
			
			Department department = null;
			User user = null;
			
			CellNode node = CellNode.nodeAtIndexPath(models, path);
			
			// TODO : cell Reusing source
			if(node.type == CellNode.CELLNODE_DEPARTMENT) {
				department = (Department)model;
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_department_cell, parent, false);
				TextView titleTV = (TextView)convertView.findViewById(R.id.title);
				titleTV.setText(department.title);
				
			} else if (node.type == CellNode.CELLNODE_USER) {
				user = (User)model;
				int uDepartment = user.department;
				long uIdx = user.idx;
				String[] uLevels = user.levels;
				String uName = user.name;
				int uPicIdx = user.pic;
				int uRank = user.rank;
				long uTS = user.TS;
				
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_user_cell, parent, false);
				TextView rankTV = (TextView)convertView.findViewById(R.id.rank);
				rankTV.setText(User.RANK[uRank]);
				TextView nameTV = (TextView)convertView.findViewById(R.id.name);
				nameTV.setText(uName);
				TextView roleTV = (TextView)convertView.findViewById(R.id.role);
				roleTV.setText("ROLE");
				TextView departmentTV = (TextView)convertView.findViewById(R.id.department);
				departmentTV.setText(""+uDepartment);
			}
			
			if(node.type == CellNode.CELLNODE_DEPARTMENT || node.type == CellNode.CELLNODE_USER) {
				final int IdtMargin = 16;
				ImageView siIV = (ImageView)convertView.findViewById(R.id.sub_indicator);
				LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) siIV.getLayoutParams();
				int lMargin = (int)(IdtMargin * getContext().getResources().getDisplayMetrics().density + 0.5f);
			    
				lp.setMargins(lp.leftMargin + lMargin*(path.length() -1), lp.topMargin, lp.rightMargin, lp.bottomMargin);
						//new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				
			    if(path.length() == 1) {
			    	siIV.setVisibility(INVISIBLE);
			    }
				siIV.setLayoutParams(lp);
			}	
			return convertView;
		}

		public int numberOfRowsInSection(IndexPath path) {
			return (numberOfFoldingRowsInSection(path)+numberOfPlainRowsInSection(path));
		}
		
		public int numberOfFoldingRowsInSection(IndexPath path) {
			return numberOfRowsOfTypeInSection(CellNode.CELLNODE_DEPARTMENT, path);
		}
		
		public int numberOfPlainRowsInSection(IndexPath path) {
			return numberOfRowsOfTypeInSection(CellNode.CELLNODE_USER, path);
		}
		
		public CellNode nodeForRowAtIndexPath(IndexPath path) {
			return CellNode.nodeAtIndexPath(models, path);
		}
		
		public int numberOfRowsOfTypeInSection(int type, IndexPath path) {
			int result = -1;
			
			CellNode node = nodeForRowAtIndexPath(path);
			int firstUserCellIndex = -1;
			
			for(int i=0; i< node.size(); i++) {
				if(node.get(i).type == CellNode.CELLNODE_USER) {
					firstUserCellIndex = i;
					break;
				}
			}
			
			if(type == CellNode.CELLNODE_DEPARTMENT) {
				result = firstUserCellIndex;
			} else if(type == CellNode.CELLNODE_USER) {
				result = node.size() - firstUserCellIndex;
			}
			return result;
		}
		
		public int nodeOrderInTypeWithIndexPath(int type, IndexPath path) {
			int result = -1;
			if(type == CellNode.CELLNODE_DEPARTMENT) {
				// Department 후에 Users가 나오므로, Department는 그대로 return해도 무방하다.
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				result = itr.lastIndex();
			} else if(type == CellNode.CELLNODE_USER) {
				IndexPath.Iterator itr = new IndexPath.Iterator(path);
				int lastIndex = itr.lastIndex();
				
				IndexPath parentPath = path.indexPathByRemovingLastIndex();
				CellNode parentNode = nodeForRowAtIndexPath(parentPath);
				int firstUserCellIndex = -1;
				for(int i=0; i< parentNode.size(); i++) {
					if(parentNode.get(i).type == CellNode.CELLNODE_USER) {
						firstUserCellIndex = i;
						break;
					}
				}
				
				result =  lastIndex - firstUserCellIndex;
			}
			return result;
		}
		private int getNodeTypeAtIndexPath(IndexPath path) {
			CellNode node = CellNode.nodeAtIndexPath(models, path);
			return node.type;
		}
		public Object objectForRowAtIndexPath(IndexPath path) {
			Object obj = null;
			
			int objectType = getNodeTypeAtIndexPath(path);
			
			int[] paths = path.getIndexes(null);
			int lastIndex = paths[(paths.length-1)];
			
			Department dep = rootDepartment;
			ArrayList<Department> deps = null;
			for(int i =0; i<(paths.length-1); i++) {
				int index = paths[i];
				deps = dep.departments;
				dep = deps.get(index);
			}

			int order = -1;
			if(objectType == CellNode.CELLNODE_DEPARTMENT) {
				order = nodeOrderInTypeWithIndexPath(CellNode.CELLNODE_DEPARTMENT, path);
				obj = (Object)dep.departments.get(order);
			} else if (objectType == CellNode.CELLNODE_USER) {
				order = nodeOrderInTypeWithIndexPath(CellNode.CELLNODE_USER, path);
				obj = (Object)dep.users.get(order);
			}
			
			return obj;
		}
		
//		public View cellForRowAtIndexPath(IndexPath path) {
//			return null;
//		}
		
//		public String titleForHeaderInSection() {
//			return null;
//		}
		
		@Override
		public int getViewTypeCount() 		{		return 	3;		}
		
		@Override
		public boolean areAllItemsEnabled() {		return 	true;	}
		
		public IndexPath getIndexPathFromPosition(int pos) {
			IndexPath path = null;
			
			
			int[] l = new int[IndexPath.MAX_LENGTH];
			for(int i=0; i<l.length; i++) {
				l[i] = -1;
			}
			
			CellNode cn = models;
			
			int cnt = 0;
			int li = 0;
			while( true ){
				CellNode _cn = cn.get(li).copy(); //
				
				
				int _cnt = cnt + _cn.count(); 
				
				if(_cnt >= (pos+1)) { // cnt = x, _cnt = x+y (x+1 ~ x+y) => group1 : 0~x-1, group2 : x ~ x+y-1
					// target is in this element tree
					if((cnt + 1) == (pos+1) ) { // cn.size() == 0;
						// 기존 cnt에 하나만 더한 것이 pos 값과 같다면, 현재 element를 선택한 것이다.
						// child가 존재했다면 _cnt > pos 였을 것이고, child가 존재하지 않았다면 _cnt == pos 였을 것이다.
						path = _cn.getIndexPath();
						break;
					} else {
						// 그게 아니라면, 하위 오브젝트를 선택한 것이므로,
						// go to children
						cn = _cn;
						_cn = null;
						li = 0;
						cnt = cnt+1;
					
					}
				} else if(_cnt < (pos+1)) {
					// go to next sibling
					li++;
					cnt = _cnt;
				}
				
			}
			
			return path;
		}
		
		public int getPositionFromIndexPath(IndexPath path) {
			int[] paths = path.getIndexes(null);
			int pos = -1;
			
			int cnt = 0;
			
			for(int li=0; li<paths.length; li++) {
				CellNode cn = models;
				int l = paths[li];
				
				
				for(int i=0; i< li; i++) {
					int _l = paths[li];
					cn = cn.get(_l);
				}
				
				for(int i=0; i < paths[l]; i++) {
					cnt += cn.get(i).count();
				}
			}
			
			pos = cnt;
			return pos;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long l_position) {
			// parent : AdapterView의 속성을 모두 사용할 수 있다.
			// view : 클릭한 row의 view
			// position : 클릭한 row의 position
			// l_position : 클릭한 row의 long Type의 position을 반환
			//String tv = (String)parent.getAdapter().getItem(position);
			//android.widget.Toast.makeText(getContext(), tv, android.widget.Toast.LENGTH_SHORT).show();
			//android.widget.Toast.makeText(getContext(), "" + position, android.widget.Toast.LENGTH_SHORT).show();
			
			// 클릭된 셀의 position을 이용하여 indexpath를 알아낸다.
			IndexPath path = getIndexPathFromPosition(position);
			IndexPath.Iterator itr = new IndexPath.Iterator(path);

			CellNode cn = models;
			// indexPath의 index들을 하나하나 따라간다.
			while(itr.hasNextIndex()) {
				Department dp = rootDepartment;
				int idx = itr.nextIndex();
				cn = cn.get(idx);
				
				if(cn.type == CellNode.CELLNODE_USER) {
				// node의 type이 USER이면 상세안내창 띄우기
					Intent intent = new Intent(getContext(), MemberDetailActivity.class);
					//intent.putExtra("KEY", VALUE);
					int userIdx = MemberDetailActivity.NOT_SPECIFIED;
					intent.putExtra("idx", 1);
					getContext().startActivity(intent);

				} else if(cn.type == CellNode.CELLNODE_DEPARTMENT) {
				// node의 type이 DEPARTMENT이면 
				
					if(cn.isUnfolded() == false) {
					// unfolded == false 이면
					// 안에 내용물이 있는지 확인하고,
						if(cn.size() > 0) {
						// 있다면 그냥 unfold
							cn.setUnfolded(true);
						} else {
						// 없다면 추가
						// 누른 것은 department이므로, indexpath를 이용하여 해당 아이템을 데이터로부터 가져온다.
							int[] untilNow = itr.getIndexesUntilNow();
							for(int i=0; i< untilNow.length; i++) {
								dp = dp.departments.get(untilNow[i]);
							}
						// 정보를 쭉 읽으며 node에 저장시키고, departments와 users..흐규흐규
							ArrayList<Department> deps = dp.departments;
							ArrayList<User> uss = dp.users;
							
							int indexForChildren = 0;
							for(int i=0; i<deps.size(); i++) {
								CellNode child = new CellNode.Builder().type(CellNode.CELLNODE_DEPARTMENT)
																	   .parentIndexPath(IndexPath.indexPathWithIndexesAndLength(untilNow, untilNow.length))
																	   .index(indexForChildren++)
																	   .unfolded(false)
																	   .build();
								cn.add(child);
							}
							
							for(int i=0; i<uss.size();i++) {
								CellNode child = new CellNode.Builder().type(CellNode.CELLNODE_USER)
																	   .parentIndexPath(IndexPath.indexPathWithIndexesAndLength(untilNow, untilNow.length))
																	   .index(indexForChildren++)
																	   .unfolded(false)
																	   .build();
								cn.add(child);
							}
							
							cn.setUnfolded(true);
						}
					} else if(cn.isUnfolded() == true && (!itr.hasNextIndex()) ){
					// unfolded == true이면,
					// fold 시킨다.
						cn.setUnfolded(false);
					// 일단 하위 트리는 굳이 제거하지 말자.
					}
				}
				// 한단계 안의 index에 대해서도반복
				
			}
			
			// view referesh
			this.notifyDataSetChanged();
			// TODO : register dataset??
		}
		
		/*
		 	ArrayList<Department> deps = rootDepartment.departments;

			for(int i=0; i<deps.size(); i++) {
				CellNode node = new CellNode.Builder().unfolded(true)
													  .type(CellNode.CELLNODE_DEPARTMENT)
													  .parentIndexPath(null)
													  .index(i)
													  .build();
				models.add(node);
		 */
	}
	
	
	static class CellNode extends ArrayList<CellNode> {

		private static final long serialVersionUID = 498188955518204141L;
		public static final int CELLNODE_NULL = -1;
		public static final int CELLNODE_USER = 1;
		public static final int CELLNODE_DEPARTMENT = 2;
		public boolean isRoot = false;
		
		public int type = CELLNODE_NULL;
		private boolean _unfolded;
		private int _index = -1;
		private IndexPath _parentIndexPath = null;
		
		
		public static class Builder {
			private int _type = CELLNODE_NULL;
			private boolean _unfolded;
			private int _index = -1;
			private IndexPath _parentIndexPath = null;
			
			public Builder type(int type) {
				this._type = type;
				return this;
			}
			public Builder unfolded(boolean unfolded) {
				this._unfolded = unfolded;
				return this;
			}
			public Builder index(int index) {
				this._index = index;
				return this;
			}
			public Builder parentIndexPath(IndexPath parentIndexPath) {
				this._parentIndexPath = parentIndexPath;
				return this;
			}
			public CellNode build() {
				CellNode node = new CellNode();
				node.type = this._type;
				node._unfolded = this._unfolded;
				node._index = this._index;
				node._parentIndexPath = this._parentIndexPath;
				return node;
				
			}
		}
		
		public CellNode copy() {
			CellNode newCellNode =  new CellNode.Builder().type(this.type)
										 .parentIndexPath(this._parentIndexPath)
										 .unfolded(this._unfolded)
										 .index(this._index)
										 .build();
			newCellNode.addAll(this);
			
			return newCellNode;
		}
		
		public static CellNode nodeAtIndexPath(CellNode rootNode, IndexPath path) {
			CellNode cn = rootNode;
			IndexPath.Iterator itr = new IndexPath.Iterator(path);
			
			while(itr.hasNextIndex()) {
				cn = cn.get(itr.nextIndex());
			}
			return cn;
		}

		
		public int countIncludeFolded() {
			int result = 0;
			for(int i=0; i< this.size(); i++) {
				result += this.get(i).countIncludeFolded();
			}
			return (result+1);
		}
		
		public int count() {
			int result = 0;
			
			if(isUnfolded() == true) {		  	// UnFolded
				for(int i=0; i< this.size(); i++) {
					result += this.get(i).count();
				}
			} else if(isUnfolded() == false) { 	// Folded
			//	result += 1;
			}
			
			
			if(isRoot != true) return result+1;
			return result;
		}
		
		public boolean isUnfolded() {
			return _unfolded;
		}
		
		public void setUnfolded(boolean unfolded) {
			_unfolded = unfolded;
		}
		
		public IndexPath getIndexPath() {
			int idx = _index;
			
			if(_parentIndexPath == null) {
				return IndexPath.indexPathWithIndex(_index);
			} else {
				return _parentIndexPath.indexPathByAddingIndex(idx);
			}
		}
		
		public void setParentIndexPath(IndexPath path) {
			_parentIndexPath = path;
		}
		public IndexPath getParentIndexPath() {
			return _parentIndexPath;
		}
		public void setIndex(int index) {
			_index = index;
		}
		
	}
	
	/*
	
	class CellModel<String, V> extends HashMap<String, V> {
		private static final long serialVersionUID = 7832721165457767737L;
		public static final int CELLMODEL_NULL = -1;
		public static final int CELLMODEL_USER = 1;
		public static final int CELLMODEL_GROUP = 2;
		
		
		private boolean _folded;
		private int _index = -1;
		private IndexPath _parentIndexPath = null;
		public int type = CELLMODEL_NULL;
		
		public boolean isFolded() {
			return _folded;
		}
		
		public IndexPath getIndexPath() {
			int idx = 0;
			if(!(_index < 0)) {
				idx = _index;
			}
			
			return _parentIndexPath.indexPathByAddingIndex(idx);
		}
		
		public void setParentIndexPath(IndexPath path) {
			_parentIndexPath = path;
		}
		
		public void setIndex(int index) {
			_index = index;
		}
		
		public int count() {
			int result = 0;
			
			Collection<?> coll = this.values();
			java.util.Iterator<?> itr =  coll.iterator();
			
			while(itr.hasNext()) {
				Object obj = (Object)itr.next();
				CellModelArray cma = null;
				if(obj instanceof CellModelArray) {
					cma = (CellModelArray)obj;
					
					int _temp = cma.count();
					if(_temp > 0) {
						result += _temp;
					} else {
						result += 0;
					}
				}
			}
			return (result+1);	// Cell을 의미하는 Object 자기 자신.
		}
	}
	*/
	/*
	
	class CellModelArray extends ArrayList<CellModel> {
		public static final int CELLMODEL_NULL = -1;
		public static final int CELLMODEL_USER = 1;
		public static final int CELLMODEL_GROUP = 2;
		
		public int type = CELLMODEL_NULL;
		
		private IndexPath _parentIndexPath = null;
		
		public void setParentIndexPath(IndexPath path) {
			_parentIndexPath = path;
		}
		public IndexPath getParentIndexPath() {
			return _parentIndexPath;
		}
		public int count() {
			int result = 0;
			for(int i=0; i< this.size(); i++) {
				result += this.get(i).count();
			}
			return result;
		}
	}
	*/
	
	/*
	private class MemberExpandableListAdapter extends BaseExpandableListAdapter {
		private ArrayList<String> groupList = null;
		private ArrayList<ArrayList<String>> childList = null;
		private LayoutInflater inflater = null;
		
		public MemberExpandableListAdapter(Context context, JSONObject data) {
			super();
			this.inflater = LayoutInflater.from(context);
			this.groupList = makeGroupList(data);
			this.childList = makeChildList(data);
			Log.v("MELA", "MELA!!");
		}
		
		private ArrayList<String> makeGroupList(JSONObject data) {
			ArrayList<String>list = new ArrayList<String>();
			

			
			
//			HashMap mDepartments = (HashMap)data.get("departments");
//			ArrayList<HashMap> aDepartments = (ArrayList<HashMap>) mDepartments.values();
//			Collections.sort(aDepartments, departmentComparator);
			
			//data는 DEP 준것이다. (안에는 departments와 users, title, sequece 이 있다.
//			for(int i=0; i< aDepartments.size(); i++) {
//				list.add((String)aDepartments.get(i).get("title"));
//			}
			
			JSONArray departments = null;		
			try {
				JSONObject _json = data.getJSONObject("departments");
				 departments = _json.toJSONArray(_json.names());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ArrayList<JSONObject> sortedDepartments = sortDepartments(departments);
			
			ListIterator<JSONObject> li = sortedDepartments.listIterator();
			while(li.hasNext()) {
				JSONObject jo = li.next();
				try {
					list.add(jo.getString("title"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
			
			
			return list;
		}
		
		private ArrayList<JSONObject> sortDepartments (JSONArray departments)  {
			ArrayList<JSONObject> sortedDepartments = new ArrayList<JSONObject>();
			
			for(int i=0; i<departments.length(); i++) {
				try {
					sortedDepartments.add(departments.getJSONObject(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					return (ArrayList<JSONObject>)null;
				}
			}
			
			final Comparator<JSONObject> departmentsComparator =
					new Comparator<JSONObject>() {
				@Override
				public int compare(JSONObject jo1, JSONObject jo2) {
					int i1;
					int i2;
					try {
						i1 = jo1.getInt("sequence");
						i2 = jo2.getInt("sequence");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return 0;
					}
					
					if( i1 > i2) return 1;
					if( i1 < i2) return -1;
					return 0;
				}
			
			};
			
			Collections.sort(sortedDepartments, departmentsComparator);
			
			return sortedDepartments;
		
		}
		private ArrayList<ArrayList<String>> makeChildList(JSONObject data){
			ArrayList<ArrayList<String>>list = new ArrayList<ArrayList<String>>();
			ArrayList<String>subList = null;
			
			JSONArray departments = null;		
			try {
				JSONObject _json = data.getJSONObject("departments");
				 departments = _json.toJSONArray(_json.names());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ArrayList<JSONObject> sortedDepartments = sortDepartments(departments);
			
			ListIterator<JSONObject> li = sortedDepartments.listIterator();
			while(li.hasNext()) {
				subList = new ArrayList<String>();
				//  각 
				JSONObject jo = li.next();
				
				JSONArray subDepartments=null;
				try {
					JSONObject _subJo = jo.getJSONObject("departments");
					subDepartments = _subJo.toJSONArray(_subJo.names());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				ArrayList<JSONObject> subSortedDepartments = sortDepartments(subDepartments);
				
				ListIterator<JSONObject> subLi = subSortedDepartments.listIterator();
				while(subLi.hasNext()) {
					JSONObject _jo = subLi.next();
					try {
						subList.add(_jo.getString("title"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}
				
				list.add(subList);
			}
					
			return list;
		}
		
//		public MemberExpandableListAdapter(Context context, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
//			super();
//			this.inflater = LayoutInflater.from(context);
//			this.groupList = groupList;
//			this.childList = childList;
//		}
		
		@Override
		public String getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}
		
		@Override
		public int getGroupCount() {
			return groupList.size();
		}
		
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v == null) {
				
			}
			return v;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	*/
}
