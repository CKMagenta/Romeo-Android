package kr.go.KNPA.Romeo;

import java.util.ArrayList;
import java.util.List;

import kr.go.KNPA.Romeo.Menu.MenuListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		
		// Fragment�� Programmatically�ϰ� ��ġ�ϱ� ���Ͽ� Fragment Transaction �ν��Ͻ��� �Ŵ����κ��� �޾ƿ´�.
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		
		// ����Ʈ�� ����ϴ� Fragment�̴�.
		mFrag = new MenuListFragment();
		
		// Behind View?? �� ����Ʈ��� ��ȯ�Ѵ�.
		ft.replace(R.id.menu_frame, mFrag);
		ft.commit();		// �߻�!!

		// customize the SlidingMenu(���� �𸣰�����, SlideingMenu�ȿ� ���ǵǾ��ִ�.)
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);	// res/drawble/shadow.xml�� �׶���Ʈ ������ ����ִ�.
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		
		//���߿� NavigationBar�� ����鼭 ����������������. �����ʸ� ���?
		
	}
	
	// Ⱦ��ũ�Ѻ並 �����������.
	public class BasePagerAdapter extends FragmentPagerAdapter {
		// �����׸�Ʈ���� ����ִ� ����Ʈ�� �ϳ� �����ϰ�,
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		// ����¡? Ⱦ��ũ���ϴ� ���ΰ�����.
		private ViewPager mPager;

		// ����Ʈ����.
		public BasePagerAdapter(FragmentManager fm, ViewPager vp) {
			super(fm);
			mPager = vp;				// ��ũ�Ѻ�� �� ����͸� �����Ҷ� ���۷����� �޾Ƽ�,
			mPager.setAdapter(this);	// �����׸�Ʈ���� �ϳ��� Ǫ���ϳ�����.
			for (int i = 0; i < 3; i++) {
				addTab(new MenuListFragment());	// ����????
			}
		}

		public void addTab(Fragment frag) {
			mFragments.add(frag);					// ������ Ǫ�� ���� ��̸� ����!!
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);		// ������ ���� in �迭
		}

		@Override
		public int getCount() {
			return mFragments.size();				// ī��Ʈ.
		}
	}

}